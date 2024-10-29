package com.example.eams_project_fall2024;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PendingRequestsActivity extends AppCompatActivity {

    private RecyclerView pendingRequestsRecyclerView;
    private UserAdapter userAdapter;  // Adapter to display user data
    private List<User> pendingUsers;  // Assume User class contains user data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pending_requests);

        // Initialize RecyclerView
        pendingRequestsRecyclerView = findViewById(R.id.pendingRequestsRecyclerView);
        pendingRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch pending users from Firestore and pass to RecyclerView
        fetchPendingUsers();
    }

    private void fetchPendingUsers() {
        // Query Firestore for pending users and populate list
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("status", "pending")  // Only fetch pending users
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    pendingUsers = queryDocumentSnapshots.toObjects(User.class);
                    userAdapter = new UserAdapter(this, pendingUsers);
                    pendingRequestsRecyclerView.setAdapter(userAdapter);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                });
    }
}

