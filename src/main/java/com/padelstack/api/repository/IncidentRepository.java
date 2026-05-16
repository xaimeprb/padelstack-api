package com.padelstack.api.repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.padelstack.api.model.IncidentDocument;
import com.padelstack.api.util.FirestoreSupport;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio encargado de acceder a los datos de incident.
 */
@Repository
public class IncidentRepository extends BaseFirestoreRepository<IncidentDocument> {

    /**
     * Crea una instancia de IncidentRepository con las dependencias necesarias.
     *
     * @param firestore valor recibido por el método.
     */
    public IncidentRepository(Firestore firestore) {
        super(firestore, IncidentDocument.class);
    }

    /**
     * Devuelve el nombre de la colección de Firestore usada por el repositorio.
     *
     * @return texto obtenido por el método.
     */
    @Override
    protected String collectionName() {
        return "incidents";
    }

    /**
     * Busca los elementos creados por el usuario actual.
     *
     * @param communityId identificador de la comunidad.
     * @param createdByUid valor recibido por el método.
     * @return lista de elementos obtenida.
     */
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

    /**
     * Busca todos los elementos asociados a una comunidad.
     *
     * @param communityId identificador de la comunidad.
     * @return lista de elementos obtenida.
     */
    public List<IncidentDocument> findAllByCommunity(String communityId) {
        QuerySnapshot snapshot = FirestoreSupport.await(collection()
                .whereEqualTo("communityId", communityId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get());
        return snapshot.getDocuments().stream()
                .map(doc -> doc.toObject(IncidentDocument.class))
                .toList();
    }

    /**
     * Guarda o actualiza el documento indicado.
     *
     * @param document valor recibido por el método.
     */
    public void upsert(IncidentDocument document) {
        save(document.incidentId, document);
    }
}
