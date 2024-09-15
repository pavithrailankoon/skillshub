package com.example.skillshub.firebaseModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadData {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();;

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

    // Method to fetch all categories available from Firestore
//    public void getCategories(OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
//        db = FirebaseConnection.getFirestoreInstance();
//        db.collection("skills").get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    Map<String, List<String>> skillMap = new HashMap<>();
//                    List<String> subSkills = new ArrayList<>();
//                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
//                        String mainCategory = document.getString("mainCategory");
//                        List<String> subCategories = (List<String>) document.get("subCategories");
//
//                        if (mainCategory != null && subCategories != null) {
//                            skillMap.put(mainCategory, subCategories);
//                            subSkills.add(mainCategory);
//                        }
//                    }
//                    if (skillMap != null) {
//                        onSuccess.onSuccess(skillMap);
//                    } else {
//                        onFailure.onFailure(new Exception("No categories found for the skills"));
//                    }
//                    callback.onSuccess(skillMap, mainSkills);
//                })
//                .addOnFailureListener(onFailure);
//    }
}
