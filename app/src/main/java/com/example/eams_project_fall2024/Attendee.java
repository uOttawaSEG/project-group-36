package com.example.eams_project_fall2024;

public class Attendee {
    private String name;
    private String status;

    public Attendee(String name) {
        this.name = name;
        this.status = "pending"; // default status
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }
}
