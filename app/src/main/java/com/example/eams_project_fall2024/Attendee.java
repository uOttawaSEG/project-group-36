package com.example.eams_project_fall2024;

public class Attendee {
    private String name;
    private boolean isApproved;

    public Attendee() {} // Default constructor for Firebase

    public Attendee(String name, boolean isApproved) {
        this.name = name;
        this.isApproved = isApproved;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}
