package com.example.skillshub.signupform;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.skillshub.signupform.RegistrationControlActivity;
import com.example.skillshub.R;

public class Part2Fragment extends Fragment {

//    private String email;
//    private String nic;
//    private String addressLine1;
//    private String addressLine2;
//    private String city;
//    private String district;
//    private int postalCode;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_part2, container, false);

//        Button signupNextButton = getActivity().findViewById(R.id.signup_next_btn);
//        Button signupBackButton = getActivity().findViewById(R.id.signup_back_btn);
//
//        //Sliding to next
//        signupNextButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((ClientRegistrationActivity) getActivity()).goToNextStep(new Part3Fragment());
//            }
//        });
//
//        //Sliding to back
//        signupBackButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().onBackPressed(); // Go back to the previous fragment
//            }
//        });

        return view;
    }
}