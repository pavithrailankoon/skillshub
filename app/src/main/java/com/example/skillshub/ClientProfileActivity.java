package com.example.skillshub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skillshub.firebaseModel.AuthManager;
import com.example.skillshub.firebaseModel.ReadData;
import com.example.skillshub.firebaseModel.UpdateData; // Change this to the correct import
import com.example.skillshub.model.CustomDialog;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class ClientProfileActivity extends AppCompatActivity implements CustomDialog.CustomDialogInterface {

    ImageView backBtn, profileImage;
    Button logOut, editDetails, editPassword;
    TextView newName, newPhoneNumber, newAddressLine1, newAddressLine2;
    private UpdateData updateData;
    private ReadData readData;
    private AuthManager authManager;

    private static final int REQUEST_IMAGE_GALLERY = 1;
    private static final int REQUEST_IMAGE_CAMERA = 2;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_profile);

        updateData = new UpdateData();
        readData = new ReadData();
        authManager = new AuthManager();

        backBtn = findViewById(R.id.backBtn);
        logOut = findViewById(R.id.logOut);
        editDetails = findViewById(R.id.editDetailsBtn);
        editPassword = findViewById(R.id.editPassword);
        profileImage = findViewById(R.id.client_profile_image);

        newName = findViewById(R.id.name);
        newPhoneNumber = findViewById(R.id.phoneNumber);
        newAddressLine1 = findViewById(R.id.addressLine1);
        newAddressLine2 = findViewById(R.id.addressLine2);

        backBtn.setOnClickListener(view -> onBackPressed());

        logOut.setOnClickListener(v -> {
            authManager.logOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(ClientProfileActivity.this, "Logout", Toast.LENGTH_SHORT).show();
        });

        editPassword.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChangePassword.class);
            startActivity(intent);
        });

        // Set up the edit button to show the dialog
        editDetails.setOnClickListener(this::openDialog);

        retrieveUserData();
    }

    @Override
    public void applyTexts(String name, String phoneNumber, String addressLine1, String addressLine2) {
        newName.setText(name);
        newPhoneNumber.setText(phoneNumber);
        newAddressLine1.setText(addressLine1);
        newAddressLine2.setText(addressLine2);

        // Update Firestore with the new details using the reusable method
        updateUserProfile(name, phoneNumber, addressLine1, addressLine2);
    }

    private void updateUserProfile(String name, String phoneNumber, String addressLine1, String addressLine2) {
        updateData.updateUserFields(name, phoneNumber, addressLine1, addressLine2, new UpdateData.FirestoreUserDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> userData) {
                Toast.makeText(ClientProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ClientProfileActivity.this, "Error updating profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openDialog(View view) {
        CustomDialog customDialog = new CustomDialog();

        // Pass existing user data to the dialog
        customDialog.setInitialValues(newName.getText().toString(),
                newPhoneNumber.getText().toString(),
                newAddressLine1.getText().toString(),
                newAddressLine2.getText().toString());
        customDialog.show(getSupportFragmentManager(), "Test CustomDialog");
    }

    private void retrieveUserData() {
        readData.getUserFields(new ReadData.FirestoreUserDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> userData) {
                if (userData != null) {
                    // Safeguard for null or missing fields
                    newName.setText(userData.getOrDefault("fullName", "No name available").toString());
                    newPhoneNumber.setText(userData.getOrDefault("phone", "No phone available").toString());
                    newAddressLine1.setText(userData.getOrDefault("address1", "No address available").toString());
                    newAddressLine2.setText(userData.getOrDefault("address2", "No address available").toString());

                    // Load profile image
                    String profileImageURL = userData.getOrDefault("profileImageURL", "").toString();
                    if (!profileImageURL.isEmpty()) {
                        Picasso.get().load(profileImageURL).placeholder(R.drawable.avatar).error(R.drawable.avatar).into(profileImage);
                    } else {
                        Picasso.get().load(R.drawable.avatar).into(profileImage);
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
}
