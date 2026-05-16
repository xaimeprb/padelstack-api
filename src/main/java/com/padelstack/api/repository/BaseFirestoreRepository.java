package com.padelstack.api.repository;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.padelstack.api.util.FirestoreSupport;

import java.util.Optional;

/**
 * Repositorio encargado de acceder a los datos de base firestore.
 */
public abstract class BaseFirestoreRepository<T> {

    protected final Firestore firestore;
    private final Class<T> targetType;

    /**
     * Crea una instancia de BaseFirestoreRepository con las dependencias necesarias.
     *
     * @param firestore valor recibido por el método.
     * @param targetType valor recibido por el método.
     */
    protected BaseFirestoreRepository(Firestore firestore, Class<T> targetType) {
        this.firestore = firestore;
        this.targetType = targetType;
    }

    /**
     * Devuelve el nombre de la colección de Firestore usada por el repositorio.
     *
     * @return texto obtenido por el método.
     */
    protected abstract String collectionName();

    /**
     * Gestiona la operación collection.
     *
     * @return resultado de la operación.
     */
    protected CollectionReference collection() {
        return firestore.collection(collectionName());
    }

    /**
     * Ajusta un documento leído desde Firestore antes de devolverlo.
     *
     * @param snapshot valor recibido por el método.
     * @param entity valor recibido por el método.
     * @return resultado devuelto por la operación.
     */
    protected T afterRead(DocumentSnapshot snapshot, T entity) {
        return entity;
    }

    /**
     * Busca un documento por su identificador.
     *
     * @param id identificador del elemento.
     * @return resultado encontrado o vacío si no existe.
     */
    public Optional<T> findById(String id) {
        DocumentSnapshot snapshot = FirestoreSupport.await(collection().document(id).get());
        if (!snapshot.exists()) {
            return Optional.empty();
        }

        T entity = snapshot.toObject(targetType);
        entity = afterRead(snapshot, entity);
        return Optional.ofNullable(entity);
    }

    /**
     * Gestiona la operación save.
     *
     * @param id identificador del elemento.
     * @param payload valor recibido por el método.
     */
    public void save(String id, Object payload) {
        FirestoreSupport.await(collection().document(id).set(payload));
    }

    /**
     * Gestiona la operación merge.
     *
     * @param id identificador del elemento.
     * @param payload valor recibido por el método.
     */
    public void merge(String id, Object payload) {
        FirestoreSupport.await(collection().document(id).set(payload, SetOptions.merge()));
    }

    /**
     * Elimina o cancela el registro solicitado si el usuario tiene permisos.
     *
     * @param id identificador del elemento.
     */
    public void delete(String id) {
        FirestoreSupport.await(collection().document(id).delete());
    }
}