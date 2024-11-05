package com.example.eams_project_fall2024;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupListActivity extends AppCompatActivity {

    private LinearLayout attendeeListLayout;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_list);

        attendeeListLayout = findViewById(R.id.attendeeListLayout);

        String eventName = getIntent().getStringExtra("eventName");

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("events").child(eventName).child("attendees");

        // Fetch attendees from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                attendeeListLayout.removeAllViews(); // Clear previous attendees
                for (DataSnapshot attendeeSnapshot : snapshot.getChildren()) {
                    String attendeeName = attendeeSnapshot.getValue(String.class);
                    addAttendeeTextView(attendeeName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SignupListActivity", "Error fetching attendees", error.toException());
            }
        });
    }

    // Method to dynamically add a TextView for each attendee
    private void addAttendeeTextView(String attendeeName) {
        TextView attendeeTextView = new TextView(this);
        attendeeTextView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        attendeeTextView.setText(attendeeName);
        attendeeTextView.setGravity(Gravity.CENTER_VERTICAL);
        attendeeTextView.setTextSize(16);
        attendeeTextView.setPadding(16, 16, 16, 16);

        attendeeListLayout.addView(attendeeTextView);
    }
}
