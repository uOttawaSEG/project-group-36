package com.example.eams_project_fall2024;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthException;

public class Administrator {
    private String adminUsername;
    private String adminPassword;

    public Administrator(String adminUsername, String adminPassword) {
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    public void approveUserRegistration() {
    }

    public void rejectUserRegistration() {
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void login(String email, String password, LoginCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            if (user.getEmail().equals(adminUsername)) {
                                callback.onLoginSuccess();
                            } else {
                                callback.onLoginFailure("User is not an administrator.");
                            }
                        } else {
                            callback.onLoginFailure("No user found.");
                        }
                    } else {
                        callback.onLoginFailure("Authentication failed: " + task.getException().getMessage());
                    }
                });
    }

    public interface LoginCallback {
        void onLoginSuccess();
        void onLoginFailure(String errorMessage);
    }
}
