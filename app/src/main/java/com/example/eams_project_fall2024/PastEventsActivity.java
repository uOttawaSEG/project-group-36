package com.example.eams_project_fall2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;

public class PastEventsActivity extends AppCompatActivity {
    private LinearLayout containerLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_events);

        db = FirebaseFirestore.getInstance();
        containerLayout = findViewById(R.id.containerLayout);

        loadPastEvents();
    }

    private void loadPastEvents() {
        Date currentDate = new Date();

        db.collection("events")
                .whereLessThan("eventDate", currentDate) // Fetch events in the past
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String eventName = document.getString("title");
                            String eventId = document.getId();
                            addEventItem(eventName, eventId);
                        }
                    } else {
                        Toast.makeText(this, "Failed to load past events.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addEventItem(String eventName, String eventId) {
        // Inflate the event item layout (e.g., event_item.xml) if you are using a custom layout
        View eventView = LayoutInflater.from(this).inflate(R.layout.activity_upcoming_events_attendeeside_item, containerLayout, false);

        // Find the TextView within the inflated layout
        TextView eventNameTextView = eventView.findViewById(R.id.eventName);
        eventNameTextView.setText(eventName);
        eventNameTextView.setContentDescription("Event name: " + eventName);

        // Set up the details button (if any) within the inflated layout
        MaterialButton detailsButton = eventView.findViewById(R.id.detailsButton);
        detailsButton.setOnClickListener(v -> {
            // Handle details action, e.g., open a details activity or dialog
        });

        // Set up the signup list button
        MaterialButton signupListButton = eventView.findViewById(R.id.signupListButton);
        signupListButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignupListActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        });

        // Add the event view to the container layout in the ScrollView
        containerLayout.addView(eventView);
    }
}
