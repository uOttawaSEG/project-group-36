package com.example.eams_project_fall2024;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ApprovedRequestsActivity extends AppCompatActivity {
    private RecyclerView approvedRequestsRecyclerView;
    private UserAdapter userAdapter;  // Adapter to display user data
    private List<User> approvedUsers;  // Assume User class contains user data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approved_requests);

        // Initialize RecyclerView
        approvedRequestsRecyclerView = findViewById(R.id.approvedRequestsRecyclerView);

        // Fetch pending users from Firestore and pass to RecyclerView
        fetchApprovedUsers();
    }

    private void fetchApprovedUsers() {
        // Query Firestore for pending users and populate list
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("status", "pending")  // Only fetch pending users
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    approvedUsers = queryDocumentSnapshots.toObjects(User.class);
                    userAdapter = new UserAdapter(this, approvedUsers);
                    approvedRequestsRecyclerView.setAdapter(userAdapter);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                });
    }
}
