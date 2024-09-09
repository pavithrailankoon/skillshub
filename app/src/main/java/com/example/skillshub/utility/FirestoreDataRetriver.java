package com.example.skillshub.utility;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreDataRetriver {
    private FirebaseFirestore db;
    private List<String> districts;
    private List<String> cities;

    public FirestoreDataRetriver() {
        db = FirebaseFirestore.getInstance();
        districts = new ArrayList<>();
        cities = new ArrayList<>();
    }

    // Interface for callback to handle data retrieval
    public interface FirestoreCallback {
        void onSuccess(Map<String, List<String>> skillMap, List<String> mainSkills);
        void onFailure(Exception e);
    }

    public interface onDataRetrieveListner<T>  {
        void onDataFetched(T data); // Called when data is successfully retrived
        void onError(Exception e); // Called when there is an error
    }


    // Retrieve districts
    public void retriveDistricts(onDataRetrieveListner<List<String>> listener) {
        db.collection("location").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                districts.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    districts.add(document.getId());
                }
                listener.onDataFetched(districts);
            } else {
                listener.onError(null);
            }
        });
    }

    // Retrieve cities for a specific district
    public void retriveCities(String district, onDataRetrieveListner<List<String>> listener) {
        db.collection("location").document(district).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    cities.clear();
                    List<String> cityList = (List<String>) document.get("cities");
                    if (cityList != null) {
                        cities.addAll(cityList);
                    }
                    listener.onDataFetched(cities);
                } else {
                    listener.onError(null);
                }
            } else {
                // Handle failure
                listener.onDataFetched(null);
            }
        });
    }

    // Method to load skills from Firestore
    public void loadSkillsFromFirestore(Context context, FirestoreCallback callback) {
        db.collection("skills")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Map<String, List<String>> skillMap = new HashMap<>();
                        List<String> mainSkills = new ArrayList<>();

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String mainCategory = document.getString("mainCategory");
                            List<String> subCategories = (List<String>) document.get("subCategories");

                            if (mainCategory != null && subCategories != null) {
                                skillMap.put(mainCategory, subCategories);
                                mainSkills.add(mainCategory);
                            }
                        }
                        callback.onSuccess(skillMap, mainSkills);
                    } else {
                        Toast.makeText(context, "No skills found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error retrieving skills", Toast.LENGTH_SHORT).show();
                    callback.onFailure(e);
                });
    }
}


