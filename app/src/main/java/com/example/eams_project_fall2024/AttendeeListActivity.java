package com.example.eams_project_fall2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AttendeeListActivity extends AppCompatActivity {

    private LinearLayout attendeeListLayout;
    private DatabaseReference eventRef;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_list);

        attendeeListLayout = findViewById(R.id.attendeeListLayout);

        // Retrieve event ID from intent
        eventId = getIntent().getStringExtra("EVENT_ID");
        if (eventId == null) {
            Toast.makeText(this, "Event ID is missing", Toast.LENGTH_SHORT).show();
            finish(); // End activity if event ID is null
            return;
        }

        // Initialize the Firebase Database reference using the event ID
        eventRef = FirebaseDatabase.getInstance().getReference("events").child(eventId).child("attendees");

        loadAttendees();
    }

    private void loadAttendees() {
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                attendeeListLayout.removeAllViews(); // Clear previous views

                for (DataSnapshot attendeeSnapshot : snapshot.getChildren()) {
                    String attendeeName = attendeeSnapshot.getValue(String.class);
                    if (attendeeName != null) {
                        addAttendeeToLayout(attendeeName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AttendeeListActivity.this, "Failed to load attendees.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addAttendeeToLayout(String attendeeName) {
        TextView attendeeTextView = new TextView(this);
        attendeeTextView.setText(attendeeName);
        attendeeTextView.setTextSize(16);
        attendeeTextView.setPadding(16, 16, 16, 16);
        attendeeListLayout.addView(attendeeTextView);
    }
}
