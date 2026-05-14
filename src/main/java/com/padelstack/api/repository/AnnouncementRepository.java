package com.padelstack.api.repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.padelstack.api.model.AnnouncementDocument;
import com.padelstack.api.util.FirestoreSupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AnnouncementRepository extends BaseFirestoreRepository<AnnouncementDocument> {

    public AnnouncementRepository(Firestore firestore) {
        super(firestore, AnnouncementDocument.class);
    }

    @Override
    protected String collectionName() {
        return "announcements";
    }

    public List<AnnouncementDocument> findVisibleByCommunity(String communityId) {
        QuerySnapshot snapshot = FirestoreSupport.await(collection()
                .whereEqualTo("communityId", communityId)
                .whereEqualTo("visible", true)
                .orderBy("publishedAt", Query.Direction.DESCENDING)
                .get());
        return snapshot.getDocuments().stream()
                .map(doc -> doc.toObject(AnnouncementDocument.class))
                .toList();
    }

    public void upsert(AnnouncementDocument document) {
        save(document.announcementId, document);
    }
}
