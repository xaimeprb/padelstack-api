package com.padelstack.api.model;

/**
 * Modelo que representa el documento de announcement guardado en Firestore.
 */
public class AnnouncementDocument {
    public String announcementId;
    public String communityId;
    public String title;
    public String content;
    public Boolean visible = true;
    public String publishedAt;
    public String createdByUid;
    public String createdByName;
    public String updatedAt;

    /**
     * Crea una instancia de AnnouncementDocument.
     */
    public AnnouncementDocument() {
    }
}
