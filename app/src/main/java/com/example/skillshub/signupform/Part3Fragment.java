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

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_part3, container, false);

        return view;
    }
}