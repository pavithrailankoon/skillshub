package com.example.skillshub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.skillshub.firebaseModel.FirebaseStoarageManager;
import com.example.skillshub.firebaseModel.ReadData;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class clientHome extends AppCompatActivity {

    private ImageButton filterButton;
    private Button button;
    private CircleImageView profileImageButton;
    private ListView mainSkillsListView;

    private ReadData readData;
    private ArrayAdapter adapter;
    private List<String> skillList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_home);

        readData = new ReadData();

        mainSkillsListView = findViewById(R.id.listView1);
        skillList = new ArrayList<>();

        loadSkillList();

        // Initialize the adapter
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, skillList);
        mainSkillsListView.setAdapter(adapter);

        //filter Button Code
        filterButton = (ImageButton) findViewById(R.id.filter_button);

        filterButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(clientHome.this, "Filter", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(clientHome.this, FilterActivity.class);
                startActivity(intent);
            }

        });


        // client profile button code

        profileImageButton = (CircleImageView) findViewById(R.id.avatar);

        profileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(clientHome.this, "Client profile", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(clientHome.this, ClientProfileActivity.class);
                startActivity(intent);
            }
        });


        //Become A worker Button Code

        button = (Button) findViewById(R.id.becomeWorkerButton);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Toast.makeText(clientHome.this,"Become A Worker Button work",Toast.LENGTH_SHORT).show();;

            }
        });


        // List View code for choose main category

        ListView listView = findViewById(R.id.listView1);

        String[] mainCategoryName = {"Technicians","Vehicles","IT","Event","Drivers","Profesionals"};

        int[] mainCategoryImage = {R.drawable.technicians,R.drawable.drivers,R.drawable.drivers,R.drawable.technicians,R.drawable.technicians,R.drawable.drivers};


        listAdapter adapter = new listAdapter(this,mainCategoryName,mainCategoryImage);
        listView.setAdapter(adapter);

    }

    // Retrieve job categories from Firestore
    private void loadSkillList() {
        readData.getSkillsList(mainSkillsListView -> {
            skillList.clear();
            skillList.addAll(mainSkillsListView);
            adapter.notifyDataSetChanged(); // Update the ListView from firestore workerInformation sub-collection
        });
    }

    private void setUserAvatar(){
        FirebaseStoarageManager imageManager = new FirebaseStoarageManager();

// Load the profile image once
        imageManager.loadProfileImage(this, profileImageButton);
    }
}