package com.example.eams_project_fall2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class OrganizerHomepageActivity extends AppCompatActivity {

    private Button viewUpcomingEventsButton;
    private Button viewPastEventsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organiser_homepage);

        viewUpcomingEventsButton = findViewById(R.id.viewUpcomingEventsButton);
        viewPastEventsButton = findViewById(R.id.viewPastEventsButton);

        // Set OnClickListener for View Upcoming Events button
        viewUpcomingEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizerHomepageActivity.this, UpcomingEventsActivity.class);
                startActivity(intent);
            }
        });

        viewPastEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizerHomepageActivity.this, PastEventsActivity.class);
                startActivity(intent);
            }
        });

    }
}