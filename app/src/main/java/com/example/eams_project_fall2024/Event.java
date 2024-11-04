package com.example.eams_project_fall2024;

public class Event {
    private String id;
    private String name;

    public Event(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

