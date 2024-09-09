package com.example.skillshub.signupform;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skillshub.R;
import com.example.skillshub.utility.FirestoreDataRetriver;

public class Part2Fragment extends Fragment {

    View view;
    private TextView email;
    private TextView nic;
    private TextView addressLine1;
    private TextView addressLine2;
    private AutoCompleteTextView city;
    private AutoCompleteTextView district;

    private FirestoreDataRetriver firestoreDataRetriver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_part2, container, false);
        email = view.findViewById(R.id.signup_email);
        nic = view.findViewById(R.id.signup_nic);
        addressLine1 = view.findViewById(R.id.signup_addressline1);
        addressLine2 = view.findViewById(R.id.signup_addressline2);
        city = view.findViewById(R.id.signup_city);
        district = view.findViewById(R.id.signup_district);

        firestoreDataRetriver = new  FirestoreDataRetriver();

        //Populate districts in AutoCompleteTextViews
        populateDistricts(getContext(), firestoreDataRetriver);

        return view;
    }

    private void populateDistricts(Context context, FirestoreDataRetriver firestoreDataRetriver){
        firestoreDataRetriver.retriveDistricts(districts -> {
            if (districts != null && !districts.isEmpty()) {
                ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, districts);
                district.setAdapter(districtAdapter);

                district.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedDistrict = (String) parent.getItemAtPosition(position);
                    retriveCities(selectedDistrict);
                });
            }
        });
    }

    private void retriveCities(String selectedDistrict) {
        firestoreDataRetriver.retriveCities(selectedDistrict, cities -> {
            if (cities != null && !cities.isEmpty()) {
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, cities);
                city.setAdapter(cityAdapter);
            } else {
                Toast.makeText(requireContext(), "Select your district first", Toast.LENGTH_SHORT).show();
            }
        });
    }
}