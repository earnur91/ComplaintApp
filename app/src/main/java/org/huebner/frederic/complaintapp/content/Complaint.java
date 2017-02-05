package org.huebner.frederic.complaintapp.content;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class Complaint {
    // Database column names
    public static final String ID = "_id";

    public static final String NAME = "NAME";

    public static final String LOCATION = "LOCATION";

    public static final String COMPLAINT_TEXT = "COMPLAINT_TEXT";

    public static final String PROCESSING_STATUS = "PROCESSING_STATUS";

    public static final String TABLE_NAME = "COMPLAINT";

    public static final String SERVER_VERSION = "SERVER_VERSION";

    public static final String CONFLICT_SERVER_VERSION = "CONFLICT_SERVER_VERSION";

    public static final String SERVER_ID = "SERVER_ID";

    public static final String SYNC_STATE = "SYNC_STATE";

    private Long id;

    private Long serverId;

    private Long serverVersion;

    private Long conflictedServerVersion;

    private String name;

    private String location;

    private String complaintText;

    private ProcessingStatus processingStatus;

    private SyncState syncState = SyncState.NOOP;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public Long getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(Long serverVersion) {
        this.serverVersion = serverVersion;
    }

    public Long getConflictedServerVersion() {
        return conflictedServerVersion;
    }

    public void setConflictedServerVersion(Long conflictedServerVersion) {
        this.conflictedServerVersion = conflictedServerVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getComplaintText() {
        return complaintText;
    }

    public void setComplaintText(String complaintText) {
        this.complaintText = complaintText;
    }

    public SyncState getSyncState() {
        return syncState;
    }

    public void setSyncState(SyncState syncState) {
        this.syncState = syncState;
    }

    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
    }

    public static Complaint ComplaintFromJson(JSONObject jsonObject) throws JSONException {
        if (jsonObject == null)
            return null;

        Complaint complaint = new Complaint();

        complaint.setServerId(jsonObject.getLong("id"));
        complaint.setServerVersion(jsonObject.getLong("version"));
        complaint.setSyncState(SyncState.NOOP);
        complaint.setName(jsonObject.getString("name"));
        complaint.setLocation(jsonObject.getString("location"));
        complaint.setComplaintText(jsonObject.getString("complaintText"));
        complaint.setProcessingStatus(ProcessingStatus.valueOf(jsonObject.getString("processingStatus")));

        return complaint;
    }

    public String toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", serverId);
        jsonObject.put("version", serverVersion);
        jsonObject.put("name", name);
        jsonObject.put("location", location);
        jsonObject.put("complaintText", complaintText);
        jsonObject.put("processingStatus", processingStatus);

        return jsonObject.toString();
    }

    /**
     * Converts the complaint entity to ContentValues
     *
     * @return the content values
     */
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(SERVER_ID, serverId);
        values.put(SERVER_VERSION, serverVersion);
        values.put(CONFLICT_SERVER_VERSION, conflictedServerVersion);
        values.put(NAME, name);
        values.put(LOCATION, location);
        values.put(COMPLAINT_TEXT, complaintText);
        values.put(PROCESSING_STATUS, processingStatus.toString());
        values.put(SYNC_STATE, syncState.name());
        return values;
    }

    public static Complaint fromCursor(Cursor cursor) {
        if (cursor == null)
            return null;

        Complaint complaint = new Complaint();

        if (!cursor.isNull(cursor.getColumnIndexOrThrow(SERVER_ID)))
            complaint.setServerId(cursor.getLong(cursor.getColumnIndexOrThrow(SERVER_ID)));
        if (!cursor.isNull(cursor.getColumnIndexOrThrow(SERVER_VERSION)))
            complaint.setServerVersion(cursor.getLong(cursor.getColumnIndexOrThrow(SERVER_VERSION)));
        if (!cursor.isNull(cursor.getColumnIndexOrThrow(CONFLICT_SERVER_VERSION)))
            complaint.setConflictedServerVersion(cursor.getLong(cursor.getColumnIndexOrThrow(CONFLICT_SERVER_VERSION)));

        complaint.setId(cursor.getLong(cursor.getColumnIndexOrThrow(ID)));
        complaint.setName(cursor.getString(cursor.getColumnIndexOrThrow(NAME)));
        complaint.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(LOCATION)));
        complaint.setComplaintText(cursor.getString(cursor.getColumnIndexOrThrow(COMPLAINT_TEXT)));
        complaint.setProcessingStatus(ProcessingStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(PROCESSING_STATUS))));
        complaint.setSyncState(SyncState.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(SYNC_STATE))));

        return complaint;
    }
}
