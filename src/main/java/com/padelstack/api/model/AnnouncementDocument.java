package com.padelstack.api.model;

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

    public AnnouncementDocument() {
    }
}
