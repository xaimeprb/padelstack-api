package com.padelstack.api.model;

/**
 * Modelo que representa el documento de statute guardado en Firestore.
 */
public class StatuteDocument {
    public String communityId;
    public String title;
    public String content;
    public Integer version;
    public String updatedAt;
    public String updatedByUid;

    /**
     * Crea una instancia de StatuteDocument.
     */
    public StatuteDocument() {
    }
}
