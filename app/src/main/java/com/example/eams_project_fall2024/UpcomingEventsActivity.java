package com.example.eams_project_fall2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpcomingEventsActivity extends AppCompatActivity {
    private LinearLayout upcomingContainerLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_events);
        db = FirebaseFirestore.getInstance();
        upcomingContainerLayout = findViewById(R.id.upcomingContainerLayout);

        addEventItem("Sample Event 1", "sampleEventId1");
        addEventItem("Sample Event 2", "sampleEventId2");
    }

    private void addEventItem(String eventName, String eventId) {
        View eventView = LayoutInflater.from(this).inflate(R.layout.event_item, upcomingContainerLayout, false);
        TextView eventNameTextView = eventView.findViewById(R.id.eventName);
        eventNameTextView.setText(eventName);

        Button signupListButton = eventView.findViewById(R.id.signupListButton);
        signupListButton.setOnClickListener(v -> {
            Intent intent = new Intent(UpcomingEventsActivity.this, SignupListActivity.class);
            intent.putExtra("eventName", eventName);
            startActivity(intent);
        });

        upcomingContainerLayout.addView(eventView);
    }
}

