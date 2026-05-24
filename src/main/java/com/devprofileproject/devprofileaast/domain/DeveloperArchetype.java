package com.devprofileproject.devprofileaast.domain;

public enum DeveloperArchetype {
    THE_ARCHITECT("The Architect", "Excels at code quality and complexity"),
    THE_GRINDER("The Grinder", "Unmatched activity and contribution consistency"),
    THE_SPECIALIST("The Specialist", "Deep tech alignment and domain expertise"),
    THE_PRESENTER("The Presenter", "Strong resume and professional presentation"),
    THE_OPEN_SOURCE_ADVOCATE("The Open Source Advocate", "Community-focused with high star counts"),
    THE_BALANCED_DEV("The Balanced Dev", "Well-rounded across all dimensions");

    private final String displayName;
    private final String description;

    DeveloperArchetype(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}