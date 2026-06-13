package com.padelstack.api.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Modelo que representa el documento de audit log guardado en Firestore.
 */
public class AuditLogDocument {
    public String logId;
    public String action;
    public String entityType;
    public String entityId;
    public String communityId;
    public String actorUid;
    public String actorName;
    public String actorEmail;
    public String createdAt;
    public Map<String, Object> details = new HashMap<>();

    /**
     * Crea una instancia de AuditLogDocument.
     */
    public AuditLogDocument() {
    }
}
