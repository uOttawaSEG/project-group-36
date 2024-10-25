package com.example.eams_project_fall2024;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Administrator {
    private String adminUsername;
    private String adminPassword;
    private FirebaseFirestore db;

    public Administrator(String adminUsername, String adminPassword) {
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.db = FirebaseFirestore.getInstance();
    }

    public void approveUserRegistration(String userId) {
        db.collection("userRequests").document(userId)
                .update("status", "approved")
                .addOnSuccessListener(aVoid -> {
                    System.out.println("User registration approved.");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error approving registration: " + e.getMessage());
                });
    }

    public void rejectUserRegistration(String userId) {
        db.collection("userRequests").document(userId)
                .update("status", "rejected")
                .addOnSuccessListener(aVoid -> {
                    System.out.println("User registration rejected.");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error rejecting registration: " + e.getMessage());
                });
    }

    public void showRejectedRequests(RejectionCallback callback) {
        db.collection("userRequests")
                .whereEqualTo("status", "rejected")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String userId = document.getId();
                                Map<String, Object> requestData = document.getData();
                                callback.onRejectedRequestFound(userId, requestData);
                            }
                        } else {
                            callback.onNoRejectedRequestsFound();
                        }
                    } else {
                        callback.onError(task.getException().getMessage());
                    }
                });
    }

    public void approveRejectedRequest(String userId) {
        db.collection("userRequests").document(userId)
                .update("status", "approved")
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Previously rejected user request approved.");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error approving rejected request: " + e.getMessage());
                });
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

    public interface RejectionCallback {
        void onRejectedRequestFound(String userId, Map<String, Object> requestData);
        void onNoRejectedRequestsFound();
        void onError(String errorMessage);
    }
}
