package com.example.eams_project_fall2024;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private TextView signupLink;
    private EditText loginEmail, loginPassword;
    private FirebaseAuth auth;
    private Button loginButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        signupLink = findViewById(R.id.signupLink);

        signupLink.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        });

        loginButton.setOnClickListener(view -> {
            loginUser();
        });
    }

    private void loginUser() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (email.isEmpty()) {
            loginEmail.setError("Email is required");
            loginEmail.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEmail.setError("Please enter a valid email");
            loginEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            loginPassword.setError("Password is required");
            loginPassword.requestFocus();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            db.collection("users").document(user.getUid()).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String role = documentSnapshot.getString("role");
                                            if ("organizer".equals(role)) {
                                                startActivity(new Intent(LoginActivity.this, OrganizerHomepageActivity.class));
                                            } else if ("attendee".equals(role)) {
                                                startActivity(new Intent(LoginActivity.this, AttendeeHomepageActivity.class));
                                            } else if ("admin".equals(role)) {
                                                startActivity(new Intent(LoginActivity.this, AdminHomepageActivity.class));
                                            } else {
                                                // Handle unknown role
                                            }
                                        }
                                    });
                        }
                    } else {
                        loginPassword.setError("Login failed. Check your credentials");
                        loginPassword.requestFocus();
                    }
                });
    }
}
