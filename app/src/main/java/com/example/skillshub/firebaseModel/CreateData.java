package com.example.skillshub.firebaseModel;

import android.content.Context;
import android.widget.Toast;

import com.example.skillshub.signupform.RegistrationControlActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

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

    // Method to add data to a sub-collection
    public void addWorkerInformation(String userId, String subCollection, Map<String, Object> workerData, OnWorkerDataUploadListener listener) {
        CollectionReference workersInfoCollection = db.collection("users").document(userId).collection(subCollection);

        // Auto-generate document ID and add data
        workersInfoCollection.add(workerData)
                .addOnSuccessListener(documentReference -> {
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    listener.onFailure(e.getMessage());
                });
    }

    // Optional interface for callback to handle success/failure
    public interface OnWorkerDataUploadListener {
        void onSuccess();
        void onFailure(String errorMessage);
    }

}
