package com.padelstack.api.repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.padelstack.api.model.ReservationDocument;
import com.padelstack.api.util.FirestoreSupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReservationRepository extends BaseFirestoreRepository<ReservationDocument> {

    public ReservationRepository(Firestore firestore) {
        super(firestore, ReservationDocument.class);
    }

    @Override
    protected String collectionName() {
        return "reservations";
    }

    public List<ReservationDocument> findActiveByResourceAndDate(String communityId, String resourceId, String date) {
        QuerySnapshot snapshot = FirestoreSupport.await(collection()
                .whereEqualTo("communityId", communityId)
                .whereEqualTo("resourceId", resourceId)
                .whereEqualTo("date", date)
                .whereEqualTo("status", "ACTIVE")
                .orderBy("startTime", Query.Direction.ASCENDING)
                .get());
        return snapshot.getDocuments().stream()
                .map(doc -> doc.toObject(ReservationDocument.class))
                .toList();
    }

    public List<ReservationDocument> findByUserAndStatus(String communityId, String userId, String status) {
        QuerySnapshot snapshot = FirestoreSupport.await(collection()
                .whereEqualTo("communityId", communityId)
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", status)
                .orderBy("date", Query.Direction.DESCENDING)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get());
        return snapshot.getDocuments().stream()
                .map(doc -> doc.toObject(ReservationDocument.class))
                .toList();
    }

    public void upsert(ReservationDocument document) {
        save(document.reservationId, document);
    }
}
