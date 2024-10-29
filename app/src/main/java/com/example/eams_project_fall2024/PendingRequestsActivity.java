package com.example.eams_project_fall2024;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PendingRequestsActivity extends AppCompatActivity {

    private TextView pendingRequestsRecyclerView;
    private UserAdapter userAdapter;  // Adapter to display user data
    private List<String> pendingUsers;  // Assume User class contains user data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pending_requests);

        getWindow().setStatusBarColor(android.graphics.Color.rgb(30, 30, 30));
        getWindow().setNavigationBarColor(android.graphics.Color.rgb(30, 30, 30));

        // Initialize RecyclerView
        pendingRequestsRecyclerView = findViewById(R.id.pendingRequestsRecyclerView);

        // Fetch pending users from Firestore and pass to RecyclerView
        fetchPendingUsers();
    }

    private void fetchPendingUsers() {
        // Query Firestore for pending users and populate list
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("status", "pending")  // Only fetch pending users
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        pendingUsers = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();

                        for(QueryDocumentSnapshot document: querySnapshot) {
                            String email = document.getString("email");

                            pendingUsers.add(email);
                        }
                        StringBuilder listOfUsers = new StringBuilder();
                        for(String userEmail: pendingUsers) {
                            listOfUsers.append(userEmail).append("\n");
                        }
                        pendingRequestsRecyclerView.setText(listOfUsers);
                        System.out.println("Pending User Emails: " + pendingUsers);
                    } else {
                        System.out.println("ERROR - ");
                    }
                });
    }
}

