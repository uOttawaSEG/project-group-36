package com.example.eams_project_fall2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import javax.microedition.khronos.egl.EGLDisplay;

public class OrganizerHomepageActivity extends AppCompatActivity {

    private Button createEventButton;
    private Button viewUpcomingEventsButton;
    private Button viewPastEventsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organiser_homepage);

        createEventButton = findViewById(R.id.createEventButton);
        viewUpcomingEventsButton = findViewById(R.id.viewUpcomingEventsButton);
        viewPastEventsButton = findViewById(R.id.viewPastEventsButton);

        createEventButton.setOnClickListener(view -> {
            startActivity(new Intent(OrganizerHomepageActivity.this, CreateEventActivity.class));
        });

        // Set OnClickListener for View Upcoming Events button
        viewUpcomingEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizerHomepageActivity.this, UpcomingEventsActivityForOrganizer.class);
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