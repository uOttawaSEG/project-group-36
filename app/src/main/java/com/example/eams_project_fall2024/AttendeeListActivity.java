package com.example.eams_project_fall2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_list);
        attendeeListLayout = findViewById(R.id.attendeeListLayout);

        String eventId = getIntent().getStringExtra("EVENT_ID");
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
                        addAttendeeItem(attendee);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addAttendeeItem(Attendee attendee) {
        View attendeeView = LayoutInflater.from(this).inflate(R.layout.attendee_item, attendeeListLayout, false);
        TextView attendeeNameTextView = attendeeView.findViewById(R.id.attendeeNameTextView);
        attendeeNameTextView.setText(attendee.getName());

        Button detailsButton = attendeeView.findViewById(R.id.attendeeDetailsButton);
        detailsButton.setOnClickListener(v -> {
            // Show details about the attendee.
        });

        Button acceptButton = attendeeView.findViewById(R.id.attendeeAcceptButton);
        acceptButton.setOnClickListener(v -> {
            // Approve the attendee.
        });

        attendeeListLayout.addView(attendeeView);
    }
}
