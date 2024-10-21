package com.example.skillshub.firebaseModel;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.skillshub.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class FirebaseStoarageManager {
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private AuthManager authmanger;

    public FirebaseStoarageManager(){
        this.storage = FirebaseStorage.getInstance();
        this.storageRef = storage.getReference();
        this.authmanger = new AuthManager();
    }

    // Method to upload profile image and get the download URL
    public void uploadImageFiles(String uid, String fileName, Uri imageUri, OnImageUploadCompleteListener listener) {
        // Create a reference to the folder in Firebase Storage
        StorageReference userFolderRef = storageRef.child(uid);

        // Create a reference to the image file in the user's folder
        StorageReference imageRef = userFolderRef.child(fileName);

        // Upload the file to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the uploaded image
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        listener.onSuccess(uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    listener.onFailure(e.getMessage());
                });
    }

    // Listener interface for image upload completion
    public interface OnImageUploadCompleteListener {
        void onSuccess(String imageUrl);
        void onFailure(String errorMessage);
    }

    public void loadProfileImage(Context context, ImageView imageView) {
        // Get current user's UID
        String uid = authmanger.getCurrentLoginUser().getUid();
        // Path to the profile image in storage (folder named after the uid)
        StorageReference profileImageRef = storageRef.child(uid + "/profile-image");

        // Get the download URL for the profile image
        profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Use Picasso or Glide to load the image into ImageView
                Picasso.get().load(uri).into(imageView);
                Picasso.get().load(uri)
                        .placeholder(R.drawable.avatar) // Placeholder image while loading
                        .into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors (e.g., image not found)
                // You can set a default image or notify the user
                imageView.setImageResource(R.drawable.avatar);  // Set default image if not found
            }
        });
    }
}
