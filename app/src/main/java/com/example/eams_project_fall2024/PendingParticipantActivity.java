package com.example.eams_project_fall2024;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class PendingParticipantActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout participantsLayout;
    private String eventId;
    private MaterialButton btnAcceptAll, btnRejectAll;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_pending_participants);

        db = FirebaseFirestore.getInstance();
        participantsLayout = findViewById(R.id.participantsLayout);
        btnAcceptAll = findViewById(R.id.btnAcceptAll);
        btnRejectAll = findViewById(R.id.btnRejectAll);

        eventId = getIntent().getStringExtra("eventId");
        if (eventId != null && !eventId.isEmpty()) {
            fetchPendingParticipants();
        } else {
            System.out.println("Error: Event ID is null or empty.");
        }

        btnAcceptAll.setOnClickListener(view -> updateAllParticipantsStatus("approved"));
        btnRejectAll.setOnClickListener(view -> updateAllParticipantsStatus("rejected"));
    }

    private void fetchPendingParticipants() {
        if (eventId == null || eventId.isEmpty()) return;

        db.collection("events").document(eventId).collection("attendees")
                .whereEqualTo("status", "pending")
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
        View cardView = LayoutInflater.from(this).inflate(R.layout.pending_request_item, participantsLayout, false);

        TextView nameTextView = cardView.findViewById(R.id.emailTextView);
        nameTextView.setText(name);

        MaterialButton acceptButton = cardView.findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(v -> updateParticipantStatus(userId, "approved"));

        MaterialButton rejectButton = cardView.findViewById(R.id.rejectButton);
        rejectButton.setOnClickListener(v -> updateParticipantStatus(userId, "rejected"));

        participantsLayout.addView(cardView);
    }

    private void updateParticipantStatus(String userId, String newStatus) {
        db.collection("events").document(eventId).collection("attendees").document(userId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Status updated to " + newStatus + " for user " + userId);
                    // Refresh the list to reflect changes
                    fetchPendingParticipants();
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error updating status: " + e.getMessage());
                });
    }

    private void updateAllParticipantsStatus(String newStatus) {
        db.collection("events").document(eventId).collection("attendees")
                .whereEqualTo("status", "pending")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            updateParticipantStatus(document.getId(), newStatus);
                        }
                    } else {
                        System.out.println("Error updating all participants: " + task.getException());
                    }
                });
    }
}

