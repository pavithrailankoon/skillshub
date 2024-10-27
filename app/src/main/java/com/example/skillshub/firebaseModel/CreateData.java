package com.example.skillshub.firebaseModel;

import android.content.Context;
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
        db.collection("users").document(uid)
                .set(userData, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Trigger success callback
                        onSuccess.run();
                    } else {
                        // Trigger failure callback
                        onFailure.run();
                    }
                });
    }

    public void saveWorkerCategory(String uid, String selectedCategory, Object selectedSubcategories) {
        // Create a map with the selectedSubcategories field
        Map<String, Object> categoryData = new HashMap<>();
        categoryData.put("selectedSubcategories", selectedSubcategories);

        // Navigate to the path: users > uid > workerProfiles > selectedCategory
        db.collection("users").document(uid)
                .collection("workerProfiles").document(selectedCategory)
                .set(categoryData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Category saved successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error saving category: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}
