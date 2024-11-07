package com.example.eams_project_fall2024;

import java.util.List;

public class Event {
    private String eventId;
    private String eventName;
    private String eventDate;
    private List<Attendee> attendees;

    public Event() {}

    public Event(String eventId, String eventName, String eventDate, List<Attendee> attendees) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.attendees = attendees;
    }

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

}
