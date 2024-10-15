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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class OrganizerSignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText, phoneEditText, organizationAddressEditText, organizationNameEditText;
    private Button signUpOrganizerButton;
    private TextView organizerLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organiser_sign_up);

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        organizerLoginLink = findViewById(R.id.loginLink);

        organizerLoginLink.setOnClickListener(view -> {
            startActivity(new Intent(OrganizerSignUpActivity.this, LoginActivity.class));
        });


        firstNameEditText = findViewById(R.id.OrganizerFirstName);
        lastNameEditText = findViewById(R.id.OrganizerLastName);
        emailEditText = findViewById(R.id.OrganizerEmail);
        passwordEditText = findViewById(R.id.OrganizerPassword);
        phoneEditText = findViewById(R.id.OrganizerPhoneNumber);
        organizationAddressEditText = findViewById(R.id.OrganizationAddress);
        organizationNameEditText = findViewById(R.id.OrganizationName);
        signUpOrganizerButton = findViewById(R.id.OrganizerSignUpButton);

        signUpOrganizerButton.setOnClickListener(view -> {
            signUpOrganizer();
        });
    }

    private void signUpOrganizer() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String organization = organizationAddressEditText.getText().toString().trim();

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

        if (organization.isEmpty()) {
            organizationNameEditText.setError("Organization Name is required");
            organizationNameEditText.requestFocus();
            return;
        }



        // Firebase Authentication - Create new organizer user
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        // Navigate to Organizer Home Page after successful sign-up
                        startActivity(new Intent(OrganizerSignUpActivity.this, OrganizerHomepageActivity.class));
                    }
                });
    }
}
