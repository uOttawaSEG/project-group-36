package com.example.eams_project_fall2024;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public class AttendeeRegisteredEventsActivity extends AppCompatActivity {

    private LinearLayout registeredContainerLayout;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_registered_events);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        registeredContainerLayout = findViewById(R.id.registeredContainerLayout);

        loadRegisteredEvents();
    }

    private void loadRegisteredEvents() {
        String attendeeId = auth.getCurrentUser().getUid();
        AtomicBoolean hasEvents = new AtomicBoolean(false); // Use AtomicBoolean for mutability

        Date currentDate = new Date(); // Current date and time

        db.collection("events")
                .whereGreaterThan("eventDate", currentDate) // Only fetch events with dates in the future
                .orderBy("eventDate", Query.Direction.ASCENDING) // Order by eventDate in ascending order
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        registeredContainerLayout.removeAllViews(); // Clear previous list

                        for (QueryDocumentSnapshot eventDocument : task.getResult()) {
                            String eventId = eventDocument.getId();

                            // Check if this attendee exists in the event's attendees subcollection
                            DocumentReference attendeeDocRef = db.collection("events")
                                    .document(eventId)
                                    .collection("attendees")
                                    .document(attendeeId);

                            attendeeDocRef.get().addOnSuccessListener(attendeeSnapshot -> {
                                if (attendeeSnapshot.exists()) {
                                    hasEvents.set(true); // Mark that at least one event is found
                                    String eventName = eventDocument.getString("title");
                                    addRegisteredEventItem(eventName, eventId);
                                }
                            }).addOnFailureListener(e -> Log.e("LoadEvents", "Failed to check attendee registration: " + e.getMessage()));
                        }

                        // Check after processing all events
                        if (!hasEvents.get()) {
                            Toast.makeText(this, "No registered events found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("LoadEvents", "Error loading events: ", task.getException());
                        Toast.makeText(this, "Failed to load registered events.", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void addRegisteredEventItem(String eventName, String eventId) {
        View eventView = LayoutInflater.from(this).inflate(R.layout.attendee_registered_events_item, registeredContainerLayout, false);

        TextView eventNameTextView = eventView.findViewById(R.id.emailTextView);
        eventNameTextView.setText(eventName);

        MaterialButton detailsButton = eventView.findViewById(R.id.detailsButton);
        detailsButton.setOnClickListener(v -> fetchAndDisplayEventDetails(eventId));

        MaterialButton statusButton = eventView.findViewById(R.id.statusButton);
        statusButton.setOnClickListener(v -> fetchAndDisplayRegistrationStatus(eventId));

        MaterialButton cancelButton = eventView.findViewById(R.id.CancelButton);
        cancelButton.setOnClickListener(v -> cancelRegistration(eventId));

        registeredContainerLayout.addView(eventView);
    }

    private void fetchAndDisplayEventDetails(String eventId) {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String title = documentSnapshot.getString("title");
                        String description = documentSnapshot.getString("description");
                        Date eventDate = documentSnapshot.getDate("eventDate");
                        String address = documentSnapshot.getString("address");

                        String message = "Title: " + title + "\n" +
                                "Description: " + description + "\n" +
                                "Date: " + eventDate + "\n" +
                                "Location: " + address;

                        new AlertDialog.Builder(this)
                                .setTitle("Event Details")
                                .setMessage(message)
                                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                                .show();
                    } else {
                        Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to retrieve event details: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fetchAndDisplayRegistrationStatus(String eventId) {
        String attendeeId = auth.getCurrentUser().getUid();
        db.collection("events").document(eventId).collection("attendees").document(attendeeId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String status = documentSnapshot.getString("status");
                        new AlertDialog.Builder(this)
                                .setTitle("Registration Status")
                                .setMessage("Your registration status is: " + (status != null ? status : "Unknown"))
                                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                                .show();
                    } else {
                        Toast.makeText(this, "Status not found for the event.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to retrieve status: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void cancelRegistration(String eventId) {
        String attendeeId = auth.getCurrentUser().getUid();

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Date eventDate = documentSnapshot.getDate("eventDate");
                    if (eventDate != null) {
                        long timeDifference = eventDate.getTime() - System.currentTimeMillis();
                        long hoursDifference = timeDifference / (1000 * 60 * 60);

                        if (hoursDifference >= 24) {
                            // Remove the attendee's document from the attendees subcollection
                            db.collection("events").document(eventId)
                                    .collection("attendees").document(attendeeId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Registration canceled successfully.", Toast.LENGTH_SHORT).show();
                                        loadRegisteredEvents(); // Refresh list
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to cancel registration: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(this, "Cannot cancel. The event is within the next 24 hours.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Event date information is missing.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to retrieve event details: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}



