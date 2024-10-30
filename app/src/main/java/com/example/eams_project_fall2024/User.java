package com.example.eams_project_fall2024;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class User {
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String password;
    protected String phoneNumber;
    protected String address;

    protected User(String firstName, String lastName, String email, String password, String phoneNumber, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getName() {
        return firstName;
    }

    public void login() {}

    public void logout() {}

    // New method for fetching and displaying user details
    public static void fetchAndDisplayUserDetails(Context context, String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(documentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        String email = documentSnapshot.getString("email");
                        String phone = documentSnapshot.getString("phone");
                        String address = documentSnapshot.getString("address");
                        String organizationName = documentSnapshot.getString("organizationName");
                        String role = documentSnapshot.getString("role");

                        String name = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
                        email = (email != null) ? email : "Not available";
                        phone = (phone != null) ? phone : "Not available";
                        address = (address != null) ? address : "Not available";
                        organizationName = (role.equals("organizer") && organizationName != null) ? organizationName : "";
                        role = (role != null) ? role : "Not specified";

                        String message = "Name: " + name + "\n" +
                                "Email: " + email + "\n" +
                                "Phone: " + phone + "\n" +
                                "Address: " + address + "\n" +
                                (role.equals("organizer") ? "Organization: " + organizationName + "\n" : "") +
                                "Role: " + role;

                        new AlertDialog.Builder(context)
                                .setTitle("User Details")
                                .setMessage(message)
                                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                                .show();
                    } else {
                        new AlertDialog.Builder(context)
                                .setTitle("User Not Found")
                                .setMessage("User details could not be found.")
                                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                                .show();
                    }
                })
                .addOnFailureListener(e -> new AlertDialog.Builder(context)
                        .setTitle("Error")
                        .setMessage("Failed to retrieve user details: " + e.getMessage())
                        .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                        .show());
    }
}

