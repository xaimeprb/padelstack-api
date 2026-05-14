package com.padelstack.api.model;

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

    public ResourceDocument() {
    }
}
