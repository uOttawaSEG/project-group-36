package com.example.eams_project_fall2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Date;

public class UpcomingEventsActivity extends AppCompatActivity {
    private LinearLayout upcomingContainerLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_events);

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
        // Inflate the event item layout (e.g., event_item.xml)
        View eventView = LayoutInflater.from(this).inflate(R.layout.event_item, upcomingContainerLayout, false);

        // Set the event name
        TextView eventNameTextView = eventView.findViewById(R.id.eventName);
        eventNameTextView.setText(eventName);

        // Set up the details button
        MaterialButton detailsButton = eventView.findViewById(R.id.detailsButton);
        detailsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Details for event: " + eventName, Toast.LENGTH_SHORT).show();
            // Handle additional details action here
        });

        // Set up the signup list button
        MaterialButton signupListButton = eventView.findViewById(R.id.signupListButton);
        signupListButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignupListActivity.class);
            intent.putExtra("eventId", eventId);
            intent.putExtra("eventName", eventName); // Pass the event name as well
            startActivity(intent);
        });

        // Optional: Set up the accept button if exists
        MaterialButton acceptButton = eventView.findViewById(R.id.acceptButton);
        if (acceptButton != null) {
            acceptButton.setOnClickListener(v -> {
                acceptEvent(eventId);
            });
        }

        // Add the event view to the container layout
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
