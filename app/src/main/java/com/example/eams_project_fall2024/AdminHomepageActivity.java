package com.example.eams_project_fall2024;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminHomepageActivity extends AppCompatActivity {
    private Administrator administrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_homepage);

        // Assuming you store the admin username in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("YourAppPreferences", MODE_PRIVATE);
        String adminUsername = preferences.getString("adminUsername", null);

        // Initialize the Administrator instance
        if (adminUsername != null) {
            administrator = new Administrator(adminUsername);
        } else {
            // Handle the case where adminUsername is not found (e.g., show an error or redirect)
        }
    }

    // Example method to approve user registration
    public void approveUser(View view) {
        String userId = "userId123"; // Replace with actual user ID you want to approve
        administrator.approveUserRegistration(userId);
    }

    public void logout(View view) {
        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut();

        // Clear shared preferences
        SharedPreferences preferences = getSharedPreferences("YourAppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
