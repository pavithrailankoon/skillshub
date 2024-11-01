package com.example.skillshub.firebaseModel;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class CreateData {
    private FirebaseFirestore db;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private String uid;
    private Context context;

    public CreateData(){
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.user = auth.getCurrentUser();
        this.uid = user.getUid();
        this.context = context;
    }

    // Method to save user data to Firestore
    public void saveUserDataToFirestore(String uid, Map<String, Object> userData, Runnable onSuccess, Runnable onFailure) {
        if (uid == null || userData == null) {
            // Log the error and trigger failure callback if data is missing
            Log.e("FirestoreError", "UID or userData is null.");
            onFailure.run();
            return;
        }

        db.collection("users").document(uid)
                .set(userData, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Trigger success callback
                        if (onSuccess != null) onSuccess.run();
                    } else {
                        // Trigger failure callback
                        if (onFailure != null) onFailure.run();
                    }
                });
    }

    public void saveWorkerCategory(Context context, String uid, String selectedCategory, Object selectedSubcategories) {
//        if (uid == null || selectedCategory == null || selectedSubcategories == null || context == null) {
//            // Log the error and show a Toast if necessary data is missing
//            Log.e("FirestoreError", "One or more parameters are null.");
//            Toast.makeText(context, "Failed to save category: Missing data.", Toast.LENGTH_SHORT).show();
//            return;
//        }

        // Create a map with the selectedSubcategories field
        Map<String, Object> categoryData = new HashMap<>();
        categoryData.put("subcategories", selectedSubcategories);

        // Navigate to the path: users > uid > workerProfiles > selectedCategory
        db.collection("users").document(uid)
                .collection("workerProfiles").document(selectedCategory)
                .set(categoryData)
                .addOnSuccessListener(aVoid -> {
                    if (context != null) {
                        Toast.makeText(context, "Category saved successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (context != null) {
                        Toast.makeText(context, "Error saving category: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}