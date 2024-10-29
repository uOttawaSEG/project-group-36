package com.example.eams_project_fall2024;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RejectedRequestsActivity extends AppCompatActivity {

    private RecyclerView rejectedRequestsRecyclerView;
    private UserAdapter userAdapter;  // Adapter to display user data
    private List<User> rejectedUsers;  // Assume User class contains user data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rejected_requests);

        // Initialize RecyclerView
        rejectedRequestsRecyclerView = findViewById(R.id.rejectedRequestsRecyclerView);
        rejectedRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch pending users from Firestore and pass to RecyclerView
        fetchRejectedUsers();
    }

    private void fetchRejectedUsers() {
        // Query Firestore for pending users and populate list
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("status", "pending")  // Only fetch pending users
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    rejectedUsers = queryDocumentSnapshots.toObjects(User.class);
                    userAdapter = new UserAdapter(this, rejectedUsers);
                    rejectedRequestsRecyclerView.setAdapter(userAdapter);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                });
    }
}
