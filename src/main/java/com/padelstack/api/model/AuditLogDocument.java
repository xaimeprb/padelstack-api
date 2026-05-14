package com.padelstack.api.model;

import java.util.HashMap;
import java.util.Map;

public class AuditLogDocument {
    public String logId;
    public String action;
    public String entityType;
    public String entityId;
    public String communityId;
    public String actorUid;
    public String actorEmail;
    public String createdAt;
    public Map<String, Object> details = new HashMap<>();

    public AuditLogDocument() {
    }
}
