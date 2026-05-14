package com.padelstack.api.repository;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.padelstack.api.model.ResourceDocument;
import com.padelstack.api.util.FirestoreSupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class ResourceRepository extends BaseFirestoreRepository<ResourceDocument> {

    public ResourceRepository(Firestore firestore) {
        super(firestore, ResourceDocument.class);
    }

    @Override
    protected String collectionName() {
        return "resources";
    }

    @Override
    protected ResourceDocument afterRead(DocumentSnapshot snapshot, ResourceDocument entity) {
        if (entity == null) {
            return null;
        }
        if (entity.resourceId == null || entity.resourceId.isBlank()) {
            entity.resourceId = snapshot.getId();
        }
        return entity;
    }

    public List<ResourceDocument> findActiveByCommunity(String communityId) {
        QuerySnapshot snapshot = FirestoreSupport.await(collection()
                .whereEqualTo("communityId", communityId)
                .whereEqualTo("active", true)
                .get());

        return snapshot.getDocuments().stream()
                .map(doc -> afterRead(doc, doc.toObject(ResourceDocument.class)))
                .filter(Objects::nonNull)
                .toList();
    }
}