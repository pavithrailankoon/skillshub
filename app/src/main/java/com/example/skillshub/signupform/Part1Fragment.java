package com.example.skillshub.signupform;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_part1, container, false);
        firstName = view.findViewById(R.id.signup_first_name);
        lastName = view.findViewById(R.id.signup_last_name);
        phoneNumber = view.findViewById(R.id.signup_mobileno);
        password = view.findViewById(R.id.signup_password_add);
        passwordConfirm = view.findViewById(R.id.signup_password_confirm);
        imageViewProfilePhoto = view.findViewById(R.id.signup_upload_avatar);
        buttonTakePhoto = view.findViewById(R.id.signup_take_profile_photo);
        buttonUploadPhoto = view.findViewById(R.id.signup_upload_profile_photo);

        buttonTakePhoto.setOnClickListener(v -> openCamera());
        buttonUploadPhoto.setOnClickListener(v -> openGallery());

        //checkPermissions();

        return view;

    }

    //Method to open camera using a button
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File imageFile = createImageFile();
            if (imageFile != null) {
                imageUri = FileProvider.getUriForFile(getActivity(), "", imageFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAMERA);
            }
        }
    }

    //Method to open gallery using a button
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
    }

    //
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY && data != null) {
                imageUri = data.getData();
                imageViewProfilePhoto.setImageURI(imageUri);
            } else if (requestCode == REQUEST_IMAGE_CAMERA) {
                imageViewProfilePhoto.setImageURI(imageUri);
            }
        }
    }

    //Create custom image file name
    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "USER_" + timeStamp;
        File storagePath = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;

        try {
            image = File.createTempFile(imageFileName, ".jpg", storagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    // Getters to get user data
    public Uri getImageUri() {
        return imageUri;
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