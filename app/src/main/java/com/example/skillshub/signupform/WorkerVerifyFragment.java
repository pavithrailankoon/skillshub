package com.example.skillshub.signupform;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skillshub.R;
import com.example.skillshub.firebaseModel.ReadData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkerVerifyFragment extends Fragment {

    View view;
    private ImageButton frontNic, backNic, brImage;
    private TextView addSkill;
    private ImageView clearNicFront, clearNicBack, clearBr;
    private Uri nicFrontUri, nicBackUri, brUri;

    private List<String> mainSkillsList = new ArrayList<>();  // Fetch this list from Firestore
    private Map<String, List<String>> subSkillsMap = new HashMap<>();  // Map of mainSkill -> subSkills from Firestore
    private String selectedMainSkill = null;  // To track selected mainSkill
    private HashMap<String, List<String>> selectedSkillsMap = new HashMap<>();  // Map to store mainSkill and subSkills

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_verify, container, false);
        initializeViews(view);

        frontNic.setOnClickListener(v -> addFrontNic());
        backNic.setOnClickListener(v -> addBackNic());
        brImage.setOnClickListener(v -> addBr());

        clearNicFront.setOnClickListener(v -> nicFrontUri = null);
        clearNicBack.setOnClickListener(v -> nicBackUri = null);
        clearBr.setOnClickListener(v -> brUri = null);

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
    }

    //Method to open gallery using a button
    private void addFrontNic() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 1);
    }
    private void addBackNic() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 2);
    }
    private void addBr() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 3);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data != null) {
            nicFrontUri = data.getData();
        }

        if (requestCode == 2 && data != null) {
            nicBackUri = data.getData();
        }

        if (requestCode == 3 && data != null) {
            brUri = data.getData();
        }
    }

//    private void showSkillDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.dialog_skill_selection, null);
//
//        Spinner mainSkillSpinner = dialogView.findViewById(R.id.main_skill_spinner);
//        Spinner subSkillSpinner = dialogView.findViewById(R.id.sub_skill_spinner);
//
//        // Adapter for main skills spinner
//        ArrayAdapter<String> mainSkillAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mainSkills);
//        mainSkillAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mainSkillSpinner.setAdapter(mainSkillAdapter);
//
//        mainSkillSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                String selectedMainSkill = mainSkills.get(position);
//                List<String> availableSubSkills = subSkillsMap.get(selectedMainSkill);
//
//                // Adapter for subskills spinner
//                ArrayAdapter<String> subSkillAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, availableSubSkills);
//                subSkillAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                subSkillSpinner.setAdapter(subSkillAdapter);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {}
//        });
//
//        builder.setView(dialogView)
//                .setPositiveButton("OK", (dialog, which) -> {
//                    String selectedMainSkill = (String) mainSkillSpinner.getSelectedItem();
//                    String selectedSubSkill = (String) subSkillSpinner.getSelectedItem();
//
//                    // Handle storing selected main and sub skills
//                    if (selectedMainSkill != null && selectedSubSkill != null) {
//                        addSkillToMap(selectedMainSkill, selectedSubSkill);
//                    }
//                })
//                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
//
//        builder.create().show();
//    }


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
