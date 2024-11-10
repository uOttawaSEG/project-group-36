package com.example.eams_project_fall2024;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AttendeeSignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText firstNameEditText, lastNameEditText, emailEditText, phoneEditText;
    private EditText passwordEditText, addressEditText;
    private Button signUpAttendeeButton;
    private TextView attendeeLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendee_sign_up);

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        firstNameEditText = findViewById(R.id.firstNameInput);
        lastNameEditText = findViewById(R.id.lastNameInput);
        emailEditText = findViewById(R.id.emailInput);
        phoneEditText = findViewById(R.id.phoneInput);
        passwordEditText = findViewById(R.id.passwordInput); // Added
        addressEditText = findViewById(R.id.addressInput); // Added
        signUpAttendeeButton = findViewById(R.id.submitButton);
        attendeeLoginLink = findViewById(R.id.loginLink); // Added

        // Set up login link click listener
        attendeeLoginLink.setOnClickListener(view -> {
            startActivity(new Intent(AttendeeSignUpActivity.this, LoginActivity.class));
        });

        // Set up sign-up button click listener
        signUpAttendeeButton.setOnClickListener(view -> {
            signUpUser();
        });
    }

    private void signUpUser() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String role = "attendee";

        // Validation checks
        if (firstName.isEmpty()) {
            firstNameEditText.setError("First name is required");
            firstNameEditText.requestFocus();
            return;
        }

        if (lastName.isEmpty()) {
            lastNameEditText.setError("Last name is required");
            lastNameEditText.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        } else if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            phoneEditText.setError("Phone number is required");
            phoneEditText.requestFocus();
            return;
        } else if (!Patterns.PHONE.matcher(phone).matches() || phone.length() < 10) {
            phoneEditText.setError("Please enter a valid phone number");
            phoneEditText.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            addressEditText.setError("Address is required");
            addressEditText.requestFocus();
            return;
        }

        // Create user in Firebase
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        // Prepare data for Firestore
                        Map<String, Object> attendee = new HashMap<>();
                        attendee.put("firstName", firstName);
                        attendee.put("lastName", lastName);
                        attendee.put("email", email);
                        attendee.put("phone", phone);
                        attendee.put("address", address);
                        attendee.put("role", role);
                        attendee.put("status", "pending");

                        // Save data to Firestore
                        db.collection("users").document(userId).set(attendee)
                                .addOnSuccessListener(aVoid -> {
                                    startActivity(new Intent(AttendeeSignUpActivity.this, LoginActivity.class));
                                });
                    }
                });
    }
}
