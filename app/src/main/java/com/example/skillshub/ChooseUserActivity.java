package com.example.skillshub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skillshub.signupform.AuthenticateActivity;
import com.example.skillshub.signupform.RegistrationControlActivity;

public class ChooseUserActivity extends AppCompatActivity {

    Button signupAsClient;
    Button signupAsWorker;
    ImageView chooseRoleBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_user);

        signupAsClient = findViewById(R.id.signup_as_client);
        signupAsWorker = findViewById(R.id.signup_as_worker);
        chooseRoleBack = findViewById(R.id.signup_back_btn);

        chooseRoleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseUserActivity.this, AuthenticateActivity.class);
                startActivity(intent);
            }
        });

        signupAsClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegistrationActivity("client");
                Toast.makeText(ChooseUserActivity.this, "You are registering as a client", Toast.LENGTH_SHORT).show();
            }
        });

        signupAsWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegistrationActivity("worker");
                Toast.makeText(ChooseUserActivity.this, "You are registering as a worker", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle system back button press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ChooseUserActivity.this, AuthenticateActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startRegistrationActivity(String registrationType) {
        Intent intent = new Intent(ChooseUserActivity.this, RegistrationControlActivity.class);
        intent.putExtra("REGISTRATION_TYPE", registrationType);
        startActivity(intent);
    }
}