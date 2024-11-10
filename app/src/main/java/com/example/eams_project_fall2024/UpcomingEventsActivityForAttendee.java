package com.example.eams_project_fall2024;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Date;
import java.util.HashMap;
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
        Date currentDate = new Date();

        db.collection("events")
                .whereGreaterThan("eventDate", currentDate) // Fetch events in the future
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Clear any previous event views
                        upcomingContainerLayout.removeAllViews();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String eventName = document.getString("title");
                            String eventId = document.getId();
                            addEventItem(eventName, eventId);
                        }

                        if (task.getResult().isEmpty()) {
                            Toast.makeText(this, "No upcoming events found.", Toast.LENGTH_SHORT).show();
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




    private void acceptEvent(String eventId) {
        // Logic to mark the event as accepted in the database
        db.collection("events").document(eventId).update("status", "accepted")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event " + eventId + " accepted!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to accept event " + eventId, Toast.LENGTH_SHORT).show();
                });
    }
}
