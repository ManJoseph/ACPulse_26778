package com.acpulse.dto.response;

public class GlobalSearchResponse {
    private String id;
    private String type; // e.g., "Room", "Lecturer"
    private String title;
    private String description;
    private String path; // e.g., "/rooms/1", "/lecturers/abc"

    // Default constructor for JSON deserialization
    public GlobalSearchResponse() {
    }

    public GlobalSearchResponse(String id, String type, String title, String description, String path) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.path = path;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPath() {
        return path;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
