package com.padelstack.api.repository;

import com.google.cloud.firestore.Firestore;
import com.padelstack.api.model.AuditLogDocument;
import org.springframework.stereotype.Repository;

@Repository
public class AuditLogRepository extends BaseFirestoreRepository<AuditLogDocument> {

    public AuditLogRepository(Firestore firestore) {
        super(firestore, AuditLogDocument.class);
    }

    @Override
    protected String collectionName() {
        return "audit_logs";
    }

    public void upsert(AuditLogDocument document) {
        save(document.logId, document);
    }
}
