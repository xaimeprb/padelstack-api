package com.padelstack.api.model;

/**
 * Modelo que representa el documento de user guardado en Firestore.
 */
public class UserDocument {
    public String uid;
    public String email;
    public String username;
    public String firstName;
    public String lastName;
    public String fullName;
    public String phone;
    public String communityId;
    public String communityName;
    public String unitDisplay;
    public String role;
    public Boolean active = true;
    public String createdAt;
    public String updatedAt;

    /**
     * Crea una instancia de UserDocument.
     */
    public UserDocument() {
    }
}
