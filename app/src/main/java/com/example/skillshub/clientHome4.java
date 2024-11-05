package com.example.skillshub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.skillshub.adapters.WorkerListAdapter;
import com.example.skillshub.firebaseModel.ReadData;
import com.example.skillshub.model.Worker;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class clientHome4 extends AppCompatActivity {

    private ImageButton filterButton;
    private ImageView backButton, refresh;
    private ProgressBar progressBar;
    private String recievedCity, recivedFilterSubCategory;

    private FirebaseFirestore db;

    private ReadData readData;
    private ListView workerListView;
    private List<Worker> workerList = new ArrayList<>();
    private WorkerListAdapter workerListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home4);

        db = FirebaseFirestore.getInstance();
        readData = new ReadData();
        recivedFilterSubCategory = getIntent().getStringExtra("FILTERED_SUB_CATEGORY");
        recievedCity = getIntent().getStringExtra("FILTERED_CITY");

        Log.d("City", recievedCity);
        Log.d("subcategory", recivedFilterSubCategory);

        queryUsersByCityAndSubcategory(recievedCity, recivedFilterSubCategory);

        // Initialize views
        refresh = findViewById(R.id.refresh_category);
        //progressBar = findViewById(R.id.progressbarfilter);
        backButton = findViewById(R.id.client2_back);

        workerListView = findViewById(R.id.listView2);

        workerListAdapter = new WorkerListAdapter(this, workerList);
        workerListView.setAdapter(workerListAdapter);

        refresh.setOnClickListener(v -> {
            Toast.makeText(clientHome4.this, "Refreshing available workers", Toast.LENGTH_SHORT).show();
            queryUsersByCityAndSubcategory(recievedCity, recivedFilterSubCategory);
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        filterButton = findViewById(R.id.filter_button);
        filterButton.setOnClickListener(v -> {
            Toast.makeText(clientHome4.this, "Filter", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(clientHome4.this, FilterActivity.class));
        });

        // Set the OnItemClickListener for the ListView
        workerListView.setOnItemClickListener((parent, view, position, id) -> {
            Worker selectedWorkerUid = workerList.get(position);
            String workerUid = selectedWorkerUid.getUid();
            Intent intent = new Intent(clientHome4.this, WorkerProfileView.class);
            intent.putExtra("SELECTED_WORKER", workerUid);
            startActivity(intent);
        });
    }

    private void queryUsersByCityAndSubcategory(String city, String subcategory) {
        //progressBar.setVisibility(View.VISIBLE);
        workerList.clear();

        db.collection("users")
                .whereEqualTo("city", city)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String userId = document.getId();
                            querySubcollectionBySubcategory(userId, subcategory);
                            Log.d("Firestore", "found worker.");
                            Log.d("UserId", userId);
                        }
                    } else {
                        Log.d("Firestore", "No users found in the specified city.");
                        Toast.makeText(clientHome4.this, "No users found in the specified city", Toast.LENGTH_LONG).show();
                        //progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error getting users by city", e);
                    Toast.makeText(clientHome4.this, "Error getting users by city", Toast.LENGTH_SHORT).show();
                    //progressBar.setVisibility(View.GONE);
                });
    }


    private void querySubcollectionBySubcategory(String userId, String subcategory) {
        db.collection("users")
                .document(userId)
                .collection("workerProfiles")
                .whereArrayContains("subcategories", subcategory)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        workerList.clear();

                        for (QueryDocumentSnapshot document : querySnapshot) {
                            // Extract the necessary fields from the document to create a Worker object
                            DocumentReference documentReference = db.collection("users").document(userId);

                            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String name = documentSnapshot.getString("fullName");
                                    String city = documentSnapshot.getString("city");
                                    String district = documentSnapshot.getString("district");
                                    String profileUrl = documentSnapshot.getString("profileImageURL");

                                    Worker worker = new Worker(userId, name, district, city, profileUrl);
                                    workerList.add(worker);
                                    workerListAdapter.notifyDataSetChanged();
                                }
                            }).addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show();
                            });
                        }

                        //runOnUiThread(() -> {

                        //});

                    } else {
                        Log.d("Firestore", "No matching subcategories found in subcollection for user " + userId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error querying subcollection for user " + userId, e);
                    Toast.makeText(clientHome4.this, "Can not retrieve data. Please refresh.", Toast.LENGTH_SHORT).show();
                });
    }

}

