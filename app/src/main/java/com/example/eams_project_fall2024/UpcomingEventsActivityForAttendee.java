package com.example.eams_project_fall2024;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UpcomingEventsActivityForAttendee  extends AppCompatActivity {
    private LinearLayout upcomingContainerLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_events_attendeeside);

        // Initialize Firestore and layout elements
        db = FirebaseFirestore.getInstance();
        upcomingContainerLayout = findViewById(R.id.upcomingContainerLayout);

        loadUpcomingEvents();
    }

    private void loadUpcomingEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Date currentDate = new Date();
        db.collection("events")
                .whereGreaterThan("eventDate", currentDate) // Fetch events in the future
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
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

        MaterialButton detailsButton = eventView.findViewById(R.id.detailsButton);
        detailsButton.setOnClickListener(v -> fetchAndDisplayEventDetails(eventId));


        MaterialButton signupListButton = eventView.findViewById(R.id.signupListButton);
        signupListButton.setOnClickListener(v -> {
            // Assume attendeeId and attendeeDetails are obtained from the logged-in user context
            String attendeeId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Map<String, Object> attendee = new HashMap<>();
            attendee.put("attendeeId", attendeeId);
            attendee.put("status", "pending");

            db.collection("events").document(eventId).collection("attendees").document(attendeeId)
                    .set(attendee)
                    .addOnSuccessListener(aVoid -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Your signup request has been sent to the organizer.")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                        AlertDialog alert = builder.create();
                        alert.show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to sign up for the event.", Toast.LENGTH_SHORT).show();
                    });
        });

        upcomingContainerLayout.addView(eventView);
    }

    private void fetchAndDisplayEventDetails(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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

                        new AlertDialog.Builder(UpcomingEventsActivityForAttendee.this)
                                .setTitle("Event Details")
                                .setMessage(message)
                                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                                .show();
                    } else {
                        new AlertDialog.Builder(UpcomingEventsActivityForAttendee.this)
                                .setTitle("Event Not Found")
                                .setMessage("Details for the selected event could not be found.")
                                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                                .show();
                    }
                })
                .addOnFailureListener(e -> new AlertDialog.Builder(UpcomingEventsActivityForAttendee.this)
                        .setTitle("Error")
                        .setMessage("Failed to retrieve event details: " + e.getMessage())
                        .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                        .show());
    }

}
