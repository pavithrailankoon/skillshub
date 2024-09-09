package com.example.skillshub.signupform;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skillshub.R;
import com.example.skillshub.utility.FirestoreDataRetriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WorkerVerifyFragment extends Fragment {

    View view;
    private ImageButton frontNic;
    private ImageButton backNic;
    private ImageButton brImage;
    private ProgressBar progressBar;
    private TextView mainSkillCategory;
    private TextView subSkillCategory;
    private TextView addSkill;
    private ImageView clearNicFront;
    private ImageView clearNicBack;
    private ImageView clearBr;

    private Uri nicFrontUri, nicBackUri, brUri;
    private Map<String, List<String>> skillMap;
    private List<String> subSkillsList;
    private boolean[] selectedSubSkills;
    private List<String> currentSubSkills = new ArrayList<>();
    private ArrayList<Integer> subSkillIndices = new ArrayList<>();
    private FirestoreDataRetriver firestoreDataRetriver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_verify, container, false);
        initializeViews();

        firestoreDataRetriver = new FirestoreDataRetriver();

        return view;
    }

    private void initializeViews() {
        frontNic = view.findViewById(R.id.signup_verify_nicfront_upload);
        backNic = view.findViewById(R.id.signup_verify_nicback_upload);
        brImage = view.findViewById(R.id.signup_verify_br_upload);
        progressBar = view.findViewById(R.id.progressBar);
        mainSkillCategory = view.findViewById(R.id.signup_main_category);
        subSkillCategory = view.findViewById(R.id.signup_sub_category);
        addSkill = view.findViewById(R.id.signup_add_newcategory);
        clearNicFront = view.findViewById(R.id.signup_clear_nicfront_upload);
        clearNicBack = view.findViewById(R.id.signup_clear_nicback_upload);
        clearBr = view.findViewById(R.id.signup_clear_br_upload);
    }

    private void loadSkills() {
        progressBar.setVisibility(View.VISIBLE);
        firestoreDataRetriver.loadSkillsFromFirestore(getContext(), new FirestoreDataRetriver.FirestoreCallback() {
            @Override
            public void onSuccess(Map<String, List<String>> skillMapResult, List<String> mainSkillsResult) {
                skillMap = skillMapResult;
                subSkillsList = mainSkillsResult;
                setupClickListeners();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setupClickListeners() {
        mainSkillCategory.setOnClickListener(v -> showMainSkillDialog());
        subSkillCategory.setOnClickListener(v -> {
            String selectedMainSkill = mainSkillCategory.getText().toString();
            if (!selectedMainSkill.isEmpty()) {
                showSubSkillsDialog(selectedMainSkill);
            } else {
                Toast.makeText(getContext(), "Please select a main category first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMainSkillDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Main Skill");

        String[] mainSkillsArray = subSkillsList.toArray(new String[0]);
        builder.setSingleChoiceItems(mainSkillsArray, -1, (dialog, which) -> {
            mainSkillCategory.setText(mainSkillsArray[which]);
            dialog.dismiss();

            // Clear the sub-skill selection when main skill is changed
            subSkillCategory.setText("");
            currentSubSkills = skillMap.get(mainSkillsArray[which]);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showSubSkillsDialog(String mainSkill) {
        currentSubSkills = skillMap.get(mainSkill);
        if (currentSubSkills == null) {
            return;
        }

        selectedSubSkills = new boolean[currentSubSkills.size()];

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Sub Skills");
        builder.setCancelable(false);

        builder.setMultiChoiceItems(currentSubSkills.toArray(new String[0]), selectedSubSkills, (dialogInterface, i, isChecked) -> {
            if (isChecked) {
                subSkillIndices.add(i);
                Collections.sort(subSkillIndices);
            } else {
                subSkillIndices.remove(Integer.valueOf(i));
            }
        });

        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            StringBuilder subSkillsBuilder = new StringBuilder();
            for (int index : subSkillIndices) {
                subSkillsBuilder.append(currentSubSkills.get(index));
                if (index != subSkillIndices.size() - 1) {
                    subSkillsBuilder.append(", ");
                }
            }
            subSkillCategory.setText(subSkillsBuilder.toString());
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

        builder.setNeutralButton("Clear All", (dialogInterface, i) -> {
            for (int j = 0; j < selectedSubSkills.length; j++) {
                selectedSubSkills[j] = false;
            }
            subSkillIndices.clear();
            subSkillCategory.setText("");
        });

        builder.show();
    }
}