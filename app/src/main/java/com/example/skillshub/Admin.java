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
    TextView users, client, workers, Category, nic,business;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        db = FirebaseFirestore.getInstance();
        users = findViewById(R.id.users);
        client = findViewById(R.id.client);
        workers = findViewById(R.id.workers);
        nic = findViewById(R.id.nic);
        Category = findViewById(R.id.category);
        business = findViewById(R.id.business);


        fetchUserCount();
        fetchClientCount();
        fetchWorkerCount();
        fetchUnverifiedNicWorkerCount();
        fetchUnverifiedBusinessRegistrationsWorkerCount();

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
                .whereEqualTo("role", "worker") // Filter by role field with value "worker"
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null) {
                            int clientCount = snapshot.size();
                            workers.setText(String.valueOf(clientCount));
                        }
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());

                    }
                });
    }

    private void fetchUnverifiedNicWorkerCount() {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        final int[] count = {0};
                        for (QueryDocumentSnapshot userDoc : task.getResult()) {
                            userDoc.getReference()
                                    .collection("workerInformation")
                                    .whereEqualTo("isNicVerified", false)
                                    .get()
                                    .addOnCompleteListener(subTask -> {
                                        if (subTask.isSuccessful()) {
                                            QuerySnapshot workerInfoSnapshot = subTask.getResult();
                                            if (workerInfoSnapshot != null && !workerInfoSnapshot.isEmpty()) {
                                                count[0] += workerInfoSnapshot.size();
                                                nic.setText(String.valueOf(count[0]));
                                            }
                                        } else {
                                            Log.w("FirestoreError", "Error accessing workerInformation", subTask.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.w("FirestoreError", "Error accessing users collection", task.getException());
                    }
                });
    }
    private void fetchUnverifiedBusinessRegistrationsWorkerCount() {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        final int[] count = {0};
                        for (QueryDocumentSnapshot userDoc : task.getResult()) {
                            userDoc.getReference()
                                    .collection("workerInformation")
                                    .whereEqualTo("isBrVerified", false)
                                    .get()
                                    .addOnCompleteListener(subTask -> {
                                        if (subTask.isSuccessful()) {
                                            QuerySnapshot workerInfoSnapshot = subTask.getResult();
                                            if (workerInfoSnapshot != null && !workerInfoSnapshot.isEmpty()) {
                                                count[0] += workerInfoSnapshot.size();
                                                business.setText(String.valueOf(count[0]));
                                            }
                                        } else {
                                            Log.w("FirestoreError", "Error accessing workerInformation", subTask.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.w("FirestoreError", "Error accessing users collection", task.getException());
                    }
                });
    }
}
