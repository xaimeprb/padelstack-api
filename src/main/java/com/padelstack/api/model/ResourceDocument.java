package com.padelstack.api.model;

/**
 * Modelo que representa el documento de resource guardado en Firestore.
 */
public class ResourceDocument {
    public String resourceId;
    public String communityId;
    public String name;
    public String type;
    public String reservationMode;
    public Integer slotMinutes;
    public String openTime;
    public String closeTime;
    public String rulesText;
    public Boolean active = true;

    /**
     * Crea una instancia de ResourceDocument.
     */
    public ResourceDocument() {
    }
}
