package com.example.skillshub.signupform;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skillshub.ChooseUserActivity;
import com.example.skillshub.LoginActivity;
import com.example.skillshub.R;
import com.example.skillshub.clientHome;
import com.example.skillshub.firebaseModel.AuthManager;
import com.example.skillshub.firebaseModel.ReadData;
import com.example.skillshub.utils.DialogUtils;
import com.example.skillshub.utils.LocalDataManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticateActivity extends AppCompatActivity {

    private EditText email;
    private EditText nic;
    private TextView generatedPwd;
    private TextView copyPwdButton;
    private TextView signupRedirectToLogin;
    private Button authCheck;
    private Button authLeave;

    private String strEmail;
    private String strPwd;
    private String strNic;
    private Context context;
    private AuthManager authManager;
    private DialogUtils dialogUtils;
    private ReadData readData;
    private LocalDataManager localDataManager;

    public static final String SHARED_PREFS = "userPrefs";

    public AuthenticateActivity(){
        this.context = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authenticate);

        localDataManager = new LocalDataManager();
        dialogUtils = new DialogUtils();
        authManager = new AuthManager();
        readData = new ReadData();

        email = findViewById(R.id.auth_email);
        nic = findViewById(R.id.auth_nic);
        generatedPwd = findViewById(R.id.generated_password);
        copyPwdButton = findViewById(R.id.copy_button);
        copyPwdButton.setPaintFlags(copyPwdButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        authCheck = findViewById(R.id.auth_check_button);
        authLeave = findViewById(R.id.auth_leave_button);
        signupRedirectToLogin = findViewById(R.id.signup_redirect_login);

        email.addTextChangedListener(new GenericTextWatcher(email));
        nic.addTextChangedListener(new GenericTextWatcher(nic));

        authCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogBox();
            }
        });
        authLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveUser();
            }
        });
        signupRedirectToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(AuthenticateActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Copy button functionality
        copyPwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = generatedPwd.getText().toString();
                if (!password.isEmpty()) {
                    copyToClipboard(password);
                } else {
                    showError("Enter valid email and nic first");
                }
            }
        });
    }

    private void openDialogBox(){
        strEmail = email.getText().toString().trim();
        strNic = nic.getText().toString().trim();

        dialogUtils.showAlertDialog(
                this,  // context, typically 'this' in an Activity or 'getContext()' in a Fragment
                "Verification Status",  // Title
                "Please check you verified or not?",  // Message
                "I`ve verified",  // Positive button text
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle positive button click (e.g., delete item)
                        checkEmailVerified();
                    }
                },
                "Send link",  // Neutral button text
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle neutral button click (e.g., dismiss dialog)
                        if(validateInput()){
                                authManager.checkIfEmailExists(strEmail, new AuthManager.EmailCheckCallback() {
                                    @Override
                                    public void onEmailCheckComplete(boolean exists) {
                                        if (!exists) {
                                            readData.checkNicExists("users", "nic", strNic, new ReadData.FirestoreNicCallback() {
                                                @Override
                                                public void onCallback(boolean exists) {
                                                    if (exists) {
                                                        // NIC already exists, show error or handle it
                                                        showError("NIC already exists");
                                                    } else {
                                                        // NIC is unique, proceed with registration
                                                        showError("NIC is unique, proceed with registration");
                                                        createAuthAccountNotVerified();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Exception e) {
                                                    // Handle the error
                                                }
                                            });
                                        } else {
                                            authManager.isEmailVerifiedorNot(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {}
                                                    },
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            sendVerificationLink();
                                                        }
                                                    });
                                        }
                                    }
                                    @Override
                                    public void onError(Exception exception) {

                                    }
                                });

                        } else {
                            showError("Fill all fields");
                        }

                    }
                },
                "Close",  // Negative button text
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle negative button click (e.g., cancel action)
                        dialog.cancel();
                    }
                }
        );
    }

    private void checkEmailVerified(){
        authManager.isEmailVerifiedorNot(
                new Runnable() {
                    @Override
                    public void run() {
                        readData.checkNicExists("users", "nic", strNic, new ReadData.FirestoreNicCallback() {
                            @Override
                            public void onCallback(boolean exists) {
                                if (exists) {
                                    // NIC already exists, show error or handle it
                                    authManager.loginUser(AuthenticateActivity.this, strEmail, strPwd,
                                            // Success callback
                                            () -> {
                                                // Success: Navigate to the main activity or client view
                                                LocalDataManager.saveDataLocal(context, "userEmail", strEmail);
                                                LocalDataManager.saveDataLocal(context, "userPassword", strPwd);
                                                LocalDataManager.saveDataLocal(context, "userNic", strNic);
                                                Toast.makeText(AuthenticateActivity.this, "Great! You are verified", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(AuthenticateActivity.this, ChooseUserActivity.class);
                                                startActivity(intent);
                                            },
                                            // Failure callback
                                            () -> {
                                                // Handle failure case, e.g., show an error dialog
                                                showError("Suspicious user behavior");
                                            }
                                    );
                                } else {
                                    // NIC is unique, proceed with registration
                                    showError("Suspicious user behavior");
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                // Handle the error
                            }
                        });
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Please verify your email first", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createAuthAccountNotVerified(){
        strEmail = email.getText().toString().trim();

        if (!strEmail.isEmpty()) {
            authManager.checkIfEmailExists(strEmail, new AuthManager.EmailCheckCallback() {
                @Override
                public void onEmailCheckComplete(boolean exists) {
                    if (!exists) {
                        authManager.createAuthAccount(AuthenticateActivity.this, strEmail, strPwd,
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        // Success action: Navigate to another screen or show success message
                                        loginUserAuth(strEmail, strPwd);
                                    }
                                },
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        // Failure action: Show an error message or handle the failure
                                    }
                                }
                        );
                    }
                }

                @Override
                public void onError(Exception exception) {

                }
            });
        } else {
            // Handle empty email/password fields
            showError("Please fill in all fields");
        }
    }

    private void loginUserAuth(String strEmail, String strPwd){
        // Handle login and verification
        authManager.loginUser(this, strEmail, strPwd,
                // Success callback
                () -> {
                    // Do something on success, e.g., navigate to next activity
                    showError("User logged");
                },
                // Failure callback
                () -> {
                    // Handle failure case, e.g., show an error dialog
                    showError("login failed");
                }
        );
    }

    private void sendVerificationLink(){
            authManager.sendVerificationLink(this,
                    new Runnable() {
                        @Override
                        public void run() {
                            // Handle success: e.g., navigate to a different screen
                        }
                    },
                    new Runnable() {
                        @Override
                        public void run() {
                            // Handle failure: e.g., show error message or retry option
                        }
                    });
    }

    // Call the checkNicExists method
    private void checkNicAvailable(){
        strNic = nic.getText().toString().trim();
        readData.checkNicExists("users", "nic", strNic, new ReadData.FirestoreNicCallback() {
            @Override
            public void onCallback(boolean exists) {
                if (exists) {
                    // NIC already exists, show error or handle it
                    showError("NIC already exists");
                } else {
                    // NIC is unique, proceed with registration
                    showError("NIC is unique, proceed with registration");
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Handle the error
            }
        });
    }

    private void leaveUser(){
        dialogUtils.showAlertDialog(
                this,  // context, typically 'this' in an Activity or 'getContext()' in a Fragment
                "Exit from verification",  // Title
                "Your information will lost",  // Message
                "OK",  // Positive button text
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle positive button click (e.g., delete item)
                        authManager.deleteUser(AuthenticateActivity.this);
                    }
                },
                null,null,
                "Cancel",  // Negative button text
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle negative button click (e.g., cancel action)
                        dialog.cancel();
                    }
                }
        );
    }

    public boolean validateInput() {
        boolean isValid = true;

        strEmail = email.getText().toString().trim();
        strNic = nic.getText().toString().trim();

        // Email validation
        if (strEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            email.setError(strEmail.isEmpty() ? "Email is required" : "Invalid email address");
            isValid = false;
        }

        // Validate NIC number
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

        return isValid;
    }

    private class GenericTextWatcher implements TextWatcher {

        private EditText editText;

        public GenericTextWatcher(EditText editText) {
            this.editText = editText;
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            editText.setError(null);
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            generatePassword();
            validateInput();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            validateInput();
//            if (editText == email) {
//                String strEmail = email.getText().toString().trim();
//
//                authManager.checkIfEmailExists(strEmail, new AuthManager.EmailCheckCallback() {
//                    @Override
//                    public void onEmailCheckComplete(boolean exists) {
//                        if (exists) {
//                            email.setError("Already used email");
//                            showError("Email is already registered. Please use a different one");
//                        } else {
//                            editText.setError(null);
//                            showError("Email is available");
//                        }
//                    }
//
//                    @Override
//                    public void onError(Exception exception) {
//
//                    }
//                });
//            } else if (editText == nic){
//                checkNicAvailable();
//            }
        }
    }

    // Method to generate password based on email and NIC
    private void generatePassword() {
        strEmail = email.getText().toString().trim();
        strNic = nic.getText().toString().trim();

        // Validate inputs
        if (strEmail.contains("@") && nic.length() >= 4) {
            // Split the email into username and domain parts
            String[] emailParts = strEmail.split("@");

            // If username part is less than 4 characters, clear the generated password
            if (emailParts.length > 1 && emailParts[0].length() >= 4) {
                // Extract first 4 letters of the username
                String firstPart = emailParts[0].substring(0, 4);
                String firstLetterCapitalized = firstPart.substring(0, 1).toUpperCase() + firstPart.substring(1);

                // Extract first 4 digits of NIC
                String nicPart = strNic.substring(0, 4);

                // Combine for final password
                strPwd = firstLetterCapitalized + "@" + nicPart;

                generatedPwd.setText(strPwd);
            } else {
                // Clear the generated password if conditions are not met
                generatedPwd.setText("");
            }
        } else {
            // Clear the generated password if email is invalid
            generatedPwd.setText("");
        }
    }

    // Method to copy password to clipboard
    private void copyToClipboard(String password) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Suggested Password", password);
        clipboard.setPrimaryClip(clip);

        showError("Generated password copied to clipboard");
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}