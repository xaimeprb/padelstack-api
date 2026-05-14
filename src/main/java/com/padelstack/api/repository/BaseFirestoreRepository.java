package com.padelstack.api.repository;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.padelstack.api.util.FirestoreSupport;

import java.util.Optional;

public abstract class BaseFirestoreRepository<T> {

    protected final Firestore firestore;
    private final Class<T> targetType;

    protected BaseFirestoreRepository(Firestore firestore, Class<T> targetType) {
        this.firestore = firestore;
        this.targetType = targetType;
    }

    protected abstract String collectionName();

    protected CollectionReference collection() {
        return firestore.collection(collectionName());
    }

    protected T afterRead(DocumentSnapshot snapshot, T entity) {
        return entity;
    }

    public Optional<T> findById(String id) {
        DocumentSnapshot snapshot = FirestoreSupport.await(collection().document(id).get());
        if (!snapshot.exists()) {
            return Optional.empty();
        }

        T entity = snapshot.toObject(targetType);
        entity = afterRead(snapshot, entity);
        return Optional.ofNullable(entity);
    }

    public void save(String id, Object payload) {
        FirestoreSupport.await(collection().document(id).set(payload));
    }

    public void merge(String id, Object payload) {
        FirestoreSupport.await(collection().document(id).set(payload, SetOptions.merge()));
    }

    public void delete(String id) {
        FirestoreSupport.await(collection().document(id).delete());
    }
}