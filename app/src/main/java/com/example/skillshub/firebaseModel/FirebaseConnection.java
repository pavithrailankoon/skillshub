package com.example.skillshub.firebaseModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseConnection {
    private static FirebaseFirestore db;
    private static FirebaseAuth auth;
    private static FirebaseStorage storage;

    private FirebaseConnection() {}

    // Method to get Firestore instance
    public static FirebaseFirestore getFirestoreInstance() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        return db;
    }

    // Method to get Firebase Authentication instance
    public static FirebaseAuth getAuthInstance() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    // Method to get Firebase Storage instance
    public static FirebaseStorage getStorageInstance() {
        if (storage == null) {
            storage = FirebaseStorage.getInstance();
        }
        return storage;
    }
}
