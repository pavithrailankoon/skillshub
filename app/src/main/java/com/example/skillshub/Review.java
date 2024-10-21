package com.example.skillshub;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skillshub.signupform.Part1Fragment;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Review extends AppCompatActivity {
    RatingBar ratingBar;
    Button clear, submit;
    EditText editText;
    TextView textView;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review);

        ratingBar = findViewById(R.id.ratingBar);
        clear = findViewById(R.id.submit);
        submit = findViewById(R.id.clear);
        editText = findViewById(R.id.editTextTextMultiLine);
        textView = findViewById(R.id.textView15);

        auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        }

    }




