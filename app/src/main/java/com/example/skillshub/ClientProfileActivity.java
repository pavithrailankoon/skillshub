package com.example.skillshub;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skillshub.firebaseModel.AuthManager;
import com.example.skillshub.firebaseModel.CreateData;
import com.example.skillshub.firebaseModel.FirebaseStorageManager;
import com.example.skillshub.firebaseModel.ReadData;
import com.example.skillshub.model.CustomDialog;
import com.example.skillshub.signupform.RegistrationControlActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientProfileActivity extends AppCompatActivity {

    ImageView backBtn, profileImage,contact_developers;
    Button logOut, editDetails, editPassword, buttonUploadPhoto,deletebtn;
    TextView newName, newPhoneNumber, newAddressLine1, newAddressLine2, city, district;
    private ReadData readData;
    private AuthManager authManager;
    private FirebaseStorageManager storageManager;
    private FirebaseUser user;
    private ArrayAdapter<String> districtAdapter;
    private ArrayAdapter<String> cityAdapter;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    private static final int REQUEST_IMAGE_GALLERY = 1;
    private Uri DEFAULT_IMAGE_URI;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);

        readData = new ReadData();
        authManager = new AuthManager();
        storageManager = new FirebaseStorageManager();
        db = FirebaseFirestore.getInstance();

        // Find views by ID
        backBtn = findViewById(R.id.backBtn);
        editPassword = findViewById(R.id.editPassword);
        profileImage = findViewById(R.id.client_profile_image);
        logOut = findViewById(R.id.logOut);
        editDetails = findViewById(R.id.editDetailsBtn);
        buttonUploadPhoto = findViewById(R.id.button);
        contact_developers = findViewById(R.id.contact_developers);
        deletebtn = findViewById(R.id.deleteAccount);

        newName = findViewById(R.id.name);
        newPhoneNumber = findViewById(R.id.phoneNumber);
        newAddressLine1 = findViewById(R.id.addressLine1);
        newAddressLine2 = findViewById(R.id.addressLine2);
        city = findViewById(R.id.textView15);
        district = findViewById(R.id.textView22);

        backBtn.setOnClickListener(view -> onBackPressed());
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if (user != null) {
            String uid = user.getUid();
            storageReference = FirebaseStorage.getInstance().getReference().child(uid + "/profile-image");
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        contact_developers.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:skillhubdevelopers@gmail.com"));
            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(emailIntent);
            } else {
                Toast.makeText(this, "No email app available", Toast.LENGTH_SHORT).show();
            }
        });

        deletebtn.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:skillhubdevelopers@gmail.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Delete Request");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "I would like to delete my account.");
            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(emailIntent);
            } else {
                Toast.makeText(this, "No email app available", Toast.LENGTH_SHORT).show();
            }
        });


        logOut.setOnClickListener(v -> {
            authManager.logOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        });

        editDetails.setOnClickListener(v -> showUpdateUserDialog());
        buttonUploadPhoto.setOnClickListener(v -> openGallery());
        editPassword.setOnClickListener(v -> {
            Intent intent = new Intent(ClientProfileActivity.this, ChangePassword.class);
            startActivity(intent);
            finish();
        });
        retrieveUserData();
    }


    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                storageManager.uploadImageFiles(user.getUid(), "profile-image", imageUri, new FirebaseStorageManager.OnImageUploadCompleteListener() {
                    @Override
                    public void onSuccess(String profileUrl) {
                        updateImageUrl(profileUrl);
                        loadImageFromFirebase(profileImage);
                        profileImage.setImageURI(imageUri);
                    }
                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(ClientProfileActivity.this, "Failed to upload profile image: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        if (imageUri == null) {
            imageUri = DEFAULT_IMAGE_URI;
            profileImage.setImageURI(imageUri);
        }
    }

    private void loadImageFromFirebase(ImageView imageView) {
        // Get download URL from Firebase Storage
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // Use Picasso to load the image into the ImageView
            Picasso.get()
                    .load(uri)
                    .resize(200, 200)
                    .centerCrop()
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(imageView);

        }).addOnFailureListener(exception -> {
            // Handle any errors
            exception.printStackTrace();
        });
    }

    private void updateImageUrl(String imageUrl){
        if (firebaseAuth.getCurrentUser() != null) {
            String uid = firebaseAuth.getCurrentUser().getUid();
            DocumentReference documentReference = db.collection("user").document(uid);

            // Create a map to update just the profileImageURL field
            Map<String, Object> updates = new HashMap<>();
            updates.put("profileImageURL", imageUrl);

            // Perform the update
            documentReference.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Profile image URL updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update profile image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ClientProfileActivity", "Error updating profile image URL", e);
                    });
        } else {
            Toast.makeText(this, "No user is signed in", Toast.LENGTH_SHORT).show();
        }
    }

    private void retrieveUserData() {
        readData.getUserFields(new ReadData.FirestoreUserDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> userData) {
                if (userData != null) {
                    newName.setText(userData.getOrDefault("fullName", "No name available").toString());
                    newPhoneNumber.setText(userData.getOrDefault("phone", "No phone available").toString());
                    newAddressLine1.setText(userData.getOrDefault("address1", "No address available").toString());
                    newAddressLine2.setText(userData.getOrDefault("address2", "No address available").toString());
                    city.setText(userData.getOrDefault("city", "No city available").toString());
                    district.setText(userData.getOrDefault("district", "No district available").toString());
                    String profileImageURL = userData.getOrDefault("profileImageURL", "No profile image available").toString();

                    if (!profileImageURL.isEmpty()) {
                        Picasso.get()
                                .load(profileImageURL)
                                .placeholder(R.drawable.avatar)
                                .error(R.drawable.avatar)
                                .into(profileImage);
                    } else {
                        profileImage.setImageResource(R.drawable.avatar);
                    }
                } else {
                    Toast.makeText(ClientProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ClientProfileActivity.this, "Error retrieving user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showUpdateUserDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.update_user_info, null);

        EditText editTextName = dialogView.findViewById(R.id.editTextName);
        EditText editTextPhoneNumber = dialogView.findViewById(R.id.editTextPhoneNumber);
        EditText editTextAddressLine1 = dialogView.findViewById(R.id.editTextAddressLine1);
        EditText editTextAddressLine2 = dialogView.findViewById(R.id.editTextAddressLine2);
        AutoCompleteTextView editTextDistrict = dialogView.findViewById(R.id.editTextDistrict);
        AutoCompleteTextView editTextCity = dialogView.findViewById(R.id.editTextCity);

        districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        editTextDistrict.setAdapter(districtAdapter);

        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        editTextCity.setAdapter(cityAdapter);

        editTextCity.setEnabled(false);

        loadDistricts();

        setupDistrictSelectionListener(editTextDistrict, editTextCity);
        setupCityClickListener(editTextCity);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            String uid = auth.getCurrentUser().getUid();
            DocumentReference documentReference = db.collection("users").document(uid);

            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    editTextName.setText(documentSnapshot.getString("fullName"));
                    editTextPhoneNumber.setText(documentSnapshot.getString("phone"));
                    editTextAddressLine1.setText(documentSnapshot.getString("addressLine1"));
                    editTextAddressLine2.setText(documentSnapshot.getString("addressLine2"));
                    editTextCity.setText(documentSnapshot.getString("city"));
                    editTextDistrict.setText(documentSnapshot.getString("district"));
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show();
            });

            new AlertDialog.Builder(this)
                    .setTitle("Update User Information")
                    .setView(dialogView)
                    .setPositiveButton("Update", (dialog, which) -> {
                        String name = editTextName.getText().toString().trim();
                        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
                        String addressLine1 = editTextAddressLine1.getText().toString().trim();
                        String addressLine2 = editTextAddressLine2.getText().toString().trim();
                        String city = editTextCity.getText().toString().trim();
                        String district = editTextDistrict.getText().toString().trim();

                        Map<String, Object> updatedUserData = new HashMap<>();
                        updatedUserData.put("fullName", name);
                        updatedUserData.put("phoneNumber", phoneNumber);
                        updatedUserData.put("addressLine1", addressLine1);
                        updatedUserData.put("addressLine2", addressLine2);
                        updatedUserData.put("city", city);
                        updatedUserData.put("district", district);

                        documentReference.update(updatedUserData)
                                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Data updated successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update data", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        } else {
            Toast.makeText(this, "No user is signed in", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDistricts() {
        readData.getDistricts(districts -> {
            districtAdapter.clear();
            districtAdapter.addAll(districts);
            districtAdapter.notifyDataSetChanged();
        }, e -> Log.e("Activity", "Failed to load districts", e));
    }

    private void setupDistrictSelectionListener(AutoCompleteTextView district, AutoCompleteTextView city) {
        district.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDistrict = (String) parent.getItemAtPosition(position);
            loadCities(selectedDistrict);
            city.setEnabled(true);
        });
    }

    private void setupCityClickListener(AutoCompleteTextView city) {
        city.setOnClickListener(v -> {
            if (!city.isEnabled()) {
                Toast.makeText(this, "Please, select district first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCities(String district) {
        readData.getCities(district, cities -> {
            cityAdapter.clear();
            cityAdapter.addAll(cities);
            cityAdapter.notifyDataSetChanged();
        }, e -> Log.e("MainActivity", "Failed to load cities", e));
    }
}
