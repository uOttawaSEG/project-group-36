package com.example.eams_project_fall2024;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;
import java.util.Locale;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UpcomingEventsActivityForAttendee extends AppCompatActivity {

    private LinearLayout upcomingContainerLayout;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_events_attendeeside);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            Log.d("Auth", "User is not authenticated, redirecting to login.");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        upcomingContainerLayout = findViewById(R.id.upcomingContainerLayout);
        loadUpcomingEvents();
    }

    private void loadUpcomingEvents() {
        Date currentDate = new Date();
        db.collection("events")
                .whereGreaterThan("eventDate", currentDate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        upcomingContainerLayout.removeAllViews(); // Clear old views
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String eventName = document.getString("title");
                            String eventId = document.getId();
                            addEventItem(eventName, eventId);
                        }
                    } else {
                        Toast.makeText(this, "Failed to load upcoming events.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addEventItem(String eventName, String eventId) {
        View eventView = LayoutInflater.from(this).inflate(R.layout.activity_upcoming_events_attendeeside_item, upcomingContainerLayout, false);

        TextView eventNameTextView = eventView.findViewById(R.id.eventName);
        eventNameTextView.setText(eventName);

        MaterialButton signupButton = eventView.findViewById(R.id.signupListButton);
        signupButton.setOnClickListener(v -> {
            registerAttendee(eventId, "pending", "Your signup request has been sent to the organizer.");
        });

        MaterialButton detailsButton = eventView.findViewById(R.id.detailsButton);
        detailsButton.setOnClickListener(v -> fetchAndDisplayEventDetails(eventId));

        upcomingContainerLayout.addView(eventView);
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

    private void registerAttendee(String eventId, String status, String message) {
        String attendeeId = auth.getCurrentUser().getUid();

        Log.d("RegisterAttendee", "Attempting to register for event: " + eventId);
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Retrieve the event details
                    Date eventDate = documentSnapshot.getDate("eventDate");
                    Date startTime = documentSnapshot.getDate("startTime");
                    Date endTime = documentSnapshot.getDate("endTime");

                    Log.d("RegisterAttendee", "Retrieved event details: Date=" + eventDate + ", Start=" + startTime + ", End=" + endTime);
                    if (eventDate != null && startTime != null && endTime != null) {
                        checkForConflicts(attendeeId, eventDate, startTime, endTime, () -> {
                            // No conflict: Proceed with registration
                            Map<String, Object> attendee = new HashMap<>();
                            attendee.put("attendeeId", attendeeId);
                            attendee.put("status", status);

                            db.collection("events").document(eventId).collection("attendees").document(attendeeId)
                                    .set(attendee)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("RegisterAttendee", "Successfully registered for the event.");
                                        new AlertDialog.Builder(this)
                                                .setMessage(message)
                                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                                .show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("RegisterAttendee", "Failed to sign up for the event", e);
                                        Toast.makeText(this, "Failed to sign up for the event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        });
                    } else {
                        Log.e("RegisterAttendee", "Event time information is missing.");
                        Toast.makeText(this, "Event time information is missing.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("RegisterAttendee", "Failed to retrieve event details", e);
                    Toast.makeText(this, "Failed to retrieve event details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void checkForConflicts(String attendeeId, Date eventDate, Date startTime, Date endTime, Runnable onSuccess) {
        Log.d("ConflictCheck", "Starting conflict check for AttendeeID=" + attendeeId);

        db.collection("events")
                .whereArrayContains("approvedAttendees", attendeeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ConflictCheck", "Successfully retrieved approved events for attendee.");
                        boolean conflictFound = false;

                        for (DocumentSnapshot document : task.getResult()) {
                            Date existingEventDate = document.getDate("eventDate");
                            Date existingStartTime = document.getDate("startTime");
                            Date existingEndTime = document.getDate("endTime");
                            String eventId = document.getId();
                            String title = document.getString("title");

                            // Log each approved event
                            Log.d("ConflictCheck", String.format(
                                    Locale.getDefault(),
                                    "Approved Event: ID=%s, Title=%s, Date=%s, Start=%s, End=%s",
                                    eventId, title, existingEventDate, existingStartTime, existingEndTime
                            ));

                            // Ensure all required fields are not null
                            if (existingEventDate != null && existingStartTime != null && existingEndTime != null) {
                                // Check for conflicts
                                boolean isSameDate = isSameDay(existingEventDate, eventDate);
                                boolean isOverlapping = (startTime.before(existingEndTime) && endTime.after(existingStartTime));

                                if (isSameDate && isOverlapping) {
                                    Log.d("ConflictCheck", "Conflict detected with EventID=" + eventId);
                                    conflictFound = true;
                                    break;
                                }
                            } else {
                                Log.w("ConflictCheck", "EventID=" + eventId + " has missing date or time fields.");
                            }
                        }

                        if (conflictFound) {
                            showConflictDialog();
                        } else {
                            Log.d("ConflictCheck", "No conflicts found. Proceeding with registration.");
                            onSuccess.run();
                        }
                    } else {
                        Log.e("ConflictCheck", "Error retrieving approved events", task.getException());
                        Toast.makeText(this, "Failed to check for conflicts.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showConflictDialog() {
        Log.d("ConflictDialog", "Conflict dialog shown to user.");
        new AlertDialog.Builder(this)
                .setTitle("Conflict Detected")
                .setMessage("You already have an event at this time.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false; // Cannot compare if either date is null
        }

        java.util.Calendar calendar1 = java.util.Calendar.getInstance();
        java.util.Calendar calendar2 = java.util.Calendar.getInstance();
        calendar1.setTime(date1);
        calendar2.setTime(date2);

        return calendar1.get(java.util.Calendar.YEAR) == calendar2.get(java.util.Calendar.YEAR) &&
                calendar1.get(java.util.Calendar.MONTH) == calendar2.get(java.util.Calendar.MONTH) &&
                calendar1.get(java.util.Calendar.DAY_OF_MONTH) == calendar2.get(java.util.Calendar.DAY_OF_MONTH);
    }
}
