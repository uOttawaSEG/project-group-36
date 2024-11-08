package com.example.eams_project_fall2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.appcompat.app.AppCompatActivity;


public class UpcomingEventsActivity extends AppCompatActivity {
    private LinearLayout upcomingContainerLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_events);
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
        // Inflate the event item layout (e.g., event_item.xml)
        View eventView = LayoutInflater.from(this).inflate(R.layout.event_item, upcomingContainerLayout, false);

        // Set the event name
        TextView eventNameTextView = eventView.findViewById(R.id.eventName);
        eventNameTextView.setText(eventName);

        // Set up the details button
        MaterialButton detailsButton = eventView.findViewById(R.id.detailsButton);
        detailsButton.setOnClickListener(v -> {
            // Handle details action
        });

        // Set up the signup list button
        MaterialButton signupListButton = eventView.findViewById(R.id.signupListButton);
        signupListButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignupListActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        });

        // Set up the accept button
        MaterialButton acceptButton = eventView.findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(v -> {
            // Handle the acceptance action
            acceptEvent(eventId);
        });

        // Add the event view to the container layout
        upcomingContainerLayout.addView(eventView);
    }

    // Method to handle acceptance logic
    private void acceptEvent(String eventId) {
        // Logic to mark the event as accepted in the database
        Toast.makeText(this, "Event " + eventId + " accepted!", Toast.LENGTH_SHORT).show();
    }


}

