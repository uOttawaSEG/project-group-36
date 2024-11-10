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

public class UpcomingEventsActivityForAttendee extends AppCompatActivity {
    private LinearLayout upcomingContainerLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_events_attendeeside);
        db = FirebaseFirestore.getInstance();
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
            db.collection("events").document(eventId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Boolean isAutoApproval = documentSnapshot.getBoolean("isAutoApproval");
                            if (isAutoApproval != null && isAutoApproval) {
                                registerAttendee(eventId, "approved", "You are registered for the event.");
                            } else {
                                registerAttendee(eventId, "pending", "Your signup request has been sent to the organizer.");
                            }
                        } else {
                            Toast.makeText(this, "Event details not found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to retrieve event details: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        upcomingContainerLayout.addView(eventView);
    }

    private void registerAttendee(String eventId, String status, String message) {
        String attendeeId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> attendee = new HashMap<>();
        attendee.put("attendeeId", attendeeId);
        attendee.put("status", status);

        db.collection("events").document(eventId).collection("attendees").document(attendeeId)
                .set(attendee)
                .addOnSuccessListener(aVoid -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(message)
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                    AlertDialog alert = builder.create();
                    alert.show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to sign up for the event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}

