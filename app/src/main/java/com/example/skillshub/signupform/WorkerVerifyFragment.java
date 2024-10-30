package com.example.skillshub.signupform;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skillshub.ChooseUserActivity;
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
    private ProgressBar progressBar;
    private ImageView clearNicFront, clearNicBack, clearBr;
    private Uri nicFrontUri, nicBackUri, brUri;
    private Map<String, Object> selectedData;
    private ReadData readData;
    private String selectedCategory;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_verify, container, false);
        initializeViews(view);

        frontNic.setOnClickListener(v -> addFrontNic());
        backNic.setOnClickListener(v -> addBackNic());
        brImage.setOnClickListener(v -> addBr());

        readData = new ReadData();

        clearNicFront.setOnClickListener(v -> nicFrontUri = null);
        clearNicBack.setOnClickListener(v -> nicBackUri = null);
        clearBr.setOnClickListener(v -> brUri = null);

        addSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomAlertDialog();
            }
        });

        return view;
    }

    @SuppressLint("MissingInflatedId")
    public void showCustomAlertDialog() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View view = inflater.inflate(R.layout.custom_list_item_category, null);

        Spinner spinnerMainCategory = view.findViewById(R.id.spinner_main_category);
        LinearLayout checkboxContainer = view.findViewById(R.id.checkbox_container);
        progressBar = view.findViewById(R.id.progressbarCategory);

        progressBar.setVisibility(View.VISIBLE);

        readData.getMainCategories(requireContext(), mainCategories -> {
            progressBar.setVisibility(View.GONE);

            if (mainCategories != null && !mainCategories.isEmpty()) {
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, mainCategories);
                spinnerMainCategory.setAdapter(categoryAdapter);

                spinnerMainCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedCategory = mainCategories.get(position);
                        readData.loadSubcategories(selectedCategory, checkboxContainer, requireContext());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        checkboxContainer.removeAllViews();
                    }
                });
            } else {
                Toast.makeText(requireContext(), "No main categories found", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view)
                .setTitle("Select Categories and Subcategories")
                .setPositiveButton("Save", (dialog, which) -> {
                    selectedCategory = (String) spinnerMainCategory.getSelectedItem();
                    ArrayList<String> selectedSubcategories = new ArrayList<>();

                    for (int i = 0; i < checkboxContainer.getChildCount(); i++) {
                        CheckBox checkBox = (CheckBox) checkboxContainer.getChildAt(i);
                        if (checkBox.isChecked()) {
                            selectedSubcategories.add(checkBox.getText().toString());
                        }
                    }

                    if (selectedCategory != null) {
                        selectedData.put("categoryName", selectedCategory);
                        selectedData.put("subcategories", selectedSubcategories);
                        Toast.makeText(requireContext(), "Categories collected", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void initializeViews(View view) {
        frontNic = view.findViewById(R.id.signup_verify_nicfront_upload);
        backNic = view.findViewById(R.id.signup_verify_nicback_upload);
        brImage = view.findViewById(R.id.signup_verify_br_upload);
        addSkill = view.findViewById(R.id.signup_add_newcategory);
        clearNicFront = view.findViewById(R.id.signup_clear_nicfront_upload);
        clearNicBack = view.findViewById(R.id.signup_clear_nicback_upload);
        clearBr = view.findViewById(R.id.signup_clear_br_upload);

        selectedData = new HashMap<>();
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

        if (resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri selectedUri = data.getData();
            if (requestCode == 1) {
                nicFrontUri = selectedUri;
                frontNic.setBackgroundColor(nicFrontUri != null ? Color.parseColor("#0000FF") : Color.parseColor("#818589"));
                Toast.makeText(requireContext(), "Front NIC image added", Toast.LENGTH_LONG).show();
            } else if (requestCode == 2) {
                nicBackUri = selectedUri;
                backNic.setBackgroundColor(nicBackUri != null ? Color.parseColor("#0000FF") : Color.parseColor("#818589"));
                Toast.makeText(requireContext(), "Back NIC image added", Toast.LENGTH_LONG).show();
            } else if (requestCode == 3) {
                brUri = selectedUri;
                brImage.setBackgroundColor(brUri != null ? Color.parseColor("#0000FF") : Color.parseColor("#818589"));
                Toast.makeText(requireContext(), "Business Certificate image added", Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean validateInput() {
        // Check if the front NIC is uploaded
        if (nicFrontUri == null) {
            Toast.makeText(requireContext(), "Please upload the front side of your NIC.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if the back NIC is uploaded
        if (nicBackUri == null) {
            Toast.makeText(requireContext(), "Please upload the back side of your NIC.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if at least one skill category and subcategory is selected
        if (selectedData == null || selectedData.isEmpty() ||
                !selectedData.containsKey("subcategories") ||
                ((List<String>) selectedData.get("subcategories")).isEmpty()) {
            Toast.makeText(requireContext(), "Please select at least one skill category and subcategory.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // All required fields are valid
        return true;
    }



    public Uri getNicFront() {
        return nicFrontUri;
    }

    public Uri getNicBack() {
        return nicBackUri;
    }

    public Uri getBr() {
        return brUri;
    }

    public String getMainCategory(){
        return selectedCategory;
    }

    public Map<String, Object> getCategories(){
        return selectedData;
    }

}
