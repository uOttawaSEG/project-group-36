package com.example.eams_project_fall2024;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ApprovedRequestsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<String> approvedUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.approved_requests);

        getWindow().setStatusBarColor(android.graphics.Color.rgb(30, 30, 30));
        getWindow().setNavigationBarColor(android.graphics.Color.rgb(30, 30, 30));

        db = FirebaseFirestore.getInstance();
        LinearLayout containerLayout = findViewById(R.id.containerLayout);

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
                            String documentId = document.getId();
                            if (email != null) {
                                approvedUsers.add(email);
                                addUserCard(containerLayout, email, documentId);
                            }
                        }
                    } else {
                        System.out.println("Error fetching documents: " + task.getException());
                    }
                });
    }

    private void addUserCard(LinearLayout containerLayout, String email, String documentId) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.approved_request_item, containerLayout, false);

        TextView emailTextView = cardView.findViewById(R.id.emailTextView);
        emailTextView.setText(email);

        MaterialButton detailsButton = cardView.findViewById(R.id.detailsButton);
        detailsButton.setTag(documentId);
        detailsButton.setOnClickListener(view -> {
            // Create a dummy user or a specific subclass of User to fetch details
            //User user = new User() {};  // Replace this with a concrete subclass if applicable
            User.fetchAndDisplayUserDetails(view.getContext(), documentId);
        });

        containerLayout.addView(cardView);
    }
}
