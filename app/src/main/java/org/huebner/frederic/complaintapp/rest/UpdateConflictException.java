package org.huebner.frederic.complaintapp.rest;

import org.huebner.frederic.complaintapp.content.Complaint;

import java.io.IOException;

/**
 *
 */
public class UpdateConflictException extends IOException {

    private final Complaint conflictedEntity;

    public UpdateConflictException(Complaint conflictedEntity) {
        super();
        this.conflictedEntity = conflictedEntity;
    }

    public Complaint getConflictedEntity() {
        return conflictedEntity;
    }
}
