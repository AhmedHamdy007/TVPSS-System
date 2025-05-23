package com.example.model;

public enum SchoolVersionLevel {
    VERSION_1("Version 1", "Basic"),
    VERSION_2("Version 2", "Standard"),
    VERSION_3("Version 3", "Advanced"),
    VERSION_4("Version 4", "Professional");

    private final String displayName;
    private final String description;

    SchoolVersionLevel(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}