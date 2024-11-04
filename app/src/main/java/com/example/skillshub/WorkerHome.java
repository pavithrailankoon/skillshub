package com.example.skillshub;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skillshub.adapters.CategoryAdapter;
import com.example.skillshub.firebaseModel.ReadData;
import com.example.skillshub.signupform.RegistrationControlActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class WorkerHome extends AppCompatActivity {

    private ImageButton filterButton;
    private CircleImageView profileImageButton;
    private ImageView refresh;
    private ProgressBar progressBar;
    ProgressDialog progressDialog;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    String uid;

    private ReadData readData;
    private ListView categoryListView;
    private ArrayList<String> categoryList;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_worker_home);

        progressDialog = new ProgressDialog(this);
        readData = new ReadData();
        progressBar = findViewById(R.id.progressbar);
        filterButton = (ImageButton) findViewById(R.id.filter_button);
        refresh  = (ImageView) findViewById(R.id.refresh_category);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        // Initialize the ListView and ArrayList
        categoryListView = findViewById(R.id.listView1);
        categoryList = new ArrayList<>();

        // Set up the custom adapter
        categoryAdapter = new CategoryAdapter(this, categoryList);
        categoryListView.setAdapter(categoryAdapter);

        refresh.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(WorkerHome.this, "Refreshing categories...", Toast.LENGTH_SHORT).show();
                getUniqueMainSkills();
            }

        });

        filterButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(WorkerHome.this, "Filter workers", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WorkerHome.this, FilterActivity.class);
                startActivity(intent);
            }

        });

        profileImageButton = (CircleImageView) findViewById(R.id.avatar);
        profileImageButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkerHome.this, WorkerProfile.class);
                startActivity(intent);
            }

        });

        setProfileImage();
        getUniqueMainSkills();
        categoryClickListener();

    }

    private void categoryClickListener() {
        progressBar.setVisibility(View.VISIBLE);
        // Set the OnItemClickListener for the ListView
        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from the categoryList
                String selectedCategory = categoryList.get(position);

                // Create an Intent to start the new Activity
                Intent intent = new Intent(WorkerHome.this, clientHome2.class);

                // Pass the selected text to the new Activity using putString
                intent.putExtra("SELECTED_CATEGORY", selectedCategory);

                // Start the new Activity
                startActivity(intent);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getUniqueMainSkills(){
        progressBar.setVisibility(View.VISIBLE);
        readData.fetchUniqueCategoryNames(new ReadData.FirestoreCallback() {
            @Override
            public void onSuccess(ArrayList<String> categories) {
                // Run on the UI thread
                runOnUiThread(() -> {
                    try {
                        // Clear the existing data
                        categoryList.clear();
                        // Add the new data to the list
                        categoryList.addAll(categories);
                        // Notify the adapter that the data set has changed
                        categoryAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    } catch (Exception e) {
                        // Log the exception for debugging
                        Log.e("CategoryListActivity", "Error updating list: " + e.getMessage());
                        Toast.makeText(WorkerHome.this, "Can not retrive. Refresh" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                // Show an error message if something went wrong
                Toast.makeText(WorkerHome.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
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
                    Toast.makeText(WorkerHome.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(WorkerHome.this, "Error retrieving user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}