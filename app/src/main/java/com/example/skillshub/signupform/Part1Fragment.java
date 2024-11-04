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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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
    private EditText fullName;
    private EditText phoneNumber;
    private CircleImageView imageViewProfilePhoto;
    private Button buttonUploadPhoto;

    private static final int REQUEST_IMAGE_GALLERY = 1;
    private Uri imageUri;
    private Uri DEFAULT_IMAGE_URI;
    @Nullable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_part1, container, false);
        initializeViews();

        buttonUploadPhoto.setOnClickListener(v -> openGallery());

        return view;

    }

    private void initializeViews() {
        fullName = view.findViewById(R.id.signup_full_name);
        phoneNumber = view.findViewById(R.id.signup_mobileno);
        imageViewProfilePhoto = view.findViewById(R.id.signup_upload_avatar);
        buttonUploadPhoto = view.findViewById(R.id.signup_upload_profile_photo);

        // Add TextWatchers to listen for text changes
        fullName.addTextChangedListener(new GenericTextWatcher(fullName));
        phoneNumber.addTextChangedListener(new GenericTextWatcher(phoneNumber));

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

    public String getFullName() {
        return fullName.getText().toString();
    }

    public String getPhoneNumber() {
        return phoneNumber.getText().toString().trim();
    }


    // Validation method
    public boolean validateInput() {
        boolean isValid = true;

        // Validate first name
        if (getFullName() == null || getFullName().isEmpty()) {
            fullName.setError("First name is required");
            isValid = false;
        }

        // Validate phone
        String strPhone = phoneNumber.getText().toString().trim();
        if (getPhoneNumber().isEmpty() || strPhone.length() != 10) {
            phoneNumber.setError("Valid phone number is required");
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

    private class GenericTextWatcher implements TextWatcher {

        private EditText editText;

        public GenericTextWatcher(EditText editText) {
            this.editText = editText;
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            validateInput();
        }
    }

}