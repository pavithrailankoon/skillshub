package com.example.skillshub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class Admin extends AppCompatActivity {

    Button logoutBtn;
    FirebaseFirestore db;
    TextView users, client, workers;
    private int totalWorkerCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        db = FirebaseFirestore.getInstance();
        users = findViewById(R.id.users);
        client = findViewById(R.id.client);
        workers = findViewById(R.id.workers);


        fetchUserCount();
        fetchClientCount();
        fetchWorkerCount();

        logoutBtn = findViewById(R.id.logoutBtn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void fetchUserCount() {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null) {
                            int userCount = snapshot.size();
                            users.setText(String.valueOf(userCount));
                        }
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());

                    }
                });
    }

    private void fetchClientCount() {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null) {
                            int clientCount = snapshot.size();
                            client.setText(String.valueOf(clientCount));
                        }
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());

                    }
                });
    }

    private void fetchWorkerCount() {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Loop through each user document
                        for (DocumentSnapshot userDocument : task.getResult()) {
                            CollectionReference workerProfilesRef = userDocument.getReference().collection("workerProfiles");

                            // Get workerProfiles subcollection for each user
                            workerProfilesRef.get().addOnCompleteListener(workerTask -> {
                                if (workerTask.isSuccessful()) {
                                    // Count documents in the subcollection
                                    int workerCount = workerTask.getResult().size();
                                    totalWorkerCount += workerCount;

                                    // Optionally, update UI here if you want live count updates
                                    workers.setText(String.valueOf(workerCount));
                                } else {
                                    Toast.makeText(this, "Failed to count workers", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(this, "Failed to get users", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
