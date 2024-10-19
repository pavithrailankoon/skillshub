package com.example.skillshub.signupform;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.example.skillshub.ChooseUserActivity;
import com.example.skillshub.R;
import com.example.skillshub.firebaseModel.ReadData;
import com.example.skillshub.utils.DialogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ImageView clearSkillButton;
    private TableLayout skilltable;
    private TableRow skillrow;

    private ArrayAdapter<String> mainSkillAdapter;
    private ArrayAdapter<String> subSkillAdapter;
    private ReadData readData;
    private DialogUtils dialogUtils;

    private static final int REQUEST_IMAGE_GALLERY_FRONT = 1;
    private static final int REQUEST_IMAGE_GALLERY_BACK = 2;
    private static final int REQUEST_IMAGE_GALLERY_BR = 3;
    private Uri nicFrontUri, nicBackUri, brUri;

    private HashMap<String, List<String>> selectedSkills = new HashMap<>();
    private String selectedMainSkill;
    private List<String> selectedSubSkill;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_verify, container, false);
        initializeViews(view);
        readData = new ReadData();
        //skillrow.setVisibility(View.VISIBLE);

        frontNic.setOnClickListener(v -> addFrontNic());
        backNic.setOnClickListener(v -> addBackNic());
        brImage.setOnClickListener(v -> addBr());

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
        addSkill = view.findViewById(R.id.signup_add_newcategory);
        clearNicFront = view.findViewById(R.id.signup_clear_nicfront_upload);
        clearNicBack = view.findViewById(R.id.signup_clear_nicback_upload);
        clearBr = view.findViewById(R.id.signup_clear_br_upload);
        skilltable = view.findViewById(R.id.skilltable);
        skillrow = view.findViewById(R.id.signup_category_row);
    }

    private void addNewRow() {
        TableRow newRow = (TableRow) LayoutInflater.from(getContext()).inflate(R.layout.skill_table_row, skilltable, false);

        clearSkillButton = newRow.findViewById(R.id.signup_clear_category);
        mainSkillCategory = view.findViewById(R.id.signup_main_category);
        subSkillCategory = view.findViewById(R.id.signup_sub_category);

//        // Set an OnClickListener for the entire TableRow to show the AlertDialog
//        newRow.setOnClickListener(v -> {
//            // Show your AlertDialog for selecting main and sub skills
//            showSkillSelectionDialog(mainSkillCategory, subSkillCategory);
//        });
        // Set OnClickListener for mainSkillCategory TextView
        mainSkillCategory.setOnClickListener(v -> {
            // Show your AlertDialog for selecting main skill
            showMainSkillDialog(mainSkillCategory);
        });

// Set OnClickListener for subSkillCategory TextView
        subSkillCategory.setOnClickListener(v -> {
            // Show your AlertDialog for selecting sub skill

        });

        clearSkillButton.setOnClickListener(v -> {
            // Remove the row from the TableLayout
            skilltable.removeView(newRow);
            // Clear data related to the mainSkillCategory and subSkillCategory in your HashMap here
            clearDataFromHashMap(mainSkillCategory.getText().toString());
        });

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

    // Method to show Main Skill selection dialog
    private void showMainSkillDialog(TextView mainSkillCategory) {
        readData.getMainSkill(mainSkills -> {
            String[] mainSkillArray = mainSkills.toArray(new String[0]);
            AlertDialog.Builder skillDialog = new AlertDialog.Builder(getContext());
            skillDialog.setTitle("Select Main Skill")
                    .setMessage("Choose a main skill")
                    .setSingleChoiceItems(mainSkillArray, -1, (dialogInterface, choiceIndex) -> {
                        String selectedSkill = mainSkillArray[choiceIndex];
                        mainSkillCategory.setText(selectedSkill);  // Update main skill category TextView
                    })
                    .setPositiveButton("Add", (dialog, which) -> {
                        // When the "Add" button is clicked, add the selected skill to the HashMap
                        String selectedSkill = mainSkillCategory.getText().toString();
                        selectedSkills.put(selectedSkill, selectedSubSkill); // Add to the HashMap
                        // You can also display a Toast message or update UI here if needed
                        Toast.makeText(getContext(), selectedSkill + " added to skills!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            // Show the skill selection dialog
            skillDialog.create().show();
        }, e -> {
            Toast.makeText(getContext(), "Error fetching main skills.", Toast.LENGTH_SHORT).show();
        });
    }

    private void showSubSkillDialog(String selectedSkill) {
        String selectedMainSkill = mainSkillCategory.getText().toString();
        if (selectedMainSkill.isEmpty()) {
            Toast.makeText(getContext(), "Please select a main skill first.", Toast.LENGTH_SHORT).show();
            //return;
        }

        // Fetch sub-skills based on the selected main skill
        readData.getSubSkill(selectedMainSkill, subSkills -> {
            String[] subSkillArray = subSkills.toArray(new String[0]);
            DialogUtils.showAlertDialog(
                    getContext(),  // Context
                    "Select Sub Skill",  // Title
                    "Choose a sub skill",  // Message
                    "Add",  // Positive button text
                    (dialog, which) -> {
                        // Show list of sub-skills in an AlertDialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setSingleChoiceItems(subSkillArray, -1, (dialogInterface, choiceIndex) -> {
                            String selectedSubSkill = subSkillArray[choiceIndex];
                            subSkillCategory.setText(selectedSubSkill);  // Update sub skill category TextView
                            dialogInterface.dismiss();
                        });
                        builder.create().show();
                    },
                    null, null,  // No neutral button
                    "Cancel", (dialog, which) -> dialog.cancel()  // Cancel button
            );
        }, e -> {
            Toast.makeText(getContext(), "Error fetching sub skills.", Toast.LENGTH_SHORT).show();
        });
    }



    private void clearDataFromHashMap(String mainSkill) {
        // Remove the entry for the selected main skill from the HashMap
        selectedSkills.remove(mainSkill);
    }

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