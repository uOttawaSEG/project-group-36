package com.example.eams_project_fall2024;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity {

    private TextInputEditText titleEditText, descriptionEditText, addressEditText;
    private TextView dateTextView, startTimeTextView, endTimeTextView;
    private SwitchMaterial approvalSwitch;
    private MaterialButton createEventButton;

    private Calendar selectedDate;
    private int startHour = 8, startMinute = 0;
    private int endHour = 9, endMinute = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_event);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        addressEditText = findViewById(R.id.addressEditText);
        approvalSwitch = findViewById(R.id.approvalSwitch);
        createEventButton = findViewById(R.id.createEventButton);
        dateTextView = findViewById(R.id.dateTextView);
        startTimeTextView = findViewById(R.id.startTimeTextView);
        endTimeTextView = findViewById(R.id.endTimeTextView);

        selectedDate = Calendar.getInstance();

        View datePickerCard = findViewById(R.id.datePickerCard);
        datePickerCard.setOnClickListener(v -> showDatePicker());

        View startTimePickerCard = findViewById(R.id.startTimePickerCard);
        startTimePickerCard.setOnClickListener(v -> showStartTimePicker());

        View endTimePickerCard = findViewById(R.id.endTimePickerCard);
        endTimePickerCard.setOnClickListener(v -> showEndTimePicker());

        createEventButton.setOnClickListener(this::createEvent);

        updateDateDisplay();
        updateTimeDisplays();
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateDisplay();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showStartTimePicker() {
        TimePickerDialog startTimePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    minute = (minute / 30) * 30;

                    startHour = hourOfDay;
                    startMinute = minute;

                    if (startHour > endHour || (startHour == endHour && startMinute >= endMinute)) {
                        endHour = startHour + 1;
                        endMinute = startMinute;
                        Toast.makeText(this, "End time adjusted to be after start time.", Toast.LENGTH_SHORT).show();
                    }

                    updateTimeDisplays();
                },
                startHour,
                startMinute,
                DateFormat.is24HourFormat(this)
        );

        startTimePickerDialog.show();
    }

    private void showEndTimePicker() {
        TimePickerDialog endTimePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    minute = (minute / 30) * 30;

                    if (hourOfDay < startHour || (hourOfDay == startHour && minute <= startMinute)) {
                        Toast.makeText(this, "End time must be after start time.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    endHour = hourOfDay;
                    endMinute = minute;
                    updateTimeDisplays();
                },
                endHour,
                endMinute,
                DateFormat.is24HourFormat(this)
        );

        endTimePickerDialog.show();
    }

    private void updateDateDisplay() {
        String dateStr = android.text.format.DateFormat.getDateFormat(this).format(selectedDate.getTime());
        dateTextView.setText(dateStr);
    }

    private void updateTimeDisplays() {
        startTimeTextView.setText(String.format("%02d:%02d", startHour, startMinute));
        endTimeTextView.setText(String.format("%02d:%02d", endHour, endMinute));
    }

    public void createEvent(View view) {
        if (!validateFields()) {
            return;
        }

        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        boolean isAutoApproval = approvalSwitch.isChecked();

        Calendar eventStartTime = (Calendar) selectedDate.clone();
        eventStartTime.set(Calendar.HOUR_OF_DAY, startHour);
        eventStartTime.set(Calendar.MINUTE, startMinute);
        eventStartTime.set(Calendar.SECOND, 0);
        eventStartTime.set(Calendar.MILLISECOND, 0);

        Calendar eventEndTime = (Calendar) selectedDate.clone();
        eventEndTime.set(Calendar.HOUR_OF_DAY, endHour);
        eventEndTime.set(Calendar.MINUTE, endMinute);
        eventEndTime.set(Calendar.SECOND, 0);
        eventEndTime.set(Calendar.MILLISECOND, 0);

        saveEventToDatabase(title, description, eventStartTime, eventEndTime, address, isAutoApproval);
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (titleEditText.getText().toString().trim().isEmpty()) {
            titleEditText.setError("Title is required");
            isValid = false;
        }

        if (descriptionEditText.getText().toString().trim().isEmpty()) {
            descriptionEditText.setError("Description is required");
            isValid = false;
        }

        if (addressEditText.getText().toString().trim().isEmpty()) {
            addressEditText.setError("Address is required");
            isValid = false;
        }

        return isValid;
    }

    private void saveEventToDatabase(String title, String description, Calendar startTime,
                                     Calendar endTime, String address, boolean isAutoApproval) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("description", description);
        event.put("startTime", startTime.getTime());
        event.put("endTime", endTime.getTime());
        event.put("address", address);
        event.put("isAutoApproval", isAutoApproval);
        event.put("createdAt", new Date()); // The date when the event was created

        // Store startTime as a separate field for easy comparison
        event.put("eventDate", startTime.getTime());

        db.collection("events")
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Event created successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}


