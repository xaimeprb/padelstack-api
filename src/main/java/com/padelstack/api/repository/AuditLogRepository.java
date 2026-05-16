package com.padelstack.api.repository;

import com.google.cloud.firestore.Firestore;
import com.padelstack.api.model.AuditLogDocument;
import org.springframework.stereotype.Repository;

/**
 * Repositorio encargado de acceder a los datos de audit log.
 */
@Repository
public class AuditLogRepository extends BaseFirestoreRepository<AuditLogDocument> {

    /**
     * Crea una instancia de AuditLogRepository con las dependencias necesarias.
     *
     * @param firestore valor recibido por el método.
     */
    public AuditLogRepository(Firestore firestore) {
        super(firestore, AuditLogDocument.class);
    }

    /**
     * Devuelve el nombre de la colección de Firestore usada por el repositorio.
     *
     * @return texto obtenido por el método.
     */
    @Override
    protected String collectionName() {
        return "audit_logs";
    }

    /**
     * Guarda o actualiza el documento indicado.
     *
     * @param document valor recibido por el método.
     */
    public void upsert(AuditLogDocument document) {
        save(document.logId, document);
    }
}
