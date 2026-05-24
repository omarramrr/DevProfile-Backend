package com.devprofileproject.devprofileaast.dto.response;

public class ArchetypeResponse {
    private String name;
    private String description;

    public ArchetypeResponse(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
}