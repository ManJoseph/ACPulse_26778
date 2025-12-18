package com.acpulse.dto.response;

public class GlobalSearchResponse {
    private String id;
    private String type; // "Room", "Lecturer", "Navigation"
    private String title;
    private String subtitle;
    private Object payload; // Can be a URL (Navigation) or ID (Entities)

    // Default constructor
    public GlobalSearchResponse() {
    }

    public GlobalSearchResponse(String id, String type, String title, String subtitle, Object payload) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.subtitle = subtitle;
        this.payload = payload;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public Object getPayload() { return payload; }
    public void setPayload(Object payload) { this.payload = payload; }
}
