package com.example.eams_project_fall2024;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AttendeeApprovedEventsActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private LinearLayout approvedEventsContainer;
    private FirebaseAuth auth;
    private ArrayList<Event> approvedEventsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approved_events);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        approvedEventsContainer = findViewById(R.id.approvedEventsContainer);

        loadApprovedEvents();
    }

    private void loadApprovedEvents() {
        String attendeeId = auth.getCurrentUser().getUid();
        Log.d("ApprovedEvents", "Fetching approved events for AttendeeID=" + attendeeId);

        db.collection("events")
                .whereArrayContains("approvedAttendees", attendeeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Log.d("ApprovedEvents", "Successfully fetched approved events for attendee.");
                        approvedEventsContainer.removeAllViews();
                        approvedEventsList.clear(); // Clear old list

                        for (DocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            Date eventDate = document.getDate("eventDate");
                            Date startTime = document.getDate("startTime");
                            Date endTime = document.getDate("endTime");

                            Log.d("ApprovedEvents", "Event: " + title + " | Date=" + eventDate +
                                    " | Start=" + startTime + " | End=" + endTime);

                            Event event = new Event(title, startTime, endTime);
                            approvedEventsList.add(event);

                            MaterialCardView eventCard = createEventCard(title, eventDate, startTime, endTime);
                            approvedEventsContainer.addView(eventCard);
                        }
                    } else {
                        Log.d("ApprovedEvents", "No approved events found for AttendeeID=" + attendeeId);
                        Toast.makeText(this, "No approved events found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ApprovedEvents", "Failed to fetch approved events: " + e.getMessage(), e);
                    Toast.makeText(this, "Failed to load approved events: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public ArrayList<Event> getApprovedEvents() {
        Log.d("ApprovedEvents", "Retrieving approved events list...");
        ArrayList<Event> approvedEvents = new ArrayList<>();
        for (int i = 0; i < approvedEventsContainer.getChildCount(); i++) {
            View card = approvedEventsContainer.getChildAt(i);
            int[] viewIds = (int[]) card.getTag(); // Retrieve the stored IDs
            int titleViewId = viewIds[0];
            int dateViewId = viewIds[1];
            int timeViewId = viewIds[2];

            String title = ((TextView) card.findViewById(titleViewId)).getText().toString().replace("Title: ", "");
            String dateText = ((TextView) card.findViewById(dateViewId)).getText().toString().replace("Date: ", "");
            String timeText = ((TextView) card.findViewById(timeViewId)).getText().toString().replace("Time: ", "");

            try {
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy h:mm a", Locale.getDefault());
                Date startTime = dateTimeFormat.parse(dateText + " " + timeText.split(" - ")[0].trim());
                Date endTime = dateTimeFormat.parse(dateText + " " + timeText.split(" - ")[1].trim());

                approvedEvents.add(new Event(title, startTime, endTime));
            } catch (Exception e) {
                Log.e("ApprovedEvents", "Error parsing event details: " + e.getMessage(), e);
            }
        }
        Log.d("ApprovedEvents", "Retrieved " + approvedEvents.size() + " approved events.");
        return approvedEvents;
    }

    private MaterialCardView createEventCard(String title, Date eventDate, Date startTime, Date endTime) {
        MaterialCardView eventCard = new MaterialCardView(this);
        eventCard.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        eventCard.setCardElevation(4);
        eventCard.setRadius(12);
        eventCard.setCardBackgroundColor(Color.parseColor("#1A1A1A"));
        eventCard.setUseCompatPadding(true);
        eventCard.setPadding(16, 16, 16, 16);
        eventCard.setContentPadding(16, 16, 16, 16);

        LinearLayout eventLayout = new LinearLayout(this);
        eventLayout.setOrientation(LinearLayout.VERTICAL);

        TextView titleView = new TextView(this);
        titleView.setText("Title: " + title);
        titleView.setTextSize(18);
        titleView.setTextColor(Color.WHITE);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        TextView dateView = new TextView(this);
        dateView.setText("Date: " + (eventDate != null ? dateFormat.format(eventDate) : "N/A"));
        dateView.setTextColor(Color.LTGRAY);

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        TextView timeView = new TextView(this);
        timeView.setText("Time: " + (startTime != null ? timeFormat.format(startTime) : "N/A") +
                " - " + (endTime != null ? timeFormat.format(endTime) : "N/A"));
        timeView.setTextColor(Color.LTGRAY);

        eventLayout.addView(titleView);
        eventLayout.addView(dateView);
        eventLayout.addView(timeView);
        eventCard.addView(eventLayout);

        return eventCard;
    }
}
