package com.example.eams_project_fall2024;

import android.annotation.SuppressLint;
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

public class ApprovedParticipantActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout participantsLayout;
    private String eventId;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approved_requests);

        db = FirebaseFirestore.getInstance();
        participantsLayout = findViewById(R.id.containerLayout);


        eventId = getIntent().getStringExtra("eventId");
        if (eventId != null && !eventId.isEmpty()) {
            fetchApprovedParticipants();
        } else {
            System.out.println("Error: Event ID is null or empty.");
        }


    }

    private void fetchApprovedParticipants() {
        if (eventId == null || eventId.isEmpty()) return;

        db.collection("events").document(eventId).collection("attendees")
                .whereEqualTo("status", "approved")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        participantsLayout.removeAllViews();  // Clear previous entries
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userId = document.getId();
                                fetchUserDetails(userId);  // Fetch details for each user ID
                            }
                        } else {
                            Toast.makeText(this, "No approved participants found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error fetching documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void fetchUserDetails(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("firstName");  // Ensure this is the correct field key
                        addParticipantCard(name, userId);
                    } else {
                        System.out.println("User not found: " + userId);
                    }
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error fetching user details: " + e.getMessage());
                });
    }


    private void addParticipantCard(String name, String userId) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.approved_request_item, participantsLayout, false);

        TextView nameTextView = cardView.findViewById(R.id.emailTextView);
        nameTextView.setText(name);

        MaterialButton detailsButton = cardView.findViewById(R.id.detailsButton);
        detailsButton.setOnClickListener(v -> {
            // Assuming the User class has a method to fetch and display user details
            User.fetchAndDisplayUserDetails(ApprovedParticipantActivity.this, userId);
        });


        participantsLayout.addView(cardView);
    }


}
