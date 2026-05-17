package com.padelstack.api.repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.padelstack.api.model.IncidentDocument;
import com.padelstack.api.util.FirestoreSupport;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
                .get());
        return mapAndSort(snapshot).stream()
                .filter(incident -> createdByUid.equals(incident.createdByUid))
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
                .get());
        return mapAndSort(snapshot);
    }

    /**
     * Obtiene todas las incidencias sin depender de indices compuestos.
     *
     * @return lista de elementos obtenida.
     */
    public List<IncidentDocument> findAll() {
        QuerySnapshot snapshot = FirestoreSupport.await(collection().get());
        return mapAndSort(snapshot);
    }

    /**
     * Convierte los documentos de Firestore y los ordena por fecha en memoria.
     *
     * @param snapshot resultado devuelto por Firestore.
     * @return lista de incidencias ordenada.
     */
    private List<IncidentDocument> mapAndSort(QuerySnapshot snapshot) {
        return snapshot.getDocuments().stream()
                .map(doc -> doc.toObject(IncidentDocument.class))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(
                        incident -> safeDate(incident.createdAt),
                        Comparator.reverseOrder()
                ))
                .toList();
    }

    /**
     * Devuelve una fecha segura para poder ordenar aunque el campo no exista.
     *
     * @param value fecha leida del documento.
     * @return texto usado para ordenar.
     */
    private String safeDate(String value) {
        return value == null ? "" : value;
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
