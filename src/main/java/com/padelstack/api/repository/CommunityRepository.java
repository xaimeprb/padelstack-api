package com.padelstack.api.repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.padelstack.api.model.CommunityDocument;
import com.padelstack.api.util.FirestoreSupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommunityRepository extends BaseFirestoreRepository<CommunityDocument> {

    public CommunityRepository(Firestore firestore) {
        super(firestore, CommunityDocument.class);
    }

    @Override
    protected String collectionName() {
        return "communities";
    }

    public List<CommunityDocument> findAllActive() {
        QuerySnapshot snapshot = FirestoreSupport.await(collection()
                .whereEqualTo("active", true)
                .get());
        return snapshot.getDocuments().stream()
                .map(doc -> doc.toObject(CommunityDocument.class))
                .toList();
    }

    public List<CommunityDocument> findAll() {
        QuerySnapshot snapshot = FirestoreSupport.await(collection().get());
        return snapshot.getDocuments().stream()
                .map(doc -> doc.toObject(CommunityDocument.class))
                .toList();
    }

    public void upsert(CommunityDocument document) {
        save(document.communityId, document);
    }
}
