package com.padelstack.api.repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.padelstack.api.model.CommunityDocument;
import com.padelstack.api.util.FirestoreSupport;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio encargado de acceder a los datos de community.
 */
@Repository
public class CommunityRepository extends BaseFirestoreRepository<CommunityDocument> {

    /**
     * Crea una instancia de CommunityRepository con las dependencias necesarias.
     *
     * @param firestore valor recibido por el método.
     */
    public CommunityRepository(Firestore firestore) {
        super(firestore, CommunityDocument.class);
    }

    /**
     * Devuelve el nombre de la colección de Firestore usada por el repositorio.
     *
     * @return texto obtenido por el método.
     */
    @Override
    protected String collectionName() {
        return "communities";
    }

    /**
     * Obtiene todos los documentos activos.
     *
     * @return lista de elementos obtenida.
     */
    public List<CommunityDocument> findAllActive() {
        QuerySnapshot snapshot = FirestoreSupport.await(collection()
                .whereEqualTo("active", true)
                .get());
        return snapshot.getDocuments().stream()
                .map(doc -> doc.toObject(CommunityDocument.class))
                .toList();
    }

    /**
     * Obtiene todos los documentos disponibles.
     *
     * @return lista de elementos obtenida.
     */
    public List<CommunityDocument> findAll() {
        QuerySnapshot snapshot = FirestoreSupport.await(collection().get());
        return snapshot.getDocuments().stream()
                .map(doc -> doc.toObject(CommunityDocument.class))
                .toList();
    }

    /**
     * Guarda o actualiza el documento indicado.
     *
     * @param document valor recibido por el método.
     */
    public void upsert(CommunityDocument document) {
        save(document.communityId, document);
    }
}
