package com.example.skillshub;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.skillshub.signupform.RegistrationControlActivity;

public class
ChooseUserActivity extends AppCompatActivity {

    View view;
    Button signupAsCilent;
    Button signupAsWorker;
    ImageView chooseRoleBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_user);

        signupAsCilent = findViewById(R.id.signup_as_client);
        signupAsWorker = findViewById(R.id.signup_as_worker);
        chooseRoleBack = findViewById(R.id.signup_back_btn);

        chooseRoleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        signupAsCilent.setOnClickListener(new View.OnClickListener() {
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
                finish();
            }
        });
    }

    private void startRegistrationActivity(String registrationType) {
        Intent intent = new Intent(ChooseUserActivity.this, RegistrationControlActivity.class);
        intent.putExtra("REGISTRATION_TYPE", registrationType);
        startActivity(intent);
    }
}