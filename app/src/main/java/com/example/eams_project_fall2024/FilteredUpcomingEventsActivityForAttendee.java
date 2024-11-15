package com.example.eams_project_fall2024;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
public class FilteredUpcomingEventsActivityForAttendee extends AppCompatActivity{
    private LinearLayout eventSearchResultContainerLayout;
    private FirebaseFirestore db;
    private EditText eventSearch;
    private Button searchForEventsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_event_search);

        db = FirebaseFirestore.getInstance();
        eventSearchResultContainerLayout = findViewById(R.id.eventSearchResultContainerLayout);
        eventSearch = findViewById(R.id.eventSearch);
        searchForEventsButton = findViewById(R.id.searchForEventsButton);
        searchForEventsButton.setOnClickListener(v -> searchEvents());
    }

    private void searchEvents() {
        String searchQuery = eventSearch.getText().toString().trim().toLowerCase();

        if (searchQuery.isEmpty()) {
            Toast.makeText(this, "Please enter a search term.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Query Firestore for events matching the search query in title or description
        db.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventSearchResultContainerLayout.removeAllViews(); // Clear previous results
                        boolean foundMatch = false;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String description = document.getString("description");

                            // Check if the title or description matches the search query
                            if ((title != null && title.toLowerCase().contains(searchQuery)) ||
                                    (description != null && description.toLowerCase().contains(searchQuery))) {
                                foundMatch = true;
                                addEventItem(title, document.getId());
                            }
                        }

                        if (!foundMatch) {
                            Toast.makeText(this, "No matching events found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to search events.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addEventItem(String eventName, String eventId) {
        View eventView = LayoutInflater.from(this).inflate(R.layout.activity_upcoming_events_attendeeside_item, eventSearchResultContainerLayout, false);
        TextView eventNameTextView = eventView.findViewById(R.id.eventName);
        eventNameTextView.setText(eventName);

        MaterialButton signupButton = eventView.findViewById(R.id.signupListButton);
        signupButton.setOnClickListener(v -> {
            db.collection("events").document(eventId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Boolean isAutoApproval = documentSnapshot.getBoolean("isAutoApproval");
                            if (isAutoApproval != null && isAutoApproval) {
                                registerAttendee(eventId, "approved", "You are registered for the event.", eventView);
                            } else {
                                registerAttendee(eventId, "pending", "Your signup request has been sent to the organizer.", eventView);
                            }
                        } else {
                            Toast.makeText(this, "Event details not found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to retrieve event details: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        MaterialButton detailsButton = eventView.findViewById(R.id.detailsButton);
        detailsButton.setOnClickListener(v -> fetchAndDisplayEventDetails(eventId));

        eventSearchResultContainerLayout.addView(eventView);
    }

    // this method is triggered when user clicks details button
    private void fetchAndDisplayEventDetails(String eventId) {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String title = documentSnapshot.getString("title");
                        String description = documentSnapshot.getString("description");
                        Date eventDate = documentSnapshot.getDate("eventDate");
                        String address = documentSnapshot.getString("address");

                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a", Locale.getDefault());
                        String dateStr = (eventDate != null) ? dateFormat.format(eventDate) : "No date provided";

                        String message = "Title: " + title + "\n" +
                                "Description: " + description + "\n" +
                                "Date: " + dateStr + "\n" +
                                "Location: " + address;

                        new androidx.appcompat.app.AlertDialog.Builder(this)
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

    //this method is triggered when user clicks sign up button
    private void registerAttendee(String eventId, String status, String message, View eventView) {
        // Reuse the method from the original code for registering attendees
        String attendeeId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Data to add to the event's attendees list
        Map<String, Object> attendeeData = new HashMap<>();
        attendeeData.put("attendeeId", attendeeId);
        attendeeData.put("status", status);

        // Data to add to the attendee's registered events list
        Map<String, Object> registeredEventData = new HashMap<>();
        registeredEventData.put("eventId", eventId);
        registeredEventData.put("status", status);

        // 1. Add attendee to the event's attendees sub collection
        db.collection("events").document(eventId).collection("attendees").document(attendeeId)
                .set(attendeeData)
                .addOnSuccessListener(aVoid -> {
                    // 2. Add event to the attendee's registered events list
                    db.collection("users").document(attendeeId)
                            .collection("registeredEvents").document(eventId)
                            .set(registeredEventData)
                            .addOnSuccessListener(unused -> {
                                // Show confirmation message after adding to both locations
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setMessage(message)
                                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                                AlertDialog alert = builder.create();
                                alert.show();

                                // Remove the event view from the layout
                                eventSearchResultContainerLayout.removeView(eventView);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("RegisterAttendee", "Failed to add event to attendee's list: " + e.getMessage());
                                Toast.makeText(this, "Failed to add event to your registered events list.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("RegisterAttendee", "Failed to sign up for the event: " + e.getMessage());
                    Toast.makeText(this, "Failed to sign up for the event.", Toast.LENGTH_SHORT).show();
                });
    }
}


