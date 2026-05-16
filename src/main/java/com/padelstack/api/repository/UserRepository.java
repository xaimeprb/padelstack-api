package com.padelstack.api.repository;

import com.google.cloud.firestore.Firestore;
import com.padelstack.api.model.UserDocument;
import org.springframework.stereotype.Repository;

/**
 * Repositorio encargado de acceder a los datos de user.
 */
@Repository
public class UserRepository extends BaseFirestoreRepository<UserDocument> {

    /**
     * Crea una instancia de UserRepository con las dependencias necesarias.
     *
     * @param firestore valor recibido por el método.
     */
    public UserRepository(Firestore firestore) {
        super(firestore, UserDocument.class);
    }

    /**
     * Devuelve el nombre de la colección de Firestore usada por el repositorio.
     *
     * @return texto obtenido por el método.
     */
    @Override
    protected String collectionName() {
        return "users";
    }

    /**
     * Guarda o actualiza el documento indicado.
     *
     * @param document valor recibido por el método.
     */
    public void upsert(UserDocument document) {
        save(document.uid, document);
    }
}
