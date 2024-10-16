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

import java.util.HashMap;
import java.util.Map;

public class OrganizerSignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText, phoneEditText, organizationNameEditText, addressEditText;
    private Button OrganizerSignUpButton;
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
        organizationNameEditText = findViewById(R.id.OrganizationName);
        OrganizerSignUpButton = findViewById(R.id.OrganizerSignUpButton);
        addressEditText = findViewById(R.id.OrganizationAddress);

        OrganizerSignUpButton.setOnClickListener(view -> {
            signUpOrganizer();
        });
    }

    private void signUpOrganizer() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String organizationName = organizationNameEditText.getText().toString().trim();
        String role = "organizer";

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

        if (organizationName.isEmpty()) {
            addressEditText.setError("organization name is required");
            addressEditText.requestFocus();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();

                        Map<String, Object> organizer = new HashMap<>();
                        organizer.put("firstName", firstName);
                        organizer.put("role", role);
                        organizer.put("lastName", lastName);
                        organizer.put("email", email);
                        organizer.put("phone", phone);
                        organizer.put("address", address);



                        db.collection("organizer").document(userId).set(organizer)
                                .addOnSuccessListener(aVoid ->  {
                                    startActivity(new Intent(OrganizerSignUpActivity.this, OrganizerHomepageActivity.class));
                                });



                    }
                });
    }
}
