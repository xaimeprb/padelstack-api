package com.padelstack.api.repository;

import com.google.cloud.firestore.Firestore;
import com.padelstack.api.model.StatuteDocument;
import org.springframework.stereotype.Repository;

@Repository
public class StatuteRepository extends BaseFirestoreRepository<StatuteDocument> {

    public StatuteRepository(Firestore firestore) {
        super(firestore, StatuteDocument.class);
    }

    @Override
    protected String collectionName() {
        return "statutes";
    }

    public void upsert(StatuteDocument document) {
        save(document.communityId, document);
    }
}
