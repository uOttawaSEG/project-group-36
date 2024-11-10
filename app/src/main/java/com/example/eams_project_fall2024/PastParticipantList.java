package com.example.eams_project_fall2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class PastParticipantList extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout participantsLayout;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approved_requests);

        eventId = getIntent().getStringExtra("eventId");
        db = FirebaseFirestore.getInstance();
        participantsLayout = findViewById(R.id.containerLayout);  // Ensure this ID exists in approved_requests.xml

        fetchApprovedParticipants();
    }

    private void fetchApprovedParticipants() {
        if (eventId == null || eventId.isEmpty()) return;

        db.collection("events").document(eventId).collection("attendees")
                .whereEqualTo("status", "approved")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        participantsLayout.removeAllViews();  // Clear previous entries
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String userId = document.getId();
                            fetchUserDetails(userId);  // Fetch details for each user ID
                        }
                    } else {
                        System.out.println("Error fetching documents: " + task.getException());
                    }
                });
    }

    private void fetchUserDetails(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("firstName");  // Use the correct field key
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
            User.fetchAndDisplayUserDetails(PastParticipantList.this, userId);
        });

        participantsLayout.addView(cardView);


    }
}
