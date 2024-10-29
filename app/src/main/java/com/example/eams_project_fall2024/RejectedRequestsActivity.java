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
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class RejectedRequestsActivity extends AppCompatActivity {

    private List<String> rejectedUsers;  // List to store pending user emails

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rejected_requests); // Updated layout file name

        // Set status and navigation bar colors
        getWindow().setStatusBarColor(android.graphics.Color.rgb(30, 30, 30));
        getWindow().setNavigationBarColor(android.graphics.Color.rgb(30, 30, 30));

        // Reference to the container layout
        LinearLayout containerLayout = findViewById(R.id.containerLayout);

        // Fetch pending users from Firestore and populate the container layout
        fetchRejectedUsers(containerLayout);
    }

    private void fetchRejectedUsers(LinearLayout containerLayout) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query Firestore for pending users
        db.collection("users")
                .whereEqualTo("status", "rejected")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        rejectedUsers = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();

                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String email = document.getString("email");
                            if (email != null) {
                                rejectedUsers.add(email);
                            }
                        }

                        for (String email : rejectedUsers) {
                            View cardView = LayoutInflater.from(this).inflate(R.layout.rejected_request_item, containerLayout, false);

                            TextView emailTextView = cardView.findViewById(R.id.emailTextView);
                            emailTextView.setText(email);

                            MaterialButton acceptButton = cardView.findViewById(R.id.acceptButton);
                            acceptButton.setOnClickListener(v -> {
                            });

                            MaterialButton detailsButton = cardView.findViewById(R.id.detailsButton);
                            detailsButton.setOnClickListener(v -> {
                            });

                            containerLayout.addView(cardView);
                        }

                    } else {
                        System.out.println("Error fetching documents: " + task.getException());
                    }
                });
    }
}


