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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.skillshub.LoginActivity;
import com.example.skillshub.R;
import com.example.skillshub.WorkerProfileView;
import com.example.skillshub.firebaseModel.AuthManager;
import com.example.skillshub.firebaseModel.CreateData;
import com.example.skillshub.firebaseModel.FirebaseStorageManager;
import com.example.skillshub.model.User;
import com.example.skillshub.utils.LocalDataManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class RegistrationControlActivity extends AppCompatActivity {

    private Button signupNextButton;
    private LinearLayout redirectLoginLayout;
    private ImageView exitSignupForm;
    private TextView signupRedirectToLogin;
    private int currentFragmentIndex = 0;
    private Fragment[] fragments;

    private LocalDataManager localDataManager;
    private Context context;
    private FirebaseStorageManager storageManager;
    private CreateData createData;
    private User userData;
    FirebaseUser user;
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
        storageManager = new FirebaseStorageManager();
        authManager = new AuthManager();
        createData = new CreateData();
        userData = new User();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        signupNextButton = findViewById(R.id.signup_next_btn);
        signupRedirectToLogin = findViewById(R.id.signup_redirect_login);
        exitSignupForm = findViewById(R.id.signup_back_btn);
        redirectLoginLayout = findViewById(R.id.signup_login_redirect_layout);

        registrationType = getIntent().getStringExtra("REGISTRATION_TYPE");
        assert registrationType != null;
        fragments = setRegistrationFlow(registrationType);

        setClickListeners();

        // Load the Part1 fragment initially
        loadFragment(fragments[currentFragmentIndex]);
    }

    private void setClickListeners(){
        signupNextButton.setOnClickListener(v -> {
            if (validateCurrentFragment()) {
            if (currentFragmentIndex < fragments.length - 1) {
                currentFragmentIndex++;
                loadFragment(fragments[currentFragmentIndex]);
            } else {
                // Last fragment: collect data and store in Firebase
                saveAndLogin();
                if (registrationType.equals("client") || registrationType.equals("worker")){
                    Intent intent = new Intent(RegistrationControlActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else if(registrationType.equals("clienttoworker")){
                    Intent intent = new Intent(RegistrationControlActivity.this, WorkerProfileView.class);
                    startActivity(intent);
                    finish();
                }
            }
            }
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
        } else if (registrationType.equals("clienttoworker")){
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
        } else if (currentFragment instanceof WorkerVerifyFragment) {
            return ((WorkerVerifyFragment) currentFragment).validateInput();
        }

        return true;
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
            case "clienttoworker":
                return new Fragment[]{new WorkerVerifyFragment()};
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
            if (registrationType.equals("clienttoworker")){
                signupNextButton.setText("PROCEED TO WORKER PROFILE");
                redirectLoginLayout.setVisibility(View.GONE);
            }
        } else {
            signupNextButton.setText("NEXT");
        }
    }

    private void saveUserDataToFirestore(FirebaseUser user) {
//        progressDialog.setTitle("Please Wait..");
//        progressDialog.setMessage("Saving user data...");
//        progressDialog.show();
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

        storageManager.uploadImageFiles(user.getUid(), "profile-image", profileUri, new FirebaseStorageManager.OnImageUploadCompleteListener() {
            @Override
            public void onSuccess(String profileUrl) {
//                progressDialog.cancel();
                // Profile image uploaded, now save user data to Firestore
                Map<String, Object> userDataMap = userData.createUserData(fullName, phone, email, nic, profileUrl, role, address1, address2, district, city);

                createData.saveUserDataToFirestore(user.getUid(), userDataMap,
                        () -> Toast.makeText(RegistrationControlActivity.this, "User data added successfully", Toast.LENGTH_SHORT).show(),
                        () -> Toast.makeText(RegistrationControlActivity.this, "Error adding user data", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onFailure(String errorMessage) {
//                progressDialog.cancel();
                Toast.makeText(RegistrationControlActivity.this, "Failed to upload profile image: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void saveWorkerDataToFirestore(FirebaseUser user) {
        Uri nicFrontUri = ((WorkerVerifyFragment) fragments[2]).getNicFront();
        Uri nicBackUri = ((WorkerVerifyFragment) fragments[2]).getNicBack();
        Uri brUri = ((WorkerVerifyFragment) fragments[2]).getBr();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get the user ID

        storageManager.uploadMultipleImages(uid, nicFrontUri, nicBackUri, brUri, new FirebaseStorageManager.OnImagesUploadCompleteListener() {
            @Override
            public void onAllUploadsSuccess(String nicFrontUrl, String nicBackUrl, String brUrl) {
                saveImageUrlsToFirestore(uid, nicFrontUrl, nicBackUrl, brUrl);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(RegistrationControlActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveImageUrlsToFirestore(String uid, String nicFrontUrl, String nicBackUrl, String brUrl) {
        // Create a map to hold the image URLs and other fields
        Map<String, Object> workerInfo = new HashMap<>();
        workerInfo.put("BrImageURL", brUrl);
        workerInfo.put("NicBackImageURL", nicBackUrl);
        workerInfo.put("NicFrontImageURL", nicFrontUrl);
        workerInfo.put("description", null);
        workerInfo.put("isBrVerified", false);
        workerInfo.put("isNicVerified", false);

        // Save to Firestore under the specified path
        db.collection("users").document(uid)
                .collection("workerInformation").add(workerInfo)
                .addOnSuccessListener(documentReference -> {
                    saveworkerCategory();
                    Toast.makeText(RegistrationControlActivity.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegistrationControlActivity.this, "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveworkerCategory() {
        Map<String, Object> selectedSkills = ((WorkerVerifyFragment) fragments[2]).getCategories();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Object selectedSubcategories = selectedSkills.get("subcategories");
        String selectedCategory = "";
        for (Map.Entry<String, Object> entry : selectedSkills.entrySet()) {
            selectedCategory = entry.getKey();
            break;
        }

        createData.saveWorkerCategory(uid, selectedCategory, selectedSubcategories);
    }
}