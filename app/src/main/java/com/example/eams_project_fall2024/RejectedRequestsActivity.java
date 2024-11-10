package com.example.eams_project_fall2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.Map;

public class RejectedRequestsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout containerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.rejected_requests);

        getWindow().setStatusBarColor(android.graphics.Color.rgb(30, 30, 30));
        getWindow().setNavigationBarColor(android.graphics.Color.rgb(30, 30, 30));

        db = FirebaseFirestore.getInstance();
        containerLayout = findViewById(R.id.containerLayout);

        fetchRejectedUsers();
    }

    private void fetchRejectedUsers() {
        db.collection("users")
                .whereEqualTo("status", "rejected")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String email = document.getString("email");
                            String documentId = document.getId(); // Get document ID for future updates

                            if (email != null) {
                                addUserCard(email, documentId);
                            }
                        }
                    } else {
                        System.out.println("Error fetching documents: " + task.getException());
                    }
                });
    }

    private void addUserCard(String email, String documentId) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.rejected_request_item, containerLayout, false);

        TextView emailTextView = cardView.findViewById(R.id.emailTextView);
        emailTextView.setText(email);

        MaterialButton acceptButton = cardView.findViewById(R.id.acceptButton);
        acceptButton.setTag(documentId); // Store document ID as tag for dynamic access

        MaterialButton detailsButton = cardView.findViewById(R.id.detailsButton);
        detailsButton.setTag(documentId);
        detailsButton.setOnClickListener(view -> {
            // Create a dummy user or a specific subclass of User to fetch details
            //User user = new User() {};  // Replace this with a concrete subclass if applicable
            User.fetchAndDisplayUserDetails(view.getContext(), documentId);
        });

        containerLayout.addView(cardView);
    }

    public void onAcceptClick(View view) {
        String documentId = (String) view.getTag();

        if (documentId != null) {
            System.out.println("Document ID for update: " + documentId);
            updateUserStatus(documentId, "approved");
        } else {
            System.err.println("Error: Document ID is null.");
        }
    }


    private void updateUserStatus(String documentId, String newStatus) {
        db.collection("users").document(documentId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("User status updated to " + newStatus);

                    // Clear the current list
                    containerLayout.removeAllViews();

                    // Reload the pending users list
                    fetchRejectedUsers();

                })



                .addOnFailureListener(e -> {
                    System.err.println("Error updating status: " + e.getMessage());
                });
    }
}


