package com.example.skillshub.signupform;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.skillshub.R;
import com.example.skillshub.firebaseModel.ReadData;

import java.util.ArrayList;

public class WorkerVerifyFragment extends Fragment {

    View view;
    private ImageButton frontNic;
    private ImageButton backNic;
    private ImageButton brImage;
    private TextView mainSkillCategory;
    private TextView subSkillCategory;
    private TextView addSkill;
    private ImageView clearNicFront;
    private ImageView clearNicBack;
    private ImageView clearBr;
    private TableLayout skilltable;
    private TableRow skillrow;

    private ArrayAdapter<String> mainSkillAdapter;
    private ArrayAdapter<String> subSkillAdapter;

    private static final int REQUEST_IMAGE_GALLERY_FRONT = 1;
    private static final int REQUEST_IMAGE_GALLERY_BACK = 2;
    private static final int REQUEST_IMAGE_GALLERY_BR = 3;
    private Uri nicFrontUri, nicBackUri, brUri;
//    private Map<String, List<String>> skillMap;
//    private List<String> subSkillsList;
//    private boolean[] selectedSubSkills;
//    private List<String> currentSubSkills = new ArrayList<>();
//    private ArrayList<Integer> subSkillIndices = new ArrayList<>();
//    private FirestoreDataRetriver firestoreDataRetriver;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_verify, container, false);
        initializeViews(view);

        frontNic.setOnClickListener(v -> addFrontNic());
        backNic.setOnClickListener(v -> addBackNic());
        brImage.setOnClickListener(v -> addBr());

//        frontNic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openCamera();
//            }
//        });
//
//        backNic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openCamera();
//            }
//        });
//
//        brImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openCamera();
//            }
//        });

        clearNicFront.setOnClickListener(v -> {
            nicFrontUri = null;
            frontNic.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        });
        clearNicBack.setOnClickListener(v -> {
            nicBackUri = null;
            backNic.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        });
        clearBr.setOnClickListener(v -> {
            brUri = null;
            brImage.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        });

        addSkill.setOnClickListener(v -> addNewRow());

        return view;
    }

    private void initializeViews(View view) {
        frontNic = view.findViewById(R.id.signup_verify_nicfront_upload);
        backNic = view.findViewById(R.id.signup_verify_nicback_upload);
        brImage = view.findViewById(R.id.signup_verify_br_upload);
        mainSkillCategory = view.findViewById(R.id.signup_main_category);
        subSkillCategory = view.findViewById(R.id.signup_sub_category);
        addSkill = view.findViewById(R.id.signup_add_newcategory);
        clearNicFront = view.findViewById(R.id.signup_clear_nicfront_upload);
        clearNicBack = view.findViewById(R.id.signup_clear_nicback_upload);
        clearBr = view.findViewById(R.id.signup_clear_br_upload);
        skilltable = view.findViewById(R.id.skilltable);
        skillrow = view.findViewById(R.id.signup_category_row);
    }

    private void addNewRow() {
        TableRow newRow = (TableRow) LayoutInflater.from(getContext()).inflate(R.layout.skill_table_row, skilltable, false);

        ImageView clearSkillButton = newRow.findViewById(R.id.signup_clear_category);

        // Set an OnClickListener for the clearSkill button to remove this specific row
        clearSkillButton.setOnClickListener(v -> skilltable.removeView(newRow));

        // Add the newly created row to the TableLayout
        skilltable.addView(newRow);
    }

    //Method to open gallery using a button
    private void addFrontNic() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY_FRONT);
    }
    private void addBackNic() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY_BACK);
    }
    private void addBr() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY_BR);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_GALLERY_FRONT && data != null) {
            nicFrontUri = data.getData();
            frontNic.setBackgroundColor(getResources().getColor(R.color.yellow));
        }

        if (requestCode == REQUEST_IMAGE_GALLERY_BACK && data != null) {
            nicBackUri = data.getData();
            backNic.setBackgroundColor(getResources().getColor(R.color.yellow));
        }

        if (requestCode == REQUEST_IMAGE_GALLERY_BR && data != null) {
            brUri = data.getData();
            brImage.setBackgroundColor(getResources().getColor(R.color.yellow));
        }
    }

//    private void validateDistrictCity(){
//        // Initialize adapters
//        mainSkillAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
//        mainSkillCategory.setAdapter(mainSkillAdapter);
//
//        subSkillAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
//        subSkillCategory.setAdapter(subSkillAdapter);
//
//        subSkillCategory.setEnabled(false);
//
//        loadSubSkills();
//
//        setupDistrictSelectionListener();
//        setupCityClickListener();
//    }

//    private void loadSubSkills() {
//        readData.getDistricts(districts -> {
//            mainSkillAdapter.clear();
//            mainSkillAdapter.addAll(districts);
//            mainSkillAdapter.notifyDataSetChanged();
//        }, e -> Log.e("MainActivity", "Failed to load districts", e));
//    }


    //Add getters to send data to RegistrationControl Activity
    public Uri getNicFront() {
        return nicFrontUri;
    }

    public Uri getNicBack() {
        return nicBackUri;
    }

    public Uri getBr() {
        return brUri;
    }


}