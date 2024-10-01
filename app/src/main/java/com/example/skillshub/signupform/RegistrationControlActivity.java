package com.example.skillshub.signupform;

import static android.app.PendingIntent.getActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.skillshub.ChooseUserActivity;
import com.example.skillshub.LoginActivity;
import com.example.skillshub.R;
import com.example.skillshub.clientHome;
import com.example.skillshub.firebaseModel.CreateData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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
    private Button signupBackButton;
    private TextView signupRedirectToLogin;
    private int currentFragmentIndex = 0;
    private Fragment[] fragments;

    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef;

    private String registrationType;
    private String profileUrl;
    private String frontNicUrl;
    private String backNicUrl;
    private String brUrl;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_control);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        signupNextButton = findViewById(R.id.signup_next_btn);
//        signupBackButton = findViewById(R.id.signup_back_btn);
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
            if (validateCurrentFragment()) {
                if (currentFragmentIndex < fragments.length - 1) {
                    currentFragmentIndex++;
                    loadFragment(fragments[currentFragmentIndex]);
                } else {
                    // Last fragment: collect data and store in Firebase
                     saveUserDataToFirebaseAuth();
                }
            }
        });

//        signupBackButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (getActivity() != null) {
//                    getActivity().onBackPressed();  // Call the activity's back handling
//                }
//            }
//        });

        signupRedirectToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationControlActivity.this, LoginActivity.class);
            startActivity(intent);
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
                return new Fragment[]{new Part1Fragment(), new Part2Fragment(), new Part3Fragment()};
            case "worker":
                return new Fragment[]{new Part1Fragment(), new Part2Fragment(), new WorkerVerifyFragment(), new Part3Fragment()};
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
            signupNextButton.setText("SEND LINK");
        } else {
            signupNextButton.setText("NEXT");
        }
    }

    private void saveUserDataToFirebaseAuth() {
        String password = ((Part1Fragment) fragments[0]).getPassword();
        String email = ((Part2Fragment) fragments[1]).getEmail();

        authUser(email, password);
    }

    public void authUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(RegistrationControlActivity.this, "Verification email sent to: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                                    checkEmailVerification(user);
                                                    signupNextButton.setText("RESEND LINK");
                                                } else {
                                                    Toast.makeText(RegistrationControlActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                                    signupNextButton.setText("RESEND LINK");
                                                }
                                            }
                                        });
                            }
                        } else {
                            // If sign-up fails, display an error message
                            Toast.makeText(RegistrationControlActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserDataToFirestore(FirebaseUser user) {
        // Collect data from fragments
        String firstName = ((Part1Fragment) fragments[0]).getFirstName();
        String lastName = ((Part1Fragment) fragments[0]).getLastName();
        String phone = ((Part1Fragment) fragments[0]).getPhoneNumber();
        String password = ((Part1Fragment) fragments[0]).getPassword();
        Uri profileUri = ((Part1Fragment) fragments[0]).getImageUri();

        String email = ((Part2Fragment) fragments[1]).getEmail();
        String nic = ((Part2Fragment) fragments[1]).getNic();
        String address1 = ((Part2Fragment) fragments[1]).getAddressLine1();
        String address2 = ((Part2Fragment) fragments[1]).getAddressLine2();
        String district = ((Part2Fragment) fragments[1]).getDistrict();
        String city = ((Part2Fragment) fragments[1]).getCity();

        Map<String, Object> userData = new HashMap<>();
        Map<String, Object> location = new HashMap<>();

        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        userData.put("phone", phone);
        userData.put("email", email);
        userData.put("nic", nic);
        userData.put("role", registrationType);
        location.put("address1", address1);
        location.put("address2", address2);
        location.put("district", district);
        location.put("city", city);

        // Store data in Firestore with document ID as uid
        uid = user.getUid();

        // Create a reference to the folder in Firebase Storage
        StorageReference userFolderRef = storageRef.child(uid);

        // Create file name for the image
        String fileName = "profile_image";

        // Create a reference to the image file in the user's folder
        StorageReference imageRef = userFolderRef.child(fileName);

        // Upload the file to Firebase Storage
        imageRef.putFile(profileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    Toast.makeText(this, "Profile image uploaded successfully", Toast.LENGTH_SHORT).show();

                    // Get the download URL of the uploaded image
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        profileUrl = uri.toString();
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    Toast.makeText(this, "Failed to upload profile image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Store profile image url in firestore
        userData.put("profileImageURL", profileUrl);

        // Add the location map to the userData map
        userData.put("location", location);

        db.collection("users").document(uid)
                .set(userData, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegistrationControlActivity.this, "User data added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegistrationControlActivity.this, "Error adding user data", Toast.LENGTH_SHORT).show();
                    }
                });

        loginUser(email, password);
    }

    private void saveWorkerDataToFirestore(FirebaseUser user){
        Uri nicFrontUri = ((WorkerVerifyFragment) fragments[2]).getNicFront();
        Uri nicBackUri = ((WorkerVerifyFragment) fragments[2]).getNicBack();
        Uri brUri = ((WorkerVerifyFragment) fragments[2]).getBr();

        // Create a reference to the folder in Firebase Storage
        StorageReference userFolderRef = storageRef.child(uid);

        // Create file name for the image
        String nicFrontfileName = "nic_front_image";
        String nicBackfileName = "nic_back_image";
        String brfileName = "br_certification_image";

        // Create a reference to the image file in the user's folder
        StorageReference frontNicRef = userFolderRef.child(nicFrontfileName);
        StorageReference backNicRef = userFolderRef.child(nicBackfileName);
        StorageReference brRef = userFolderRef.child(brfileName);

        // Upload the file to Firebase Storage
        frontNicRef.putFile(nicFrontUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    Toast.makeText(this, "NIC front image uploaded successfully", Toast.LENGTH_SHORT).show();

                    // Get the download URL of the uploaded image
                    frontNicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        frontNicUrl = uri.toString();
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    Toast.makeText(this, "Failed to upload profile image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        backNicRef.putFile(nicBackUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    Toast.makeText(this, "NIC back image uploaded successfully", Toast.LENGTH_SHORT).show();

                    // Get the download URL of the uploaded image
                    backNicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        backNicUrl = uri.toString();
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    Toast.makeText(this, "Failed to upload profile image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        brRef.putFile(brUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    Toast.makeText(this, "NIC back image uploaded successfully", Toast.LENGTH_SHORT).show();

                    // Get the download URL of the uploaded image
                    brRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        brUrl = uri.toString();
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    Toast.makeText(this, "Failed to upload profile image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Define the map structure for BRVerification
        Map<String, Object> brVerification = new HashMap<>();
        brVerification.put("BRimageURL", brUrl);
        brVerification.put("isBrVerified", false);

        // Define the map structure for NICVerification
        Map<String, Object> nicVerification = new HashMap<>();
        nicVerification.put("NicBackImageURL", backNicUrl);
        nicVerification.put("NicFrontImageURL", frontNicUrl);
        nicVerification.put("isNicVerified", false);

        // Create the worker information map
        Map<String, Object> workerInfo = new HashMap<>();
        workerInfo.put("BRVerification", brVerification);
        workerInfo.put("NICVerification", nicVerification);

        DocumentReference userDocument = db.collection("users").document(uid);
        CollectionReference workersInfoCollction = userDocument.collection("workerInformation");

        // Add a new document to sub collecion
        workersInfoCollction.add(workerInfo)
                .addOnSuccessListener(documentReference -> {
                    //String workerId = documentReference.getId();
                    Toast.makeText(this, "Worker profile created", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error creating worker profile", Toast.LENGTH_SHORT).show();
                });
    }

    public void checkEmailVerification(FirebaseUser user) {
        Handler handler = new Handler();  // Create a handler to execute the check
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                user.reload().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (user.isEmailVerified()) {
                            Toast.makeText(RegistrationControlActivity.this, "User is verified", Toast.LENGTH_SHORT).show();

                            // Stop further checks by removing the callbacks immediately after verification
                            handler.removeCallbacks(this);

                            // Redirect based on registration type
                            if (registrationType.equals("client")) {
                                // Save client details and redirect to client home
                                saveUserDataToFirestore(user);
                            } else if (registrationType.equals("worker")) {
                                saveUserDataToFirestore(user);
                                saveWorkerDataToFirestore(user);
                            }

                            Toast.makeText(RegistrationControlActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistrationControlActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();  // Finish the current activity to prevent going back
                        } else {
                            Toast.makeText(RegistrationControlActivity.this, "Email not verified yet, retrying...", Toast.LENGTH_SHORT).show();

                            // Retry after a delay if email is not verified
                            handler.postDelayed(this, 3000);  // Retry after 3 seconds
                        }
                    } else {
                        Toast.makeText(RegistrationControlActivity.this, "Failed to reload user. Please try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        // Start the handler to check every 3 seconds
        handler.postDelayed(runnable, 3000);
    }




    public void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            checkEmailVerification(user);
                        }
                    } else {
                        Toast.makeText(RegistrationControlActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}