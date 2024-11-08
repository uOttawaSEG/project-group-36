package com.example.eams_project_fall2024;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupListActivity extends AppCompatActivity {

    private EditText firstNameInput, lastNameInput, emailInput, phoneInput;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_list);

        // Retrieve event details passed from the previous activity
        Intent intent = getIntent();
        String eventName = intent.getStringExtra("eventName");
        String eventId = intent.getStringExtra("eventId");

        // Set the title to display the event name
        TextView titleTextView = findViewById(R.id.titleSignupList);
        if (eventName != null) {
            titleTextView.setText("Sign Up for " + eventName);
        } else {
            titleTextView.setText("Sign Up");
        }

        // Initialize form fields
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);

        // Set up the submit button
        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> {
            String firstName = firstNameInput.getText().toString().trim();
            String lastName = lastNameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();

            // Perform validation and save the signup details
            if (!firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !phone.isEmpty()) {
                // Logic to save the details to Firebase or another database
                Toast.makeText(SignupListActivity.this, "Successfully signed up for " + eventName, Toast.LENGTH_SHORT).show();
                finish(); // Close the activity after submission
            } else {
                Toast.makeText(SignupListActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
