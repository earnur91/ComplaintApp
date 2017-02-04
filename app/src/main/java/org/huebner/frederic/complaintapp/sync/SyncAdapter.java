package org.huebner.frederic.complaintapp.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "SyncAdpater";

    public SyncAdapter(Context context, Boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        // TODO: Implement sync strategy here
        Log.d(TAG, "Synchronization started");

        syncCreatedEntities(provider, syncResult);

        Log.d(TAG, "Synchronization finished");
    }

    private void syncCreatedEntities(ContentProviderClient contentProvider, SyncResult syncResult) {

    }
}
