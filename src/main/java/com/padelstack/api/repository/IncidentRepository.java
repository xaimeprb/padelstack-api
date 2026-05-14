package com.padelstack.api.repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.padelstack.api.model.IncidentDocument;
import com.padelstack.api.util.FirestoreSupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IncidentRepository extends BaseFirestoreRepository<IncidentDocument> {

    public IncidentRepository(Firestore firestore) {
        super(firestore, IncidentDocument.class);
    }

    @Override
    protected String collectionName() {
        return "incidents";
    }

    public List<IncidentDocument> findMine(String communityId, String createdByUid) {
        QuerySnapshot snapshot = FirestoreSupport.await(collection()
                .whereEqualTo("communityId", communityId)
                .whereEqualTo("createdByUid", createdByUid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get());
        return snapshot.getDocuments().stream()
                .map(doc -> doc.toObject(IncidentDocument.class))
                .toList();
    }

    public List<IncidentDocument> findAllByCommunity(String communityId) {
        QuerySnapshot snapshot = FirestoreSupport.await(collection()
                .whereEqualTo("communityId", communityId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get());
        return snapshot.getDocuments().stream()
                .map(doc -> doc.toObject(IncidentDocument.class))
                .toList();
    }

    public void upsert(IncidentDocument document) {
        save(document.incidentId, document);
    }
}
