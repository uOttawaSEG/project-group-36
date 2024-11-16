package com.example.eams_project_fall2024;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class AttendeeHomepageActivity extends AppCompatActivity {

    private Button viewUpcomingEventsButton;
    private Button searchForEventsButton;
    private Button openApprovedEventsButton; // New button for approved events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendee_homepage);

        // Initialize the "View Upcoming Events" button
        viewUpcomingEventsButton = findViewById(R.id.viewUpcomingEventsButton);
        viewUpcomingEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeHomepageActivity.this, UpcomingEventsActivityForAttendee.class);
                startActivity(intent);
            }
        });

        // Initialize the "Search for Events" button
        searchForEventsButton = findViewById(R.id.searchForEventsButton);
        searchForEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeHomepageActivity.this, FilteredUpcomingEventsActivityForAttendee.class);
                startActivity(intent);
            }
        });

        // Initialize the "View Approved Events" button
        openApprovedEventsButton = findViewById(R.id.openApprovedEventsButton); // Link to the button in the layout
        openApprovedEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeHomepageActivity.this, AttendeeApprovedEventsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void logout(View view) {
        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut();

        // Clear any stored data
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
}
