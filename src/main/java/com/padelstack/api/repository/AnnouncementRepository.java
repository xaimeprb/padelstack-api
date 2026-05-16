package com.padelstack.api.repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.padelstack.api.model.AnnouncementDocument;
import com.padelstack.api.util.FirestoreSupport;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio encargado de acceder a los datos de announcement.
 */
@Repository
public class AnnouncementRepository extends BaseFirestoreRepository<AnnouncementDocument> {

    /**
     * Crea una instancia de AnnouncementRepository con las dependencias necesarias.
     *
     * @param firestore valor recibido por el método.
     */
    public AnnouncementRepository(Firestore firestore) {
        super(firestore, AnnouncementDocument.class);
    }

    /**
     * Devuelve el nombre de la colección de Firestore usada por el repositorio.
     *
     * @return texto obtenido por el método.
     */
    @Override
    protected String collectionName() {
        return "announcements";
    }

    /**
     * Busca visible by community.
     *
     * @param communityId identificador de la comunidad.
     * @return lista de elementos obtenida.
     */
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

    /**
     * Guarda o actualiza el documento indicado.
     *
     * @param document valor recibido por el método.
     */
    public void upsert(AnnouncementDocument document) {
        save(document.announcementId, document);
    }
}
