package com.example.skillshub.firebaseModel;

import com.google.firebase.firestore.FirebaseFirestore;

public class CreateData {
    private FirebaseFirestore db;

    public CreateData() {
        this.db = FirebaseConnection.getFirestoreInstance();
    }
}
