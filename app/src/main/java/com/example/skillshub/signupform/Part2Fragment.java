package com.example.skillshub.signupform;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skillshub.R;
import com.example.skillshub.firebaseModel.ReadData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import java.util.ArrayList;
import java.util.List;

public class Part2Fragment extends Fragment {

    View view;
    private EditText email;
    private EditText nic;
    private EditText addressLine1;
    private EditText addressLine2;
    private AutoCompleteTextView city;
    private AutoCompleteTextView district;
    private ReadData readData;
    ProgressDialog progressDialog;

    private ArrayAdapter<String> districtAdapter;
    private ArrayAdapter<String> cityAdapter;

    private FirebaseAuth auth;

    public interface EmailCheckCallback {
        void onEmailCheckCompleted(boolean emailExists);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_part2, container, false);
        initializeViews();

        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getActivity());
        readData = new ReadData();

        validateDistrictCity();

        return view;
    }

    private void initializeViews(){
        email = view.findViewById(R.id.signup_email);
        nic = view.findViewById(R.id.signup_nic);
        addressLine1 = view.findViewById(R.id.signup_addressline1);
        addressLine2 = view.findViewById(R.id.signup_addressline2);
        city = view.findViewById(R.id.signup_city);
        district = view.findViewById(R.id.signup_district);

        // Add TextWatchers to listen for text changes
        email.addTextChangedListener(new Part2Fragment.GenericTextWatcher(email));
        nic.addTextChangedListener(new Part2Fragment.GenericTextWatcher(nic));
        city.addTextChangedListener(new Part2Fragment.GenericTextWatcher(city));
        district.addTextChangedListener(new Part2Fragment.GenericTextWatcher(district));
    }

    private void validateDistrictCity(){
        // Initialize adapters
        districtAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        district.setAdapter(districtAdapter);

        cityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        city.setAdapter(cityAdapter);

        city.setEnabled(false);

        loadDistricts();

        setupDistrictSelectionListener();
        setupCityClickListener();
    }

    private void loadDistricts() {
        readData.getDistricts(districts -> {
            districtAdapter.clear();
            districtAdapter.addAll(districts);
            districtAdapter.notifyDataSetChanged();
        }, e -> Log.e("MainActivity", "Failed to load districts", e));
    }

    private void setupDistrictSelectionListener() {
        district.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDistrict = (String) parent.getItemAtPosition(position);
            loadCities(selectedDistrict);
            city.setEnabled(true);
        });
    }

    private void setupCityClickListener() {
        city.setOnClickListener(v -> {
            if (!city.isEnabled()) {
                Toast.makeText(getContext(), "Please, select district first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCities(String district) {
        readData.getCities(district, cities -> {
            cityAdapter.clear();
            cityAdapter.addAll(cities);
            cityAdapter.notifyDataSetChanged();
        }, e -> Log.e("MainActivity", "Failed to load cities", e));
    }


    //Add getters to send values to RgistrationControlActivity
    public String getEmail() {
        return email.getText().toString();
    }

    public String getNic() {
        return nic.getText().toString();
    }

    public String getAddressLine1() {
        return addressLine1.getText().toString();
    }

    public String getAddressLine2() {
        return addressLine2.getText().toString();
    }

    public String getDistrict() {
        return district.getText().toString();
    }

    public String getCity() {
        return city.getText().toString();
    }

    // Validation method
    public boolean validateInput() {
        boolean isValid = true;

        // Validate email

        String strEmail = email.getText().toString().trim();
        if (strEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
                if(strEmail.isEmpty()){ email.setError("Email is required"); }
                if(!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()){ email.setError("Invalid email address"); }
                //if(checkEmailExists(strEmail)){ email.setError("Email address is already in use"); }
            isValid = false;
        }

       // Validate NIC number
        String strNic = nic.getText().toString().trim();
        if (strNic.isEmpty() || !(strNic.length() == 10 || strNic.length() == 12) || (strNic.length() == 10 && !strNic.matches(".*[VXvx]$"))) {
            if (strNic.isEmpty()) {
                nic.setError("NIC number is required");
            } else if (!(strNic.length() == 10 || strNic.length() == 12)) {
                nic.setError("NIC number does not contain valid character count");
            } else if (strNic.length() == 10 && !strNic.matches(".*[VXvx]$")) {
                nic.setError("Last character should changed with 'V' or 'X'");
            } else if (strNic.length() == 9 ) {
                nic.setError("Your NIC number should end with 'V' or 'X'");
            }
            isValid = false;
        }


        // Validate districts
        if (getDistrict().isEmpty()) {
            district.setError("District is required");
            isValid = false;
        }

        // Validate city
        if (getCity().isEmpty()) {
            city.setError("City is required");
            isValid = false;
        }
        return isValid;
    }

    // Method to check if the email exists in Firebase Auth
    private void checkEmailExists(String strEmail, EmailCheckCallback callback) {
        auth.fetchSignInMethodsForEmail(strEmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean emailExists = !task.getResult().getSignInMethods().isEmpty();
                if (emailExists) {
                    email.setError("Email already in use");
                }
                callback.onEmailCheckCompleted(emailExists);
            } else {
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    email.setError("Invalid email");
                }
                callback.onEmailCheckCompleted(false);
            }
        });
    }

    private class GenericTextWatcher implements TextWatcher {

        private EditText editText;

        public GenericTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            email.setError(null);
            email.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editText == email) {
                String strEmail = email.getText().toString().trim();

                checkEmailExists(strEmail, emailExists -> {
                    if (!emailExists) {
                        validateInput();
                    }
                });
            } else {
                validateInput();
            }
        }
    }
}