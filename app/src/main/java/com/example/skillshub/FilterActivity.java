package com.example.skillshub;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.skillshub.firebaseModel.ReadData;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity {

    Spinner mainSkill;
    Spinner subSkill;
    Spinner district;
    Spinner city;

    private ReadData readData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_filter);

        readData = new ReadData();

        mainSkill = findViewById(R.id.spinner_category);
        subSkill = findViewById(R.id.spinner_sub_category);
        district = findViewById(R.id.spinner_district);
        city = findViewById(R.id.spinner_city);

        readData.fetchUniqueCategoryNames(new ReadData.FirestoreCallback() {
            @Override
            public void onSuccess(ArrayList<String> categoryNames) {
                // Populate the spinner with the fetched category names
                populateSpinner(categoryNames);
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle error (e.g., show a Toast or log the error)
                Toast.makeText(getApplicationContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateSpinner(ArrayList<String> categoryNames) {
        // Create an ArrayAdapter using the category names list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        mainSkill.setAdapter(adapter);
    }
}