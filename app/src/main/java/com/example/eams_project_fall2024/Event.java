package com.example.eams_project_fall2024;

import java.util.List;

public class Event {
    private String eventId;
    private String eventName;
    private String eventDate;
    private List<Attendee> attendees; // Assuming attendees is a list

    // Default constructor required for Firebase deserialization
    public Event() {}

    // Constructor with parameters
    public Event(String eventId, String eventName, String eventDate, List<Attendee> attendees) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.attendees = attendees;
    }

    // Getters and Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public List<Attendee> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<Attendee> attendees) {
        this.attendees = attendees;
    }

    // Method to approve all attendees
    public void approveAllAttendees() {
        if (attendees != null) {
            for (Attendee attendee : attendees) {
                attendee.setApproved(true); // Assuming Attendee class has an isApproved field
            }
        }
    }
}
