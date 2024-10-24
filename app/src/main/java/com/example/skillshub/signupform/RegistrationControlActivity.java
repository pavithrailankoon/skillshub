package com.example.skillshub.signupform;

import static android.app.PendingIntent.getActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.skillshub.ChooseUserActivity;
import com.example.skillshub.LoginActivity;
import com.example.skillshub.R;
import com.example.skillshub.firebaseModel.AuthManager;
import com.example.skillshub.firebaseModel.CreateData;
import com.example.skillshub.firebaseModel.FirebaseStoarageManager;
import com.example.skillshub.model.User;
import com.example.skillshub.utils.LocalDataManager;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class RegistrationControlActivity extends AppCompatActivity {

    private Button signupNextButton;
    private ImageView exitSignupForm;
    private TextView signupRedirectToLogin;
    private int currentFragmentIndex = 0;
    private Fragment[] fragments;

    private LocalDataManager localDataManager;
    private Context context;
    private FirebaseStoarageManager storageManager;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef;
    AuthManager authManager;
    ProgressDialog progressDialog;

    private String registrationType;
    private String profileUrl;
    private String frontNicUrl;
    private String backNicUrl;
    private String brUrl;
    private String uid;

    public RegistrationControlActivity(){
        this.context = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_control);

        progressDialog = new ProgressDialog(this);
        localDataManager = new LocalDataManager();
        storageManager = new FirebaseStoarageManager();
        authManager = new AuthManager();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        signupNextButton = findViewById(R.id.signup_next_btn);
        signupRedirectToLogin = findViewById(R.id.signup_redirect_login);
        exitSignupForm = findViewById(R.id.signup_back_btn);

        registrationType = getIntent().getStringExtra("REGISTRATION_TYPE");
        fragments = setRegistrationFlow(registrationType);

        setClickListeners();

        // Load the Part1 fragment initially
        loadFragment(fragments[currentFragmentIndex]);
    }

    private void setClickListeners(){
        signupNextButton.setOnClickListener(v -> {
            //if (validateCurrentFragment()) {
            if (currentFragmentIndex < fragments.length - 1) {
                currentFragmentIndex++;
                loadFragment(fragments[currentFragmentIndex]);
            } else {
                // Last fragment: collect data and store in Firebase
                saveAndLogin();
                Intent intent = new Intent(RegistrationControlActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            //}
        });

        signupRedirectToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationControlActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        exitSignupForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitConfirmationDialog();
            }
        });

        // Handle system back button press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleFragmentBackNavigation();
            }
        });
    }

    private void saveAndLogin(){
        if (registrationType.equals("client")){
            saveUserDataToFirestore(authManager.getCurrentLoginUser());
        } else if (registrationType.equals("worker")){
            saveUserDataToFirestore(authManager.getCurrentLoginUser());
            saveWorkerDataToFirestore(authManager.getCurrentLoginUser());
        } else if (registrationType.equals("clientToWorker")){
            saveWorkerDataToFirestore(authManager.getCurrentLoginUser());
        }
    }

    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Registration")
                .setMessage("Are you sure you want to exit the registration process? All progress will be lost.")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();                      }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Method to validate current fragment
    private boolean validateCurrentFragment() {
        Fragment currentFragment = fragments[currentFragmentIndex];

        if (currentFragment instanceof Part1Fragment) {
            return ((Part1Fragment) currentFragment).validateInput();
        } else if (currentFragment instanceof Part2Fragment) {
            return ((Part2Fragment) currentFragment).validateInput();
        }

        return true; // Default to true if no validation method is present
    }

    // Method to load fragments with slide animation
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        try {
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,  // enter
                    R.anim.slide_out_left   // exit
            );
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit(); // Commit the transaction
        } catch (Exception e) {
            Log.e("FragmentTransaction", "Error during fragment transaction", e);
        }
        updateNextButtonText(currentFragmentIndex);
    }

    // Set the registration flow for client or worker
    private Fragment[] setRegistrationFlow(String registrationType){
        switch (registrationType.toLowerCase()) {
            case "client":
                return new Fragment[]{new Part1Fragment(), new Part2Fragment()};
            case "worker":
                return new Fragment[]{new Part1Fragment(), new Part2Fragment(), new WorkerVerifyFragment()};
            case "clientToWorker":
                return new Fragment[]{new Part1Fragment(), new WorkerVerifyFragment()};
            default:
                return new Fragment[]{};
        }
    }

    private void handleFragmentBackNavigation() {
        if (currentFragmentIndex > 0) {
            currentFragmentIndex--;
            loadFragment(fragments[currentFragmentIndex]);
        } else {
            showExitConfirmationDialog();
        }
    }

    private void updateNextButtonText(int currentFragmentIndex) {
        if (currentFragmentIndex == fragments.length - 1) {
            signupNextButton.setText("SAVE & LOGIN");
        } else {
            signupNextButton.setText("NEXT");
        }
    }

    private void saveUserDataToFirestore(FirebaseUser user) {
        progressDialog.setTitle("Please Wait..");
        progressDialog.setMessage("Saving user data...");
        progressDialog.show();
        // Collect data from fragments
        String fullName = ((Part1Fragment) fragments[0]).getFullName();
        String phone = ((Part1Fragment) fragments[0]).getPhoneNumber();
        Uri profileUri = ((Part1Fragment) fragments[0]).getImageUri();
        String email = localDataManager.getDataLocal(context, "userEmail");
        String nic = localDataManager.getDataLocal(context, "userNic");
        String address1 = ((Part2Fragment) fragments[1]).getAddressLine1();
        String address2 = ((Part2Fragment) fragments[1]).getAddressLine2();
        String district = ((Part2Fragment) fragments[1]).getDistrict();
        String city = ((Part2Fragment) fragments[1]).getCity();

        String role = registrationType;

        CreateData createData = new CreateData();

        // Create location data map
        User userData = new User();

        storageManager.uploadImageFiles(user.getUid(), "profile-image", profileUri, new FirebaseStoarageManager.OnImageUploadCompleteListener() {
            @Override
            public void onSuccess(String profileUrl) {
                // Profile image uploaded, now save user data to Firestore
                Map<String, Object> userDataMap = userData.createUserData(fullName, phone, email, nic, profileUrl, role, address1, address2, district, city);

                createData.saveUserDataToFirestore(user.getUid(), userDataMap,
                        () -> Toast.makeText(RegistrationControlActivity.this, "User data added successfully", Toast.LENGTH_SHORT).show(),
                        () -> Toast.makeText(RegistrationControlActivity.this, "Error adding user data", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(RegistrationControlActivity.this, "Failed to upload profile image: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        progressDialog.cancel();
    }

    private void saveWorkerDataToFirestore(FirebaseUser user){
        progressDialog.setTitle("Please Wait..");
        progressDialog.setMessage("Saving user data...");
        progressDialog.show();
        Uri nicFrontUri = ((WorkerVerifyFragment) fragments[2]).getNicFront();
        Uri nicBackUri = ((WorkerVerifyFragment) fragments[2]).getNicBack();
        Uri brUri = ((WorkerVerifyFragment) fragments[2]).getBr();

        CreateData createData = new CreateData();
        User userData = new User();

        storageManager.uploadImageFiles(user.getUid(), "nic-front-image", nicFrontUri, new FirebaseStoarageManager.OnImageUploadCompleteListener() {
            @Override
            public void onSuccess(String nicFrontUrl) {
                storageManager.uploadImageFiles(user.getUid(), "nic-back-image", nicBackUri, new FirebaseStoarageManager.OnImageUploadCompleteListener() {
                    @Override
                    public void onSuccess(String nicBackUrl) {
                        // Now upload BR image
                        storageManager.uploadImageFiles(user.getUid(), "br-image", brUri, new FirebaseStoarageManager.OnImageUploadCompleteListener() {
                            @Override
                            public void onSuccess(String brUrl) {
                                // All images uploaded, now save user data to Firestore

                                // Create the NICVerification data map
                                Map<String, Object> nicVerification = userData.createWorkerNicData(nicFrontUrl, nicBackUrl, false);

                                // Create the BRVerification data map
                                Map<String, Object> brVerification = userData.createWorkerBrData(brUrl, false);

                                // Combine the data into the main user map
                                Map<String, Object> workerDataMap = userData.createWorkerData(nicVerification, brVerification);

                                // Save all worker data
                                createData.addWorkerInformation(user.getUid(), "workerInformation", workerDataMap, new CreateData.OnWorkerDataUploadListener() {
                                    @Override
                                    public void onSuccess() {
                                        // Handle success, e.g., notify the user
                                        System.out.println("Worker data uploaded successfully!");
                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        // Handle failure, e.g., show an error message
                                        System.err.println("Error uploading worker data: " + errorMessage);
                                    }
                                });
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(RegistrationControlActivity.this, "Failed to upload BR image: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(RegistrationControlActivity.this, "Failed to upload NIC back image: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(RegistrationControlActivity.this, "Failed to upload NIC front image: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        progressDialog.cancel();
    }
}