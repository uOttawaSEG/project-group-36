package com.example.eams_project_fall2024;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private static final String STATUS_APPROVED = "approved";
    private static final String STATUS_REJECTED = "rejected";
    private static final String STATUS_PENDING = "pending";

    private TextView linkToSignup;
    private EditText emailInput, passwordInput;
    private FirebaseAuth firebaseAuth;
    private Button loginBtn;
    private FirebaseFirestore firestoreDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply background color adjustments to system bars
        getWindow().setStatusBarColor(android.graphics.Color.rgb(30, 30, 30));
        getWindow().setNavigationBarColor(android.graphics.Color.rgb(30, 30, 30));
        setContentView(R.layout.activity_login);

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        firestoreDb = FirebaseFirestore.getInstance();

        // UI components setup
        emailInput = findViewById(R.id.loginEmail);
        passwordInput = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginButton);
        linkToSignup = findViewById(R.id.signupLink);

        // Setup listeners
        linkToSignup.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, MainActivity.class)));
        loginBtn.setOnClickListener(view -> initiateLogin());
    }

    private void initiateLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate fields before attempting login
        if (!areFieldsValid(email, password)) return;

        // Attempt Firebase authentication
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            retrieveUserDetails(user.getUid());
                        }
                    } else {
                        showToast("Login unsuccessful. Check credentials and try again.");
                    }
                });
    }

    private boolean areFieldsValid(String email, String password) {
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email");
            emailInput.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return false;
        }
        return true;
    }

    private void retrieveUserDetails(String userId) {
    DocumentReference userDocRef = firestoreDb.collection("users").document(userId);
    userDocRef.get().addOnCompleteListener(task -> {
        if (task.isSuccessful() && task.getResult() != null) {
            DocumentSnapshot userDocument = task.getResult();
            if (userDocument.exists()) {
                String userRole = userDocument.getString("role");
                String registrationStatus = userDocument.getString("status");
                String adminUsername = userDocument.getString("email");

                if ("admin".equals(userRole)) {
                    openHomepage(userRole);
                } else {
                    processLoginStatus(userRole, registrationStatus, userDocument.getString("email"));
                }
            } else {
                showToast("ERROR: Document not found.");
            }
        } else {
            showToast("ERROR: Issue retrieving user data.");
        }
    });
}

    private void processLoginStatus(String role, String status, String email) {
        switch (status) {
            case STATUS_APPROVED:
                openHomepage(role);
                break;

            case STATUS_REJECTED:
                displayRejectedAlert();
                break;

            case STATUS_PENDING:
                displayPendingAlert();
                break;

            default:
                showToast("ERROR: Registration status undefined.");
                break;
        }
    }

    private void openHomepage(String role) {
        Intent intent;
        switch (role) {
            case "organizer":
                intent = new Intent(LoginActivity.this, OrganizerHomepageActivity.class);
                break;
            case "attendee":
                intent = new Intent(LoginActivity.this, AttendeeHomepageActivity.class);
                break;
            case "admin":
                intent = new Intent(LoginActivity.this, AdminHomepageActivity.class);
                break;
            default:
                showToast("ERROR.");
                return;
        }
        startActivity(intent);
        finish();
    }

    private void displayRejectedAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Registration Rejected")
                .setMessage("Your registration was rejected. Please contact the Administrator at 123-456-7890.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void displayPendingAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Registration Pending")
                .setMessage("Your registration is pending approval. Please check back later.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}





