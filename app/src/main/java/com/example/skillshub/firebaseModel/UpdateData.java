package com.example.skillshub.firebaseModel;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateData {
    private Context context;

    public UpdateData(){
        this.context = context;
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();


    public void updateUserFields(String name, String phoneNumber, String addressLine1, String addressLine2, FirestoreUserDataCallback callback) {
        // Assuming the user ID is stored in a variable userId
        String userId = auth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(userId); // Replace "users" with your actual collection name

        // Prepare the data to update
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("fullName", name);
        updatedData.put("phone", phoneNumber);
        updatedData.put("address1", addressLine1);
        updatedData.put("address2", addressLine2);

        // Update the document in Firestore
        userRef.update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    // If the update is successful, retrieve the updated user data
                    userRef.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Convert the document data to a map
                            Map<String, Object> userData = documentSnapshot.getData();
                            callback.onSuccess(userData);
                        } else {
                            callback.onFailure(new Exception("User data not found."));
                        }
                    }).addOnFailureListener(callback::onFailure);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public interface FirestoreUserDataCallback {
        void onSuccess(Map<String, Object> userData);
        void onFailure(Exception e);
    }
}
