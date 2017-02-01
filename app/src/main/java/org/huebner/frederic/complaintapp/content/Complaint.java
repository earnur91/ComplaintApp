package org.huebner.frederic.complaintapp.content;

/**
 *
 */
public class Complaint {
    // Database column names
    public static final String NAME = "NAME";

    public static final String LOCATION = "LOCATION";

    public static final String COMPLAINT_TEXT = "COMPLAINT_TEXT";

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
}
