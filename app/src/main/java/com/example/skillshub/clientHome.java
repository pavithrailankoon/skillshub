package com.example.skillshub;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class clientHome extends AppCompatActivity {

    ImageButton filterButton;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_home);






        //Filter Button Code


        filterButton = (ImageButton) findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Toast.makeText(clientHome.this,"Filter button Work",Toast.LENGTH_SHORT).show();;

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

        ListView listView = findViewById(R.id.listView);

        String[] mainCategoryName = {"Technicians","Vehicles","IT","Event","Drivers","Profesionals"};

        int[] mainCategoryImage = {R.drawable.technicians,R.drawable.drivers,R.drawable.drivers,R.drawable.technicians,R.drawable.technicians,R.drawable.drivers};


        listAdapter adapter = new listAdapter(this,mainCategoryName,mainCategoryImage);
        listView.setAdapter(adapter);

    }




}