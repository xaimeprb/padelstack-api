package com.padelstack.api.repository;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.padelstack.api.model.ResourceDocument;
import com.padelstack.api.util.FirestoreSupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * Repositorio encargado de acceder a los datos de resource.
 */
@Repository
public class ResourceRepository extends BaseFirestoreRepository<ResourceDocument> {

    /**
     * Crea una instancia de ResourceRepository con las dependencias necesarias.
     *
     * @param firestore valor recibido por el método.
     */
    public ResourceRepository(Firestore firestore) {
        super(firestore, ResourceDocument.class);
    }

    /**
     * Devuelve el nombre de la colección de Firestore usada por el repositorio.
     *
     * @return texto obtenido por el método.
     */
    @Override
    protected String collectionName() {
        return "resources";
    }

    /**
     * Ajusta un documento leído desde Firestore antes de devolverlo.
     *
     * @param snapshot valor recibido por el método.
     * @param entity valor recibido por el método.
     * @return resultado de la operación.
     */
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

    /**
     * Obtiene los recursos activos de una comunidad.
     *
     * @param communityId identificador de la comunidad.
     * @return lista de elementos obtenida.
     */
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