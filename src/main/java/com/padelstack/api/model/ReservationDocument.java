package com.padelstack.api.model;

public class ReservationDocument {
    public String reservationId;
    public String communityId;
    public String userId;
    public String userEmail;
    public String userFullName;
    public String resourceId;
    public String resourceName;
    public String date;
    public String startTime;
    public String endTime;
    public Boolean allDay;
    public String slotLabel;
    public String status;
    public String createdAt;
    public String updatedAt;
    public String cancelledAt;

    public ReservationDocument() {
    }
}
