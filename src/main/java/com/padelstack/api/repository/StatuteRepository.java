package com.padelstack.api.repository;

import com.google.cloud.firestore.Firestore;
import com.padelstack.api.model.StatuteDocument;
import org.springframework.stereotype.Repository;

/**
 * Repositorio encargado de acceder a los datos de statute.
 */
@Repository
public class StatuteRepository extends BaseFirestoreRepository<StatuteDocument> {

    /**
     * Crea una instancia de StatuteRepository con las dependencias necesarias.
     *
     * @param firestore valor recibido por el método.
     */
    public StatuteRepository(Firestore firestore) {
        super(firestore, StatuteDocument.class);
    }

    /**
     * Devuelve el nombre de la colección de Firestore usada por el repositorio.
     *
     * @return texto obtenido por el método.
     */
    @Override
    protected String collectionName() {
        return "statutes";
    }

    /**
     * Guarda o actualiza el documento indicado.
     *
     * @param document valor recibido por el método.
     */
    public void upsert(StatuteDocument document) {
        save(document.communityId, document);
    }
}
