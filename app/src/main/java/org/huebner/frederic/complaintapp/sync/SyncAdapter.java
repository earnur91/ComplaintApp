package org.huebner.frederic.complaintapp.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.util.LongSparseArray;

import org.huebner.frederic.complaintapp.content.Complaint;
import org.huebner.frederic.complaintapp.content.ComplaintContentProvider;
import org.huebner.frederic.complaintapp.content.SyncState;
import org.huebner.frederic.complaintapp.rest.RestClient;
import org.huebner.frederic.complaintapp.rest.UpdateConflictException;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static android.text.TextUtils.join;
import static org.huebner.frederic.complaintapp.content.ComplaintContentProvider.CONTENT_URI;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "SyncAdpater";

    public SyncAdapter(Context context, Boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        // TODO: Implement sync strategy here
        Log.d(TAG, "*---------- Synchronization started ----------*");

        syncCreatedEntities(provider, syncResult);
        syncDeletedEntities(provider, syncResult);


        if (!extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD)) {
            syncBackendToLocalStore(provider, syncResult);
        }

        Log.d(TAG, "*---------- Synchronization finished ----------*");
    }

    private void syncDeletedEntities(ContentProviderClient provider, SyncResult syncResult) {
        try {
            Cursor deletedRecordsCursor = provider.query(CONTENT_URI, null,
                    Complaint.SYNC_STATE + " = ?",
                    new String[]{SyncState.DELETE.name()},
                    Complaint.SERVER_ID);

            for (boolean more = deletedRecordsCursor.moveToFirst(); more; more = deletedRecordsCursor.moveToNext()) {
                Complaint complaint = Complaint.fromCursor(deletedRecordsCursor);
                Long id = complaint.getId();
                Uri localrecordUri = ComplaintContentProvider.getUri(id);

                RestClient.deleteComplaint(complaint);
                provider.delete(localrecordUri, null, null);
            }
        } catch (Exception e) {
            Log.w(TAG, "Error writing deleted records to backend", e);
            syncResult.stats.numIoExceptions++;
        }
    }

    private void syncCreatedEntities(ContentProviderClient contentProvider, SyncResult syncResult) {
        try {
            // get changed entities from local database
            Cursor changedEntitiesCursor = contentProvider.query(
                    CONTENT_URI,
                    null,
                    Complaint.SYNC_STATE
                            + " IN ( '"
                            + join("', '", new SyncState[]{SyncState.CREATE,
                            SyncState.UPDATE}) + "' )", null,
                    Complaint.SERVER_ID);

            // Send each entity to backend server
            for (boolean more = changedEntitiesCursor.moveToFirst(); more; more = changedEntitiesCursor.moveToNext()) {
                Complaint complaint = Complaint.fromCursor(changedEntitiesCursor);
                Long id = complaint.getId();
                Uri localrecordUri = ComplaintContentProvider.getUri(id);

                try {
                    complaint = RestClient.saveComplaint(complaint);
                    // update local entity
                    contentProvider.update(localrecordUri, complaint.toContentValues(), null, null);
                } catch (FileNotFoundException e) {
                    Log.w(TAG, "Updated record was deleted on backend: " + complaint.getServerId());
                    contentProvider.delete(localrecordUri, null, null);
                } catch (UpdateConflictException e) {
                    // handleUpdateConflict(contentProvider, complaint, localrecordUri, e);
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Error writing changed records to backend", e);
            syncResult.stats.numIoExceptions++;
        }
    }

    private void syncBackendToLocalStore(ContentProviderClient contentProviderClient, SyncResult syncResult) {
        try {
            LongSparseArray<Complaint> serverIdIndex = loadAllComplaintsFromBackend();
            Cursor cursor = loadAllComplaintsFromLocalStore(contentProviderClient);

            for (boolean more = cursor.moveToFirst(); more; more = cursor.moveToNext()) {
                processExistingRecord(contentProviderClient, serverIdIndex, cursor);
            }

            // Insert new Complaints from Backend
            for (int i = 0; i < serverIdIndex.size(); i++) {
                contentProviderClient.insert(CONTENT_URI, serverIdIndex.get(serverIdIndex.keyAt(i)).toContentValues());
            }


        } catch (Exception e) {
            Log.w(TAG, "Error synchronizing complaints from backend", e);
            syncResult.stats.numIoExceptions++;
        }
    }

    private LongSparseArray<Complaint> loadAllComplaintsFromBackend() throws IOException, JSONException {
        List<Complaint> complaints = RestClient.getAllComplaintsFromRemote();
        return getIdIndex(complaints);
    }

    private Cursor loadAllComplaintsFromLocalStore(ContentProviderClient contentProviderClient) throws RemoteException {
        return contentProviderClient.query(
                CONTENT_URI,
                new String[]{
                        Complaint.ID,
                        Complaint.SERVER_ID,
                        Complaint.SYNC_STATE},
                null,
                null,
                Complaint.SERVER_ID);
    }

    private static LongSparseArray<Complaint> getIdIndex(List<Complaint> items) {
        LongSparseArray<Complaint> sparseArray = new LongSparseArray<>();
        for (Complaint complaint : items) {
            sparseArray.put(complaint.getServerId(), complaint);
        }
        return sparseArray;
    }

    private void processExistingRecord(ContentProviderClient provider, LongSparseArray<Complaint> serverIdIndex, Cursor cursor) throws RemoteException {

        long serverId = cursor.getLong(cursor.getColumnIndexOrThrow(Complaint.SERVER_ID));
        long internalId = cursor.getLong(cursor.getColumnIndexOrThrow(Complaint.ID));
        String syncState = cursor.getString(cursor.getColumnIndexOrThrow(Complaint.SYNC_STATE));

        Uri uri = ComplaintContentProvider.getUri(internalId);

        if (serverIdIndex.get(serverId) != null) {
            Complaint complaint = serverIdIndex.get(serverId);
            serverIdIndex.remove(serverId);

            // Backend + Local DB w/o changes --> Update
            if (SyncState.NOOP.name().equals(syncState)) {
                provider.update(uri, complaint.toContentValues(), null, null);
            }
        } else if (!SyncState.CREATE.name().equals(syncState)) {
            // Only local DB and not a new record --> Delete
            provider.delete(uri, null, null);
        }
    }
}
