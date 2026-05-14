package com.padelstack.api.repository;

import com.google.cloud.firestore.Firestore;
import com.padelstack.api.model.UserDocument;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends BaseFirestoreRepository<UserDocument> {

    public UserRepository(Firestore firestore) {
        super(firestore, UserDocument.class);
    }

    @Override
    protected String collectionName() {
        return "users";
    }

    public void upsert(UserDocument document) {
        save(document.uid, document);
    }
}
