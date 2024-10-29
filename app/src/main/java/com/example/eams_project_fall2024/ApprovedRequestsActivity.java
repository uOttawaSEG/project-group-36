package com.example.eams_project_fall2024;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ApprovedRequestsActivity extends AppCompatActivity {

    private FirebaseFirestore db;  // Firestore instance
    private List<String> approvedUsers;  // List to store approved user emails

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approved_requests); // Main layout file

        // Set status and navigation bar colors
        getWindow().setStatusBarColor(android.graphics.Color.rgb(30, 30, 30));
        getWindow().setNavigationBarColor(android.graphics.Color.rgb(30, 30, 30));

        db = FirebaseFirestore.getInstance();  // Initialize Firestore
        LinearLayout containerLayout = findViewById(R.id.containerLayout);  // Layout to hold user cards

        // Fetch approved users from Firestore and populate the container layout
        fetchApprovedUsers(containerLayout);
    }

    private void fetchApprovedUsers(LinearLayout containerLayout) {
        db.collection("users")
                .whereEqualTo("status", "approved")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        approvedUsers = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();

                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String email = document.getString("email");
                            String documentId = document.getId();  // Get document ID for details
                            if (email != null) {
                                approvedUsers.add(email);
                                addUserCard(containerLayout, email, documentId);  // Add user card for each approved user
                            }
                        }
                    } else {
                        System.out.println("Error fetching documents: " + task.getException());
                    }
                });
    }

    private void addUserCard(LinearLayout containerLayout, String email, String documentId) {
        // Inflate the custom layout for each approved user
        View cardView = LayoutInflater.from(this).inflate(R.layout.approved_request_item, containerLayout, false);

        TextView emailTextView = cardView.findViewById(R.id.emailTextView);
        emailTextView.setText(email);

        MaterialButton detailsButton = cardView.findViewById(R.id.detailsButton);
        detailsButton.setTag(documentId);  // Set document ID as tag on the Details button
        detailsButton.setOnClickListener(this::showUserDetails);  // Set the click listener

        // Add the card view to the container
        containerLayout.addView(cardView);
    }

    public void showUserDetails(View view) {
        String documentId = (String) view.getTag();  // Retrieve document ID from the button's tag

        if (documentId != null) {
            fetchAndDisplayUserDetails(view.getContext(), documentId);
        } else {
            System.err.println("Error: Document ID is null.");
        }
    }

    private void fetchAndDisplayUserDetails(Context context, String documentId) {
        db.collection("users").document(documentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve user details with null checks
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        String email = documentSnapshot.getString("email");
                        String phone = documentSnapshot.getString("phone");
                        String address = documentSnapshot.getString("address");
                        String organizationName = documentSnapshot.getString("organizationName");
                        String role = documentSnapshot.getString("role");

                        String name = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
                        email = (email != null) ? email : "Not available";
                        phone = (phone != null) ? phone : "Not available";
                        address = (address != null) ? address : "Not available";
                        organizationName = (role.equals("organizer") || organizationName != null) ? organizationName : "";
                        role = (role != null) ? role : "Not specified";

                        String message = "Name: " + name + "\n" +
                                "Email: " + email + "\n" +
                                "Phone: " + phone + "\n" +
                                "Address: " + address + "\n" +
                                (role.equals("organizer") ? "Organization: " + organizationName + "\n" : "") +
                                "Role: " + role;

                        new AlertDialog.Builder(context)
                                .setTitle("User Details")
                                .setMessage(message)
                                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                                .show();
                    } else {
                        new AlertDialog.Builder(context)
                                .setTitle("User Not Found")
                                .setMessage("User details could not be found.")
                                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                                .show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Show error if there's an issue fetching details
                    new AlertDialog.Builder(context)
                            .setTitle("Error")
                            .setMessage("Failed to retrieve user details: " + e.getMessage())
                            .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                            .show();
                });
    }
}

