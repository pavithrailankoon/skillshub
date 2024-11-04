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

public class FirebaseStorageManager {
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private AuthManager authmanger;

    public FirebaseStorageManager(){
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

    public void uploadMultipleImages(String uid, Uri nicFrontUri, Uri nicBackUri, Uri brUri, OnImagesUploadCompleteListener listener) {
        // Create a reference to the folder in Firebase Storage
        StorageReference userFolderRef = storageRef.child(uid);

        // Keep track of the URLs of each image after upload
        final String[] imageUrls = new String[3]; // Initialize all values to null by default
        final int[] uploadCount = {0}; // Counter to track completed uploads

        // Upload NIC Front Image
        uploadSingleImage(userFolderRef, "nic-front-image", nicFrontUri, new OnImageUploadCompleteListener() {
            @Override
            public void onSuccess(String imageUrl) {
                imageUrls[0] = imageUrl;
                checkAllUploadsComplete(listener, imageUrls, uploadCount);
            }

            @Override
            public void onFailure(String errorMessage) {
                listener.onFailure("Failed to upload NIC Front Image: " + errorMessage);
            }
        });

        // Upload NIC Back Image
        uploadSingleImage(userFolderRef, "nic-back-image", nicBackUri, new OnImageUploadCompleteListener() {
            @Override
            public void onSuccess(String imageUrl) {
                imageUrls[1] = imageUrl;
                checkAllUploadsComplete(listener, imageUrls, uploadCount);
            }

            @Override
            public void onFailure(String errorMessage) {
                listener.onFailure("Failed to upload NIC Back Image: " + errorMessage);
            }
        });

        // Only upload BR Image if it is provided (not null)
        if (brUri != null) {
            uploadSingleImage(userFolderRef, "br-image", brUri, new OnImageUploadCompleteListener() {
                @Override
                public void onSuccess(String imageUrl) {
                    imageUrls[2] = imageUrl;
                    checkAllUploadsComplete(listener, imageUrls, uploadCount);
                }

                @Override
                public void onFailure(String errorMessage) {
                    listener.onFailure("Failed to upload BR Image: " + errorMessage);
                }
            });
        } else {
            // If BR image is not provided, increment the counter directly
            uploadCount[0]++;
            checkAllUploadsComplete(listener, imageUrls, uploadCount);
        }
    }

    // Helper method to upload a single image and notify via listener
    private void uploadSingleImage(StorageReference folderRef, String fileName, Uri imageUri, OnImageUploadCompleteListener listener) {
        if (imageUri == null) {
            listener.onFailure("Image URI is null for " + fileName);
            return;
        }
        StorageReference imageRef = folderRef.child(fileName);
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> listener.onSuccess(uri.toString())))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // Helper method to check if all images are uploaded
    private void checkAllUploadsComplete(OnImagesUploadCompleteListener listener, String[] imageUrls, int[] uploadCount) {
        uploadCount[0]++;
        if (uploadCount[0] == 3) { // All uploads (including potential nulls) are complete
            listener.onAllUploadsSuccess(imageUrls[0], imageUrls[1], imageUrls[2]);
        }
    }

    // Listener interface for multiple image uploads
    public interface OnImagesUploadCompleteListener {
        void onAllUploadsSuccess(String nicFrontUrl, String nicBackUrl, String brUrl);
        void onFailure(String errorMessage);
    }


}
