package com.example.eams_project_fall2024;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
    private TextView signupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        SharedPreferences preferences = getSharedPreferences("YourAppPreferences", MODE_PRIVATE);
        String userEmail = preferences.getString("UserEmail", null);

        signupLink = findViewById(R.id.signupLink);

        signupLink.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        });
    }

    // need to add implementation of storing user's email after successful login!
    // use this code so i can clear the SharedPreference value when user logs out
    // SharedPreferences preferences = getSharedPreferences("YourAppPreferences", MODE_PRIVATE);
    // SharedPreferences.Editor editor = preferences.edit();
    // editor.putString("UserEmail", user.getEmail());
    // editor.apply();
}