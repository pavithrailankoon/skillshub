package com.example.skillshub;

import android.os.Bundle;

import android.widget.ListView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class clientHome extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainCategory_listView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ListView listView = findViewById(R.id.mainCategory_listView);

        String[] mainCategory = {"Technicians","Drivers","IT","Profesionals"};
        int[] mainCategoryIcon = {R.drawable.technicians,R.drawable.drivers,R.drawable.baseline_category_24,R.drawable.star};

        listAdapter adapter = new listAdapter(this,mainCategory,mainCategoryIcon);
        listView.setAdapter(adapter);

    }




}