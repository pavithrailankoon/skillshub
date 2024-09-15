package com.example.skillshub.signupform;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.skillshub.signupform.RegistrationControlActivity;
import com.example.skillshub.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Part1Fragment extends Fragment {

    View view;
    private EditText firstName;
    private EditText lastName;
    private EditText phoneNumber;
    private EditText password;
    private EditText passwordConfirm;
    private CircleImageView imageViewProfilePhoto;
    private Button buttonTakePhoto, buttonUploadPhoto;

    private static final int REQUEST_IMAGE_GALLERY = 1;
    private static final int REQUEST_IMAGE_CAMERA = 2;
    private Uri imageUri;
    private Uri DEFAULT_IMAGE_URI;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_part1, container, false);
        initializeViews();

        //buttonTakePhoto.setOnClickListener(v -> openCamera());
        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    checkPermissions();
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAMERA);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Couldn't load photo", Toast.LENGTH_LONG).show();
                }
            }
        });
        buttonUploadPhoto.setOnClickListener(v -> openGallery());
        //checkPermissions();

        return view;

    }

    private void initializeViews() {
        firstName = view.findViewById(R.id.signup_first_name);
        lastName = view.findViewById(R.id.signup_last_name);
        phoneNumber = view.findViewById(R.id.signup_mobileno);
        password = view.findViewById(R.id.signup_password_add);
        passwordConfirm = view.findViewById(R.id.signup_password_confirm);
        imageViewProfilePhoto = view.findViewById(R.id.signup_upload_avatar);
        buttonTakePhoto = view.findViewById(R.id.signup_take_profile_photo);
        buttonUploadPhoto = view.findViewById(R.id.signup_upload_profile_photo);

        // Set default image URI
        DEFAULT_IMAGE_URI = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.drawable.avatar);
    }


    //Method to open gallery using a button
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
    }

    //
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_GALLERY && data != null) {
                imageUri = data.getData();
                imageViewProfilePhoto.setImageURI(imageUri);
        }

        if (requestCode == REQUEST_IMAGE_CAMERA && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap photo = (Bitmap) data.getExtras().get("data");

                imageViewProfilePhoto.setImageBitmap(photo);

            } catch (Exception e) {
                Toast.makeText(getContext(), "Couldn't load photo", Toast.LENGTH_LONG).show();
            }
        }

        // If no image is selected, set the default image URI
        if (imageUri == null) {
            imageUri = DEFAULT_IMAGE_URI;
            imageViewProfilePhoto.setImageURI(imageUri);
        }
    }

    // Getters to get user data
    public Uri getImageUri() {
        return imageUri != null ? imageUri : DEFAULT_IMAGE_URI;
    }

    public String getFirstName() {
        return firstName.getText().toString();
    }

    public String getLastName() {
        return lastName.getText().toString();
    }

    public String getPhoneNumber() {
        return phoneNumber.getText().toString();
    }

    public String getPassword() {
        return password.getText().toString();
    }

    public String getPasswordConfirm() {
        return passwordConfirm.getText().toString();
    }


    // Validation method
    public boolean validateInput() {
        boolean isValid = true;

        // Validate first name
        if (getFirstName().isEmpty()) {
            firstName.setError("First name is required");
            isValid = false;
        }

        // Validate last name
        if (getLastName().isEmpty()) {
            lastName.setError("Last name is required");
            isValid = false;
        }

        // Validate phone
        if (getPhoneNumber().isEmpty() || getPhoneNumber().length()<10) {
            phoneNumber.setError("Valid phone number is required");
            isValid = false;
        }

        // Validate password
        String strPwd = password.getText().toString().trim();
        if (strPwd.isEmpty() || strPwd.length() < 6) {
            if (strPwd.isEmpty()) {
                password.setError("Password is required");
            } else if (strPwd.length() < 6) {
                password.setError("Password must be at least 6 characters");
            }
            isValid = false;
        }

        // Validate password confirm
        String strPwdConfirm = passwordConfirm.getText().toString().trim();
        if (strPwdConfirm.isEmpty() || !strPwd.equals(strPwdConfirm)) {
            passwordConfirm.setError("Password is not matched");
            isValid = false;
        }


        return isValid;
    }

    //Handle permissions
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 100);
        }
    }

}