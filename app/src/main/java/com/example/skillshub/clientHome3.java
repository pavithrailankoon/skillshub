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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skillshub.adapters.SubCategoryAdapter;
import com.example.skillshub.adapters.WorkerListAdapter;
import com.example.skillshub.firebaseModel.FirebaseStoarageManager;
import com.example.skillshub.firebaseModel.ReadData;
import com.example.skillshub.model.Worker;
import com.example.skillshub.signupform.RegistrationControlActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class clientHome3 extends AppCompatActivity {

    private CircleImageView profileImageButton;
    private ImageButton filterButton;
    private TextView categoryPath;
    private ImageView backButton;
    private ProgressBar progressBar;
    private ImageView refresh;
    private Button button;
    private String recievedSubSkill;

    private ReadData readData;
    private ListView workerListView;
    private List<Worker> workerList = new ArrayList<>();
    private WorkerListAdapter workerListAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_home2);

        readData = new ReadData();
        recievedSubSkill = getIntent().getStringExtra("SELECTED_SUB_CATEGORY");
        refresh  = (ImageView) findViewById(R.id.refresh_category);
        progressBar = findViewById(R.id.progressbar);
        backButton = findViewById(R.id.client2_back);
        categoryPath = findViewById(R.id.main_category_path);
        categoryPath.setText(recievedSubSkill);

        workerListView = findViewById(R.id.listView2);

        // Set up the custom adapter
        workerListAdapter = new WorkerListAdapter(this, workerList);
        workerListView.setAdapter(workerListAdapter);

        profileImageButton = (CircleImageView) findViewById(R.id.avatar);
        profileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                       Toast.makeText(clientHome3.this, "Client profile", Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(clientHome3.this, ClientProfileActivity.class);
                       startActivity(intent);
                   }
               });

        refresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(clientHome3.this, "Refreshing available workers", Toast.LENGTH_SHORT).show();
                getWorkerList();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        categoryPath.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        filterButton = (ImageButton) findViewById(R.id.filter_button);

        filterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(clientHome3.this, "Filter", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(clientHome3.this, FilterActivity.class);
                startActivity(intent);
            }
        });

        button = (Button) findViewById(R.id.becomeWorkerButton);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(clientHome3.this, RegistrationControlActivity.class);
                intent.putExtra("REGISTRATION_TYPE", "clienttoworker");
                startActivity(intent);
                Toast.makeText(clientHome3.this, "Fill the verification information", Toast.LENGTH_SHORT).show();
            }
        });

        getWorkerList();

        // Set the OnItemClickListener for the ListView
        workerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from the categoryList
                //String selectedWorkerUID = workerList.get(position);

                // Create an Intent to start the new Activity
                //Intent intent = new Intent(clientHome3.this, WorkerProfileView.class);

                // Pass the selected text to the new Activity using putString
                //intent.putExtra("SELECTED_WORKER", selectedWorker);

                // Start the new Activity
                //startActivity(intent);
            }
        });
    }

    private void getWorkerList(){
        // Call the method to retrieve workers by subcategory
        readData.getWorkersBySubcategory(recievedSubSkill, new ReadData.FirestoreWorkerCallback() {
            @Override
            public void onWorkerDataRetrieved(List<Worker> workers) {
                runOnUiThread(() -> {
                    if (workers != null) {
                        workerList.clear();
                        workerList.addAll(workers);
                        workerListAdapter.notifyDataSetChanged();

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

    private void setUserAvatar(){
        FirebaseStoarageManager imageManager = new FirebaseStoarageManager();

        // Load the profile image once
        imageManager.loadProfileImage(this, profileImageButton);
    }
}