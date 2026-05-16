package com.padelstack.api.repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.padelstack.api.model.ReservationDocument;
import com.padelstack.api.util.FirestoreSupport;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio encargado de acceder a los datos de reservation.
 */
@Repository
public class ReservationRepository extends BaseFirestoreRepository<ReservationDocument> {

    /**
     * Crea una instancia de ReservationRepository con las dependencias necesarias.
     *
     * @param firestore valor recibido por el método.
     */
    public ReservationRepository(Firestore firestore) {
        super(firestore, ReservationDocument.class);
    }

    /**
     * Devuelve el nombre de la colección de Firestore usada por el repositorio.
     *
     * @return texto obtenido por el método.
     */
    @Override
    protected String collectionName() {
        return "reservations";
    }

    /**
     * Busca reservas activas de un recurso en una fecha concreta.
     *
     * @param communityId identificador de la comunidad.
     * @param resourceId identificador del recurso.
     * @param date fecha usada en la operación.
     * @return lista de elementos obtenida.
     */
    public List<ReservationDocument> findActiveByResourceAndDate(String communityId, String resourceId, String date) {
        QuerySnapshot snapshot = FirestoreSupport.await(collection()
                .whereEqualTo("communityId", communityId)
                .whereEqualTo("resourceId", resourceId)
                .whereEqualTo("date", date)
                .whereEqualTo("status", "ACTIVE")
                .orderBy("startTime", Query.Direction.ASCENDING)
                .get());
        return snapshot.getDocuments().stream()
                .map(doc -> doc.toObject(ReservationDocument.class))
                .toList();
    }

    /**
     * Busca reservas de un usuario filtrando por estado.
     *
     * @param communityId identificador de la comunidad.
     * @param userId valor recibido por el método.
     * @param status estado usado para filtrar o actualizar datos.
     * @return lista de elementos obtenida.
     */
    public List<ReservationDocument> findByUserAndStatus(String communityId, String userId, String status) {
        QuerySnapshot snapshot = FirestoreSupport.await(collection()
                .whereEqualTo("communityId", communityId)
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", status)
                .orderBy("date", Query.Direction.DESCENDING)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get());
        return snapshot.getDocuments().stream()
                .map(doc -> doc.toObject(ReservationDocument.class))
                .toList();
    }

    /**
     * Guarda o actualiza el documento indicado.
     *
     * @param document valor recibido por el método.
     */
    public void upsert(ReservationDocument document) {
        save(document.reservationId, document);
    }
}
