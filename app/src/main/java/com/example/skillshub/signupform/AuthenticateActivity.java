package com.example.skillshub.signupform;

import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AppCompatActivity;

import com.example.skillshub.R;
import com.example.skillshub.firebaseModel.AuthManager;
import com.example.skillshub.firebaseModel.ReadData;
import com.example.skillshub.utils.DialogUtils;

public class AuthenticateActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private EditText repassword;
    private EditText nic;
    private TextView generatedPwd;
    private TextView copyPwdButton;
    private Button authCheck;
    private Button authLeave;

    private String strEmail;
    private String strPwd;
    private String strPwdConfirm;
    private String strNic;
    private AuthManager authManager;
    private DialogUtils dialogUtils;
    private ReadData readData;

    public static final String SHARED_PREFS = "userPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authenticate);

        dialogUtils = new DialogUtils();
        authManager = new AuthManager();
        readData = new ReadData();
        email = findViewById(R.id.auth_email);
        password = findViewById(R.id.auth_password);
        repassword = findViewById(R.id.auth_password_confirm);
        nic = findViewById(R.id.auth_nic);
        generatedPwd = findViewById(R.id.generated_password);
        copyPwdButton = findViewById(R.id.copy_button);
        copyPwdButton.setPaintFlags(copyPwdButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        authCheck = findViewById(R.id.auth_check_button);
        authLeave = findViewById(R.id.auth_leave_button);

        email.addTextChangedListener(new GenericTextWatcher(email));
        password.addTextChangedListener(new GenericTextWatcher(password));
        repassword.addTextChangedListener(new GenericTextWatcher(repassword));
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

        // Copy button functionality
        copyPwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = generatedPwd.getText().toString();
                if (!password.isEmpty()) {
                    copyToClipboard(password);
                } else {
                    showError("Enter valid email and password first");
                }
            }
        });
    }

    private void openDialogBox(){
        strEmail = email.getText().toString().trim();
        strPwd = password.getText().toString().trim();
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
                        if(checkEmailVerified()) {
                            saveDataLocal("userEmail", strEmail);
                            saveDataLocal("userPassword", strPwd);
                            saveDataLocal("userNic", strNic);
                            Toast.makeText(AuthenticateActivity.this, "Great! You are verified", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(AuthenticateActivity.this, ChooseUserActivity.class);
//                            startActivity(intent);
                        }
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
                                        if (!exists && !authManager.isEmailVerified()) {
                                            createAuthAccountNotVerified();
                                        }
                                        else if (exists && !authManager.isEmailVerified()) {
                                            resendVerificationLink();
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

    private boolean checkEmailVerified(){
        if (authManager.getCurrentUser() == null) {
            Toast.makeText(this, "No user is signed in", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if the email is verified
        boolean verified = authManager.isEmailVerified();
        if (verified) {
            Toast.makeText(this, "Email is verified", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(this, "Please verify your email first", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void createAuthAccountNotVerified(){
        strEmail = email.getText().toString().trim();
        strPwd = password.getText().toString().trim();

        if (!strEmail.isEmpty() && !strPwd.isEmpty()) {
            authManager.checkIfEmailExists(strEmail, new AuthManager.EmailCheckCallback() {
                @Override
                public void onEmailCheckComplete(boolean exists) {
                    if (!exists) {
                        authManager.createAuthAccount(AuthenticateActivity.this, strEmail, strPwd,
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        // Success action: Navigate to another screen or show success message
                                        loginUserAuth();
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

    private void loginUserAuth(){
        strEmail = email.getText().toString().trim();
        strPwd = password.getText().toString().trim();
        authManager.loginUser(AuthenticateActivity.this, strEmail, strPwd,
                new Runnable() {
                    @Override
                    public void run() {
                        // Success: Navigate to the main activity or client view
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        // Handle wrong email case (already shown via Toast)
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        // Handle wrong password case (already shown via Toast)
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        // Handle general failure case
                    }
                });
    }

    private void sendVerificationLink(){
            authManager.sendVerificationLink(this,
                    authManager.getCurrentUser(),
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

    private void resendVerificationLink(){
        authManager.checkIfEmailExists(strEmail, new AuthManager.EmailCheckCallback() {
            @Override
            public void onEmailCheckComplete(boolean exists) {
                if (!exists && !authManager.isEmailVerified()) {
                    sendVerificationLink();
                }
            }

            @Override
            public void onError(Exception exception) {

            }
        });
    }

    // Call the checkNicExists method
    private void checkNicAvailable(){
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

    private void saveDataLocal(String KEY, String name) {
        // Get SharedPreferences object
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        // Get the SharedPreferences editor
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Put data in SharedPreferences
        editor.putString(KEY, name);

        // Apply the changes
        editor.apply();
    }

    public boolean validateInput() {
        boolean isValid = true;

        strEmail = email.getText().toString().trim();
        strPwd = password.getText().toString().trim();
        strPwdConfirm = repassword.getText().toString().trim();
        strNic = nic.getText().toString().trim();

        // Email validation
        if (strEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            email.setError(strEmail.isEmpty() ? "Email is required" : "Invalid email address");
            isValid = false;
        }

        // Password validation
        if (strPwd.isEmpty() || strPwd.length() < 6) {
            password.setError(strPwd.isEmpty() ? "Password is required" : "Password must be at least 6 characters");
            isValid = false;
        }

        // Password confirmation validation
        if (strPwdConfirm.isEmpty()) {
            repassword.setError(strPwdConfirm.isEmpty() ? "Re-entering password is required" : "Passwords do not match");
            isValid = false;
        }

        if (!strPwd.equals(strPwdConfirm)) {
            repassword.setError("Passwords do not match");
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
            if (editText == password) {
                if (charSequence.length() < 6) {
                    password.setError("Password too short");
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editText == email) {
                String strEmail = email.getText().toString().trim();

                authManager.checkIfEmailExists(strEmail, new AuthManager.EmailCheckCallback() {
                    @Override
                    public void onEmailCheckComplete(boolean exists) {
                        if (exists) {
                            email.setError("Already used email");
                            showError("Email is already registered. Please use a different one");
                        } else {
                            editText.setError(null);
                            showError("Email is available");
                        }
                    }

                    @Override
                    public void onError(Exception exception) {

                    }
                });
            }
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
                String generatedPass = firstLetterCapitalized + "@" + nicPart;
                generatedPwd.setText(generatedPass);
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

        showError("Suggested password copied to clipboard");
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}