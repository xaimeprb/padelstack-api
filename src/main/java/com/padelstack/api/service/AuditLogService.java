package com.padelstack.api.service;

import com.padelstack.api.model.AuditLogDocument;
import com.padelstack.api.model.UserDocument;
import com.padelstack.api.repository.AuditLogRepository;
import com.padelstack.api.util.TimeUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String action,
                    String entityType,
                    String entityId,
                    UserDocument actor,
                    Map<String, Object> details) {
        AuditLogDocument log = new AuditLogDocument();
        log.logId = UUID.randomUUID().toString().replace("-", "");
        log.action = action;
        log.entityType = entityType;
        log.entityId = entityId;
        log.communityId = actor.communityId;
        log.actorUid = actor.uid;
        log.actorEmail = actor.email;
        log.createdAt = TimeUtils.nowIsoUtc();
        log.details = details;
        auditLogRepository.upsert(log);
    }
}
