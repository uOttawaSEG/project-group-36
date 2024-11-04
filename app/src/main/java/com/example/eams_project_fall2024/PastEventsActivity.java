package com.example.eams_project_fall2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PastEventsActivity extends AppCompatActivity {

    private LinearLayout containerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_events);

        containerLayout = findViewById(R.id.containerLayout);

        // Example of dynamically adding a past event item
        addEventItem("Sample Event 1");
        addEventItem("Sample Event 2");
    }

    // Method to dynamically add an event item
    private void addEventItem(String eventName) {
        View eventView = LayoutInflater.from(this).inflate(R.layout.event_item, containerLayout, false);
        TextView eventNameTextView = eventView.findViewById(R.id.eventName);
        eventNameTextView.setText(eventName);
        containerLayout.addView(eventView);
    }
}

