package org.huebner.frederic.complaintapp.content;

/**
 * Represents the synchronisation state of a specific entity.
 */
public enum SyncState {
    NOOP,
    CREATE,
    UPDATE,
    DELETE,
    CONFLICTED;
}
