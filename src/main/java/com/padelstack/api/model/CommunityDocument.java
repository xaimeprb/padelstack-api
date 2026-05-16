package com.padelstack.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo que representa el documento de community guardado en Firestore.
 */
public class CommunityDocument {
    public String communityId;
    public String name;
    public List<String> units = new ArrayList<>();
    public Boolean active = true;

    /**
     * Crea una instancia de CommunityDocument.
     */
    public CommunityDocument() {
    }
}
