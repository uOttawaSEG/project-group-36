package com.example.eams_project_fall2024;

import android.os.Bundle;
import android.view.LayoutInflater;
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

    private LinearLayout attendeeListLayout;  // Updated to match the ID in activity_signup_list.xml
    private DatabaseReference eventRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_list);  // Corrected layout reference

        attendeeListLayout = findViewById(R.id.attendeeListLayout);  // Corrected ID reference

        // Get the event ID from the Intent
        String eventId = getIntent().getStringExtra("EVENT_ID");

        // Reference to the event's attendees in Firebase
        eventRef = FirebaseDatabase.getInstance().getReference("events").child(eventId).child("attendees");

        loadAttendees();
    }

    private void loadAttendees() {
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot attendeeSnapshot : snapshot.getChildren()) {
                    Attendee attendee = attendeeSnapshot.getValue(Attendee.class);
                    if (attendee != null) {
                        addAttendeeItem(attendee.getName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AttendeeListActivity.this, "Failed to load attendees", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addAttendeeItem(String attendeeName) {
        View attendeeView = LayoutInflater.from(this).inflate(R.layout.attendee_item, attendeeListLayout, false);
        TextView attendeeNameTextView = attendeeView.findViewById(R.id.attendeeNameTextView);
        attendeeNameTextView.setText(attendeeName);
        attendeeListLayout.addView(attendeeView);
    }
}
