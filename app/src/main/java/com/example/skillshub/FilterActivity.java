package com.example.skillshub;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class FilterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_filter);

        // Find the ImageView by its ID
        ImageView imageView = findViewById(R.id.avatar);



//        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
//
//        // Create an ArrayAdapter with the list of items
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items);
//
//        // Set the adapter to the AutoCompleteTextView
//        autoCompleteTextView.setAdapter(adapter);
//
//        // Set the adapter to the AutoCompleteTextView
//        autoCompleteTextView.setAdapter(adapter);
//
//        // Optional: Set the threshold value to start searching after 1 character
//        autoCompleteTextView.setThreshold(1);
//    }
//    private static final String[] items = new String[] {
//            "Belgium", "France", "Italy", "Germany", "Spain"
//    };
    }
}