package com.example.eams_project_fall2024;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private TextView loginLink;
    private Button signUpAttendeeButton;
    private Button signUpOrganizerButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        loginLink = findViewById(R.id.loginButton);
        signUpAttendeeButton = findViewById(R.id.signUpAttendeeButton);
        signUpOrganizerButton = findViewById(R.id.signUpOrganizerButton);

        loginLink.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        signUpAttendeeButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, AttendeeSignUpActivity.class));
        });

        signUpOrganizerButton.setOnClickListener(view ->{
            startActivity(new Intent(MainActivity.this, OrganizerSignUpActivity.class));
        });

    }

}