package com.example.skillshub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class Introduction extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);  // Ensure this matches your layout file name

        // Initialize the button
        Button continueButton = findViewById(R.id.continueButton);

        // Set an OnClickListener to the button
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start NextActivity
                Intent intent = new Intent(Introduction.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}
