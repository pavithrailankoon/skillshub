package com.example.skillshub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.skillshub.adapters.WorkerListAdapter;
import com.example.skillshub.firebaseModel.FirebaseStorageManager; // Corrected class name
import com.example.skillshub.firebaseModel.ReadData;
import com.example.skillshub.model.Worker;
import com.example.skillshub.signupform.RegistrationControlActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class clientHome3 extends AppCompatActivity {

    private ImageButton filterButton;
    private ImageView backButton, refresh;
    private ProgressBar progressBar;
    private String receivedSubSkill;

    private FirebaseFirestore db;

    private ReadData readData;
    private ListView workerListView;
    private List<Worker> workerList = new ArrayList<>();
    private WorkerListAdapter workerListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home3);

        db = FirebaseFirestore.getInstance();
        readData = new ReadData();
        receivedSubSkill = getIntent().getStringExtra("SELECTED_SUB_CATEGORY");

        // Initialize views
        refresh = findViewById(R.id.refresh_category);
        progressBar = findViewById(R.id.progressbar);
        backButton = findViewById(R.id.client2_back);

        workerListView = findViewById(R.id.listView2);

        // Set up the custom adapter
        workerListAdapter = new WorkerListAdapter(this, workerList);
        workerListView.setAdapter(workerListAdapter);

        getWorkerList();

        refresh.setOnClickListener(v -> {
            Toast.makeText(clientHome3.this, "Refreshing available workers", Toast.LENGTH_SHORT).show();
            getWorkerList();
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        filterButton = findViewById(R.id.filter_button);
        filterButton.setOnClickListener(v -> {
            Toast.makeText(clientHome3.this, "Filter", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(clientHome3.this, FilterActivity.class));
        });

        // Set the OnItemClickListener for the ListView
        workerListView.setOnItemClickListener((parent, view, position, id) -> {
            Worker selectedWorkerUid = workerList.get(position);
            String workerUid = selectedWorkerUid.getUid();
            Intent intent = new Intent(clientHome3.this, WorkerProfileView.class);
            intent.putExtra("SELECTED_WORKER", workerUid);
            startActivity(intent);
        });

    }

    private void getWorkerList() {
        progressBar.setVisibility(View.VISIBLE); // Show loading indicator
        readData.getWorkersBySubcategory(receivedSubSkill, new ReadData.FirestoreWorkerCallback() {
            @Override
            public void onWorkerDataRetrieved(List<Worker> workers) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE); // Hide loading indicator
                    if (workers != null) {

                        runOnUiThread(() -> {
                            try {
                                workerList.clear();
                                workerList.addAll(workers);
                                workerListAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                // Log the exception for debugging
                                Log.e("CategoryListActivity", "Error updating list: " + e.getMessage());
                                Toast.makeText(clientHome3.this, "Can not retrive. Refresh" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                        if (workers.isEmpty()) {
                            Toast.makeText(clientHome3.this, "No workers found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(clientHome3.this, "Failed to retrieve workers.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

