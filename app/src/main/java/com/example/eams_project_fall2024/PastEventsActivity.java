package com.example.eams_project_fall2024;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PastEventsActivity extends AppCompatActivity {
    private LinearLayout containerLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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
        View eventView = LayoutInflater.from(this).inflate(R.layout.activity_past_events_item, containerLayout, false);

        // Find the TextView within the inflated layout
        TextView eventNameTextView = eventView.findViewById(R.id.eventName);
        eventNameTextView.setText(eventName);
        eventNameTextView.setContentDescription("Event name: " + eventName);

        // Set up the details button (if any) within the inflated layout
        MaterialButton detailsButton = eventView.findViewById(R.id.detailsButton);
        detailsButton.setOnClickListener(v -> fetchAndDisplayEventDetails(eventId));

        MaterialButton previousListButton = eventView.findViewById(R.id.participantListButton);
        previousListButton.setOnClickListener(v -> {
            Intent intent = new Intent(PastEventsActivity.this, PastParticipantList.class);
            intent.putExtra("eventId", eventId);  // Pass the event ID to the activity
            startActivity(intent);
        });

        // Add the event view to the container layout in the ScrollView
        containerLayout.addView(eventView);
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

                        new AlertDialog.Builder(PastEventsActivity.this)
                                .setTitle("Event Details")
                                .setMessage(message)
                                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                                .show();
                    } else {
                        new AlertDialog.Builder(PastEventsActivity.this)
                                .setTitle("Event Not Found")
                                .setMessage("Details for the selected event could not be found.")
                                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                                .show();
                    }
                })
                .addOnFailureListener(e -> new AlertDialog.Builder(PastEventsActivity.this)
                        .setTitle("Error")
                        .setMessage("Failed to retrieve event details: " + e.getMessage())
                        .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                        .show());
    }

}
