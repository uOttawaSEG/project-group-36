package com.example.eams_project_fall2024;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.List;

public class SignupListActivity extends AppCompatActivity {

    private LinearLayout attendeeListLayout;
    private DatabaseReference databaseReference;
    private List<Attendee> attendeeList = new ArrayList<>(); // Assuming an Attendee class exists

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_list);

        attendeeListLayout = findViewById(R.id.attendeeListLayout);
        Button acceptAllButton = findViewById(R.id.acceptAllButton);

        String eventName = getIntent().getStringExtra("eventName");

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("events").child(eventName).child("attendees");

        // Fetch attendees from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                attendeeListLayout.removeAllViews(); // Clear previous attendees
                attendeeList.clear(); // Clear list before adding new data

                for (DataSnapshot attendeeSnapshot : snapshot.getChildren()) {
                    String attendeeName = attendeeSnapshot.getValue(String.class);
                    if (attendeeName != null) {
                        attendeeList.add(new Attendee(attendeeName)); // Add attendee to list
                        addAttendeeTextView(attendeeName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SignupListActivity", "Error fetching attendees", error.toException());
            }
        });

        // Set listener for "Accept All" button
        acceptAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptAllAttendees();
            }
        });
    }

    // Method to dynamically add a TextView for each attendee
    private void addAttendeeTextView(String attendeeName) {
        TextView attendeeTextView = new TextView(SignupListActivity.this);
        attendeeTextView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        attendeeTextView.setText(attendeeName);
        attendeeTextView.setGravity(Gravity.CENTER_VERTICAL);
        attendeeTextView.setTextSize(16);
        attendeeTextView.setPadding(16, 16, 16, 16);

        attendeeListLayout.addView(attendeeTextView);
    }

    // Method to accept all attendees
    private void acceptAllAttendees() {
        for (Attendee attendee : attendeeList) {
            attendee.setStatus("accepted");  // Example status update method
            // Update in the database if needed
        }

        // Notify user of success and refresh the list if necessary
        Toast.makeText(SignupListActivity.this, "All attendees accepted!", Toast.LENGTH_SHORT).show();
        // Update UI if needed, e.g., by reloading or updating attendee list
        updateAttendeeList();
    }

    // Placeholder method for updating the attendee list on the UI
    private void updateAttendeeList() {
        // Implementation for updating the attendee list UI after changes
    }
}
