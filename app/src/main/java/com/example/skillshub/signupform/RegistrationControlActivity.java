package com.example.skillshub.signupform;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.skillshub.R;

public class RegistrationControlActivity extends AppCompatActivity {

    Button signupNextButton;
    Button signupBackButton;
    private int currentFragmentIndex = 0;
    private Fragment[] fragments = {new Part1Fragment(), new Part2Fragment(), new WorkerVerifyFragment()};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration_control);

        // Retrieve the fragments order from the RegistrationActivity
//        String registrationType = getIntent().getStringExtra("registrationType");
//        fragments = setRegistrationFlow(registrationType);

        // Handle Next button click to slide to the next fragment
        signupNextButton = findViewById(R.id.signup_next_btn);
        //signupBackButton = findViewById(R.id.signup_back_btn);

        // Load the Part1 fragment initially (User Basic Information and password)
        loadFragment(fragments[currentFragmentIndex]);

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

    }

    // Method to load fragments with slide animation
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        try {
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,  // enter
                    R.anim.slide_out_left,  // exit
                    R.anim.slide_in_left,   // popEnter
                    R.anim.slide_out_right  // popExit
            );
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            Log.e("FragmentTransaction", "Error during fragment transaction", e);
        }
    }

    //Set the registration flow for client or worker
    private Fragment[] setRegistrationFlow(String registrationType){
        switch (registrationType.toLowerCase()) {
            case "client":
                return new Fragment[]{new Part1Fragment(), new Part2Fragment(), new Part3Fragment()};
            case "worker":
                return new Fragment[]{new Part1Fragment(), new Part2Fragment(), new WorkerVerifyFragment()};
            default:
                return new Fragment[]{};
        }
    }
}
