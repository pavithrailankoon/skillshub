package com.example.skillshub.signupform;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.skillshub.ChooseUserActivity;
import com.example.skillshub.LoginActivity;
import com.example.skillshub.R;

public class RegistrationControlActivity extends AppCompatActivity {

    Button signupNextButton;
    Button signupBackButton;
    TextView signupRedirectToLogin;
    private int currentFragmentIndex = 0;
    private Fragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration_control);

        // Handle Next button click to slide to the next fragment
        signupNextButton = findViewById(R.id.signup_next_btn);
        //signupBackButton = findViewById(R.id.signup_back_btn);
        signupRedirectToLogin = findViewById(R.id.signup_redirect_login);

        String registrationType = getIntent().getStringExtra("REGISTRATION_TYPE");
        fragments = setRegistrationFlow(registrationType);

        setClickListeners();

        // Load the Part1 fragment initially (User Basic Information and password)
        loadFragment(fragments[currentFragmentIndex]);



    }

    private void setClickListeners(){
        signupNextButton.setOnClickListener(v -> {
            if (currentFragmentIndex < fragments.length - 1) {
                currentFragmentIndex++;
                loadFragment(fragments[currentFragmentIndex]);
            } else {
                // Last fragment: collect data and store in Firebase
                //saveUserDataToFirebase();
            }
        });


//        signupBackButton.setOnClickListener(v -> {
//            if (currentFragmentIndex > 0) {
//                currentFragmentIndex--;
//                loadFragment(fragments[currentFragmentIndex]);
//            }
//        });

        signupRedirectToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationControlActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleFragmentBackNavigation();
            }
        });
    }

    // Method to load fragments with slide animation
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        try {
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,  // enter
                    R.anim.slide_out_left // exit
//                    R.anim.slide_in_left,   // popEnter
//                    R.anim.slide_out_right  // popExit
            );
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            Log.e("FragmentTransaction", "Error during fragment transaction", e);
        }
        updateNextButtonText(currentFragmentIndex);
    }

    //Set the registration flow for client or worker
    private Fragment[] setRegistrationFlow(String registrationType){
        switch (registrationType.toLowerCase()) {
            case "client":
                return new Fragment[]{new Part1Fragment(), new Part2Fragment(), new Part3Fragment()};
            case "worker":
                return new Fragment[]{new Part1Fragment(), new Part2Fragment(),new WorkerVerifyFragment(), new Part3Fragment()};
            default:
                return new Fragment[]{};
        }
    }

    private void handleFragmentBackNavigation() {
        if (currentFragmentIndex > 0) {
            currentFragmentIndex--;
            loadFragment(fragments[currentFragmentIndex]);
        } else {
            finish();
        }
    }

    private void updateNextButtonText(int currentFragmentIndex) {
        if (currentFragmentIndex == fragments.length - 1) {
            signupNextButton.setText("CREATE");
        } else {
            signupNextButton.setText("NEXT");
        }
    }
}
