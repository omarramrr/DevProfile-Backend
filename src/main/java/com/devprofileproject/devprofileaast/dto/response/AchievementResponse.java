package com.devprofileproject.devprofileaast.dto.response;

public class AchievementResponse {
    private String id;
    private String name;
    private String description;
    private String icon;
    private boolean unlocked;

    public AchievementResponse(String id, String name, String description,
            String icon, boolean unlocked) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.unlocked = unlocked;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
    public boolean isUnlocked() { return unlocked; }
}