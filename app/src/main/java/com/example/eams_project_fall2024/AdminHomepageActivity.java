package com.example.eams_project_fall2024;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminHomepageActivity extends AppCompatActivity {
    private Administrator administrator;


    public void logout(View view) {
        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut();

        // Clear shared preferences
        SharedPreferences preferences = getSharedPreferences("YourAppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_homepage);

        // Set up buttons
        Button viewRequestsButton = findViewById(R.id.viewRequestsButton);
        Button viewRejectedRequestsButton = findViewById(R.id.viewRejectedRequestsButton);
        Button viewApprovedRequestsButton = findViewById(R.id.viewApprovedRequestsButton);

        // Open Pending Requests Activity
        viewRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomepageActivity.this, PendingRequestsActivity.class);
                startActivity(intent);
            }
        });

        // Open Rejected Requests Activity
        viewRejectedRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomepageActivity.this, RejectedRequestsActivity.class);
                startActivity(intent);
            }
        });

        // Open Approved Requests Activity
        viewApprovedRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomepageActivity.this, ApprovedRequestsActivity.class);
                startActivity(intent);
            }
        });
    }

}
