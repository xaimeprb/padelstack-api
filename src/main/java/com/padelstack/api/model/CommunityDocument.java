package com.padelstack.api.model;

import java.util.ArrayList;
import java.util.List;

public class CommunityDocument {
    public String communityId;
    public String name;
    public List<String> units = new ArrayList<>();
    public Boolean active = true;

    public CommunityDocument() {
    }
}
