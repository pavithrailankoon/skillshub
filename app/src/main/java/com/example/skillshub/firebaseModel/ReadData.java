package com.example.skillshub.firebaseModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReadData {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Method to fetch all districts from Firestore
    public void getDistricts(OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
        db.collection("location").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> districts = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        districts.add(document.getId()); // Each document ID represents a district
                    }
                    onSuccess.onSuccess(districts);
                })
                .addOnFailureListener(onFailure);
    }

    // Method to fetch cities for a given district from Firestore
    public void getCities(String district, OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
        db.collection("location").document(district).get()
                .addOnSuccessListener(documentSnapshot -> {
                    List<String> cities = (List<String>) documentSnapshot.get("cities"); // Assume cities field is an array
                    if (cities != null) {
                        onSuccess.onSuccess(cities);
                    } else {
                        onFailure.onFailure(new Exception("No cities found for the district"));
                    }
                })
                .addOnFailureListener(onFailure);
    }

    // Method to fetch available main skill category from Firestore
    public void getMainSkills(OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
        db.collection("skills").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> mainSkill = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        mainSkill.add(document.getId()); // Each document ID represents a main skill
                    }
                    onSuccess.onSuccess(mainSkill);
                })
                .addOnFailureListener(onFailure);
    }

    //Method to fetch available sub skill categories from Firestore
    public void getSubSkills(String district, OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
        db.collection("skills").document(district).get()
                .addOnSuccessListener(documentSnapshot -> {
                    List<String> subSkills = (List<String>) documentSnapshot.get("subSkills"); // sub skills field is an array
                    if (subSkills != null) {
                        onSuccess.onSuccess(subSkills);
                    } else {
                        onFailure.onFailure(new Exception("No sub skills found for the main skill"));
                    }
                })
                .addOnFailureListener(onFailure);
    }

    // Method to retrieve job categories
    public void getSkillsList(final FirestoreCallback callback) {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Set<String> uniqueCategories = new HashSet<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Check if the job_categories subcollection exists
                            document.getReference().collection("workerProfiles")
                                    .get()
                                    .addOnCompleteListener(categoryTask -> {
                                        if (categoryTask.isSuccessful()) {
                                            // Check if the result is not empty
                                            if (!categoryTask.getResult().isEmpty()) {
                                                for (QueryDocumentSnapshot categoryDoc : categoryTask.getResult()) {
                                                    String category = categoryDoc.getString("category_name");
                                                    if (category != null) {
                                                        uniqueCategories.add(category); // Add to Set to ensure uniqueness
                                                    }
                                                }
                                            }
                                        }
                                        // After iterating over all users, trigger callback with the unique categories
                                        callback.onSuccess(new ArrayList<>(uniqueCategories));
                                    });
                        }
                    }
                });
    }

    public interface FirestoreCallback {
        void onSuccess(List<String> uniqueCategories);
    }
}
