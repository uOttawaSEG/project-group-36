package com.example.eams_project_fall2024;

import android.content.Context;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;

public class Organizer extends User {
    public Organizer(String firstName, String lastName, String email, String password, String phoneNumber, String address) {
        super(firstName, lastName, email, password, phoneNumber, address);
    }

    public void createEvent() {
        // Implementation for creating an event
    }

    public void approveEventRegistration() {
        // Implementation for approving an event registration
    }

    public void rejectEventRegistration() {
        // Implementation for rejecting an event registration
    }

    public void viewEventAttendees() {
        // Implementation for viewing event attendees
    }
}
