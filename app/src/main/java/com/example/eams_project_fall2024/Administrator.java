package com.example.eams_project_fall2024;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

<<<<<<< HEAD
public class Administrator extends AppCompatActivity {
   /* private String adminUsername;
=======
public class Administrator {
    private String adminUsername;
>>>>>>> 9264a1d3887a2a13863ce327a306566dfb4ba7ff
    private FirebaseFirestore db;

    public Administrator(String adminUsername) {
        this.adminUsername = adminUsername;
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
        if (callback == null) {
            throw new IllegalArgumentException("Callback cannot be null");
        }
        
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
<<<<<<< HEAD
    }*/
=======
    }

    public interface RejectionCallback {
        void onRejectedRequestFound(String userId, Map<String, Object> requestData);
        void onNoRejectedRequestsFound();
        void onError(String errorMessage);
    }
>>>>>>> 9264a1d3887a2a13863ce327a306566dfb4ba7ff
}
