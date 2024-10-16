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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    //Administrator admin = new Administrator();
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
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        signupLink = findViewById(R.id.signupLink);

        SharedPreferences preferences = getSharedPreferences("YourAppPreferences", MODE_PRIVATE);
        String userEmail = preferences.getString("UserEmail", null);


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
            loginPassword.requestFocus();
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
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();

                            db.collection("users").document(userId).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String role = documentSnapshot.getString("role");

                                            if ("attendee".equals(role)) {
                                                startActivity(new Intent(LoginActivity.this, AttendeeHomepageActivity.class));
                                            } else if ("organizer".equals(role)) {
                                                startActivity(new Intent(LoginActivity.this, OrganizerHomepageActivity.class));
                                            }
                                            else{
                                                startActivity(new Intent(LoginActivity.this, AdminHomepageActivity.class));
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    // need to add implementation of storing user's email after successful login!
    // use this code so i can clear the SharedPreference value when user logs out
    // SharedPreferences preferences = getSharedPreferences("YourAppPreferences", MODE_PRIVATE);
    // SharedPreferences.Editor editor = preferences.edit();
    // editor.putString("UserEmail", user.getEmail());
    // editor.apply();
}