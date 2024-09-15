package com.example.skillshub.signupform;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skillshub.R;

import java.util.ArrayList;
import java.util.Collections;
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
    private TableLayout skilltable;
    private TableRow skillrow;

    private static final int REQUEST_IMAGE_GALLERY = 1;
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

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_IMAGE_GALLERY && data != null) {
//            imageUri = data.getData();
//            imageViewProfilePhoto.setImageURI(imageUri);
//            frontNic.setBackgroundColor();
//        }
//
//    }

}