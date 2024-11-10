package com.example.eams_project_fall2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UpcomingEventsActivityForOrganizer extends AppCompatActivity {

    private LinearLayout upcomingContainerLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_events_organizer_side);

        db = FirebaseFirestore.getInstance();
        upcomingContainerLayout = findViewById(R.id.upcomingContainerLayout);

        loadUpcomingEvents();
    }

    private void loadUpcomingEvents() {
        Date currentDate = new Date();
        upcomingContainerLayout.removeAllViews(); // Clear the list before repopulating
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
        View eventView = LayoutInflater.from(this).inflate(R.layout.activity_upcoming_events_organizer_side_item, upcomingContainerLayout, false);

        TextView eventNameTextView = eventView.findViewById(R.id.eventName);
        eventNameTextView.setText(eventName);

        MaterialButton approvedButton = eventView.findViewById(R.id.approvedListButton);
        approvedButton.setOnClickListener(v -> {
            Intent intent = new Intent(UpcomingEventsActivityForOrganizer.this, ApprovedParticipantActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        });


        MaterialButton detailsButton = eventView.findViewById(R.id.detailsButton);
        detailsButton.setOnClickListener(v -> fetchAndDisplayEventDetails(eventId));

        MaterialButton pendingListButton = eventView.findViewById(R.id.pendingListButton); // Assuming this is the button ID in your XML
        pendingListButton.setOnClickListener(v -> {
            Intent intent = new Intent(UpcomingEventsActivityForOrganizer.this, PendingParticipantActivity.class);
            intent.putExtra("eventId", eventId);  // Pass the event ID to the pending participants activity
            startActivity(intent);
        });

        MaterialButton deleteButton = eventView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> confirmAndDeleteEvent(eventId));

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

                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a", Locale.getDefault());
                        String dateStr = (eventDate != null) ? dateFormat.format(eventDate) : "No date provided";

                        String message = "Title: " + title + "\n" +
                                "Description: " + description + "\n" +
                                "Date: " + dateStr + "\n" +
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

    private void confirmAndDeleteEvent(String eventId) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Delete", (dialog, which) -> deleteEvent(eventId))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteEvent(String eventId) {
        db.collection("events").document(eventId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event deleted successfully.", Toast.LENGTH_SHORT).show();
                    loadUpcomingEvents();  // Refresh the list after deletion
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error deleting event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
