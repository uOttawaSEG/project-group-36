package com.example.eams_project_fall2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class UpcomingEventsActivity extends AppCompatActivity {

    private LinearLayout upcomingContainerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_events);

        upcomingContainerLayout = findViewById(R.id.upcomingContainerLayout);

        // Example of dynamically adding an upcoming event item
        addEventItem("Sample Upcoming Event 1");
        addEventItem("Sample Upcoming Event 2");
    }

    // Method to dynamically add an event item
    private void addEventItem(String eventName) {
        View eventView = LayoutInflater.from(this).inflate(R.layout.event_item, upcomingContainerLayout, false);
        TextView eventNameTextView = eventView.findViewById(R.id.eventName);
        eventNameTextView.setText(eventName);
        upcomingContainerLayout.addView(eventView);
    }
}

