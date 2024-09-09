package com.example.skillshub.signupform;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.skillshub.signupform.RegistrationControlActivity;
import com.example.skillshub.R;

import java.util.List;
import java.util.Map;

public class Part3Fragment extends Fragment {

//    private String nicFrontImagePath;
//    private String nicBackImagePath;
//    private String brImagePath;
//    private Map<String, List<String>> skillsMap;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_part3, container, false);

//        Button signupNextButton = getActivity().findViewById(R.id.signup_next_btn);
//        Button signupBackButton = getActivity().findViewById(R.id.signup_back_btn);
//
//        signupNextButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Handle form submission
//                // For example, send data to the server or save it locally
//            }
//        });
//
//        // Handle the BACK button click
//        signupBackButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().onBackPressed(); // Go back to the previous fragment
//            }
//        });

        return view;
    }
}