package com.example.eams_project_fall2024;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OrganizerSignUpActivity extends AppCompatActivity {
    private TextView organizerLoginLink;
    private TextView attendeeSignupLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organizer_sign_up);

        organizerLoginLink = findViewById(R.id.organizerLoginLink);
        attendeeSignupLink = findViewById(R.id.attendeeSignupLink);

        organizerLoginLink.setOnClickListener(view -> {
            startActivity(new Intent(OrganizerSignUpActivity.this, LoginActivity.class));
        });

        attendeeSignupLink.setOnClickListener(view -> {
            startActivity(new Intent(OrganizerSignUpActivity.this, AttendeeSignUpActivity.class));
        });

    }
}