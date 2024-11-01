package com.example.skillshub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.skillshub.adapters.WorkerListAdapter;
import com.example.skillshub.firebaseModel.FirebaseStorageManager;
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

public class clientHome4 extends AppCompatActivity {

    private CircleImageView profileImageButton;
    private ImageButton filterButton;
    private TextView categoryPath;
    private ImageView backButton, refresh;
    private ProgressBar progressBar;
    private Button button;
    private String receivedSubSkill;
    private FirebaseFirestore db;

    String mainCategory, subCategory, district, city;
    private ReadData readData;
    private ListView workerListView;
    private List<Worker> workerList = new ArrayList<>();
    private WorkerListAdapter workerListAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home3);

        db = FirebaseFirestore.getInstance();
        readData = new ReadData();
        receivedSubSkill = getIntent().getStringExtra("SELECTED_SUB_CATEGORY");

        // Get filter parameters from intent
        mainCategory = getIntent().getStringExtra("mainCategory");
        subCategory = getIntent().getStringExtra("subCategory");
        district = getIntent().getStringExtra("district");
        city = getIntent().getStringExtra("city");

        filterWorkers(mainCategory, subCategory, district, city);

        // Initialize views
        refresh = findViewById(R.id.refresh_category);
        progressBar = findViewById(R.id.progressbar);
        backButton = findViewById(R.id.client2_back);
        categoryPath = findViewById(R.id.main_category_path);
        categoryPath.setText(receivedSubSkill);

        workerListView = findViewById(R.id.listView2);

        // Set up the custom adapter
        workerListAdapter = new WorkerListAdapter(this, workerList);
        workerListView.setAdapter(workerListAdapter);

        profileImageButton = findViewById(R.id.avatar);
        profileImageButton.setOnClickListener(v -> {
            Toast.makeText(clientHome4.this, "Client profile", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(clientHome4.this, ClientProfileActivity.class));
        });

        refresh.setOnClickListener(v -> {
            Toast.makeText(clientHome4.this, "Refreshing available workers", Toast.LENGTH_SHORT).show();
        });

        backButton.setOnClickListener(v -> onBackPressed());

        categoryPath.setOnClickListener(v -> onBackPressed());

        filterButton = findViewById(R.id.filter_button);
        filterButton.setOnClickListener(v -> {
            Toast.makeText(clientHome4.this, "Filter", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(clientHome4.this, FilterActivity.class));
        });

        button = findViewById(R.id.becomeWorkerButton);
        button.setOnClickListener(view -> {
            Intent intent = new Intent(clientHome4.this, RegistrationControlActivity.class);
            intent.putExtra("REGISTRATION_TYPE", "clienttoworker");
            startActivity(intent);
            Toast.makeText(clientHome4.this, "Fill the verification information", Toast.LENGTH_SHORT).show();
        });

        // Set the OnItemClickListener for the ListView
        workerListView.setOnItemClickListener((parent, view, position, id) -> {
            Worker selectedWorkerUid = workerList.get(position);
            String workerUid = selectedWorkerUid.getUid();
            Intent intent = new Intent(clientHome4.this, WorkerProfileView.class);
            intent.putExtra("SELECTED_WORKER", workerUid);
            startActivity(intent);
        });

        setProfileImage();
    }

    private void filterWorkers(String mainCategory, String subCategory, String district, String city) {
        db.collection("users").addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w("ClientHome3Activity", "Listen failed.", e);
                return;
            }

            if (queryDocumentSnapshots != null) {
                workerList.clear();
                for (DocumentSnapshot userDoc : queryDocumentSnapshots.getDocuments()) {
                    String userDistrict = userDoc.getString("district");
                    String userCity = userDoc.getString("city");

                    // Verify if district and city match
                    if (district.equals(userDistrict) && city.equals(userCity)) {
                        userDoc.getReference().collection("workerProfiles")
                                .whereEqualTo("mainCategory", mainCategory)
                                .whereArrayContains("subCategories", subCategory)
                                .get().addOnSuccessListener(profileQuery -> {
                                    for (DocumentSnapshot profileDoc : profileQuery.getDocuments()) {
                                        Worker worker = profileDoc.toObject(Worker.class);
                                        if (worker != null) {
                                            workerList.add(worker);
                                        }
                                    }
                                    workerListAdapter.notifyDataSetChanged();
                                });
                    }
                }
            }
        });
    }

    private void setProfileImage() {
        readData.getUserFields(new ReadData.FirestoreUserDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> userData) {
                if (userData != null) {
                    String profileImageURL = userData.getOrDefault("profileImageURL", "No profile image available").toString();

                    if (!profileImageURL.isEmpty()) {
                        Picasso.get()
                                .load(profileImageURL)
                                .placeholder(R.drawable.avatar)
                                .error(R.drawable.avatar)
                                .into(profileImageButton);
                    } else {
                        profileImageButton.setImageResource(R.drawable.avatar);
                    }
                } else {
                    Toast.makeText(clientHome4.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(clientHome4.this, "Error retrieving user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
