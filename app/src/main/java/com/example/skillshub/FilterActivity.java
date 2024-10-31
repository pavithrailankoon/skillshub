package com.example.skillshub;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.skillshub.adapters.WorkerListAdapter;
import com.example.skillshub.firebaseModel.ReadData;
import com.example.skillshub.model.Worker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity {

    Spinner mainSkill;
    Spinner subSkill;
    Spinner district;
    Spinner city;
    Button filter, back;
    private WorkerListAdapter adapter;
    private List<Worker> filteredWorkerList = new ArrayList<>();
    String selectedMainCategory, selectedSubCategory, selectedDistrict, selectedCity;
    private ReadData readData;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_filter);

        readData = new ReadData();
        adapter = new WorkerListAdapter(this, filteredWorkerList);

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

        loadDistricts();

        // Main Category Spinner Listener
        mainSkill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMainCategory = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        subSkill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSubCategory = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDistrict = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCity = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        filter = findViewById(R.id.filter_button);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterActivity.this, clientHome3.class);
                intent.putExtra("mainCategory", selectedMainCategory);
                intent.putExtra("subCategory", selectedSubCategory);
                intent.putExtra("district", selectedDistrict);
                intent.putExtra("city", selectedCity);
                startActivity(intent);
            }
        });

        back = findViewById(R.id.filter_back_button);
        back.setOnClickListener(v -> onBackPressed());
    }

    private void filterWorkers() {
        CollectionReference usersRef = db.collection("users");

        // Start query based on mainCategory and proceed with other conditions
        Query query = usersRef.whereEqualTo("workerProfiles.mainCategory", selectedMainCategory)
                .whereArrayContains("workerProfiles.subcategories", selectedSubCategory)
                .whereEqualTo("district", selectedDistrict)
                .whereEqualTo("city", selectedCity);

        // Execute the query
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> workerNames = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String workerName = document.getString("workerName"); // Assuming worker name field
                    workerNames.add(workerName);
                }

                // Display worker names
                if (workerNames.isEmpty()) {
                    // No workers found
                    Toast.makeText(this, "No workers found with the selected criteria.", Toast.LENGTH_SHORT).show();
                } else {
                    // Display workerNames in a ListView, RecyclerView, etc.
                    //displayWorkers(workerNames);
                }
            } else {
                Log.d("Firestore", "Error getting documents: ", task.getException());
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

        mainSkill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMainCategory = categoryNames.get(position);
                loadSubcategories(selectedMainCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }

    private void loadSubcategories(String mainCategoryName) {
        readData.fetchUniqueSubcategories(mainCategoryName, new ReadData.FirestoreSubSkillCallback() {
            @Override
            public void onSuccess(ArrayList<String> subcategoryNames) {
                ArrayAdapter<String> subSkillAdapter = new ArrayAdapter<>(FilterActivity.this,
                        android.R.layout.simple_spinner_item, subcategoryNames);
                subSkillAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subSkill.setAdapter(subSkillAdapter);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(FilterActivity.this, "Failed to load subcategories: " + errorMessage,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDistricts() {
        readData.getDistricts(new OnSuccessListener<List<String>>() {
            @Override
            public void onSuccess(List<String> districts) {
                ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(FilterActivity.this,
                        android.R.layout.simple_spinner_item, districts);
                districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                district.setAdapter(districtAdapter);

                // Set listener for district selection
                district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedDistrict = districts.get(position);
                        loadCities(selectedDistrict);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // No action needed
                    }
                });
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FilterActivity.this, "Failed to load districts: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCities(String district) {
        readData.getCities(district, new OnSuccessListener<List<String>>() {
            @Override
            public void onSuccess(List<String> cities) {
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(FilterActivity.this,
                        android.R.layout.simple_spinner_item, cities);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                city.setAdapter(cityAdapter);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FilterActivity.this, "Failed to load cities: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
