package com.example.eams_project_fall2024;

public class Attendee extends User {
    public Attendee(String firstName, String lastName, String email, String password, String phoneNumber, String address) {
        super(firstName, lastName, email, password, phoneNumber, address);
    }

    public void requestEventRegistration() {}

    public void cancelEventRegistration() {}

    public void viewRegisteredEvents() {}

    public void searchAvailableEvents() {}
}
