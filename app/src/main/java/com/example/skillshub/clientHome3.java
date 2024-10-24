package com.example.skillshub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skillshub.adapters.SubCategoryAdapter;
import com.example.skillshub.firebaseModel.FirebaseStoarageManager;
import com.example.skillshub.firebaseModel.ReadData;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class clientHome3 extends AppCompatActivity {

    private CircleImageView profileImageButton;
    private ImageButton filterButton;
    private TextView categoryPath;
    private ImageView backButton;
    private ProgressBar progressBar;
    private ImageView refresh;
    private String recievedMainSkill;

    private ReadData readData;
    private ListView categoryListView;
    private ArrayList<String> subCategoryList;
    private SubCategoryAdapter subCategoryAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_home2);

        readData = new ReadData();
        recievedMainSkill = getIntent().getStringExtra("SELECTED_CATEGORY");
        refresh  = (ImageView) findViewById(R.id.refresh_category);
        progressBar = findViewById(R.id.progressbar);
        backButton = findViewById(R.id.client2_back);
        categoryPath = findViewById(R.id.main_category_path);
        categoryPath.setText(recievedMainSkill);

        categoryListView = findViewById(R.id.listView2);
        subCategoryList = new ArrayList<>();

        // Set up the custom adapter
        subCategoryAdapter = new SubCategoryAdapter(this, subCategoryList);
        categoryListView.setAdapter(subCategoryAdapter);

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
                Toast.makeText(clientHome3.this, "Refreshing sub-categories...", Toast.LENGTH_SHORT).show();
                getUniqueSubSkills();
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

        getUniqueSubSkills();

        // Set the OnItemClickListener for the ListView
        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from the categoryList
                String selectedSubCategory = subCategoryList.get(position);

                // Create an Intent to start the new Activity
                //Intent intent = new Intent(clientHome2.this, clientHome2.class);

                // Pass the selected text to the new Activity using putString
                //intent.putExtra("SELECTED_SUB_CATEGORY", selectedSubCategory);

                // Start the new Activity
                //startActivity(intent);
            }
        });
    }

    private void getUniqueSubSkills() {
        progressBar.setVisibility(View.VISIBLE);
        readData.fetchUniqueSubcategories(recievedMainSkill, new ReadData.FirestoreSubSkillCallback() {
            @Override
            public void onSuccess(ArrayList<String> subcategoryNames) {
                // Handle the successful retrieval of subcategory names
                // Run on the UI thread
                runOnUiThread(() -> {
                    try {
                        // Clear the existing data
                        subCategoryList.clear();
                        // Add the new data to the list
                        subCategoryList.addAll(subcategoryNames);
                        // Notify the adapter that the data set has changed
                        subCategoryAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    } catch (Exception e) {
                        // Log the exception for debugging
                        Log.e("CategoryListActivity", "Error updating list: " + e.getMessage());
                        Toast.makeText(clientHome3.this, "Can not retrive. Refresh" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle the failure case
                Toast.makeText(clientHome3.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setUserAvatar(){
        FirebaseStoarageManager imageManager = new FirebaseStoarageManager();

        // Load the profile image once
        imageManager.loadProfileImage(this, profileImageButton);
    }
}