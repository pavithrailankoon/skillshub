package com.example.skillshub;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skillshub.firebaseModel.AuthManager;
import com.example.skillshub.signupform.AuthenticateActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordTextView;
    private TextView signUpTextView;
    private AuthManager authManager;
    FirebaseAuth auth;
    ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        forgotPasswordTextView = findViewById(R.id.forgot_password);
        signUpTextView = findViewById(R.id.text3);
        authManager = new AuthManager();

        emailEditText.addTextChangedListener(new LoginActivity.GenericTextWatcher(emailEditText));
        passwordEditText.addTextChangedListener(new LoginActivity.GenericTextWatcher(passwordEditText));

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (validateInput(email, password)) {
                    loginUserAuth(email, password);
                }
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AuthenticateActivity.class);
                startActivity(intent);
            }
        });

        // Handle system back button press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                exit();
            }
        });
    }

    // Add exit dialog to completely exit
    public void exit() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirm Exit!");
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher_square);
        alertDialogBuilder.setMessage("Are you sure you want to exit?");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finishAffinity();  // Close all activities
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    // Method to log in the user with authentication
    private void loginUserAuth(String email, String password) {
        loginButton.setEnabled(false);
        progressDialog.setTitle("Please Wait..");
        progressDialog.setMessage("logging...");
        progressDialog.show();
        authManager.loginUserActivity(LoginActivity.this, email, password,
                new Runnable() {
                    @Override
                    public void run() {
                        // Success: Navigate to the main activity or client view
                        Intent intent = new Intent(LoginActivity.this, clientHome.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        progressDialog.cancel();
                        finish();
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        // Handle wrong email case
                        emailEditText.setError("Invalid email address");
                        progressDialog.cancel();
                        loginButton.setEnabled(true);
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        // Handle wrong password case
                        passwordEditText.setError("Incorrect password");
                        progressDialog.cancel();
                        loginButton.setEnabled(true);
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        // Handle general failure case
                        Toast.makeText(LoginActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                        loginButton.setEnabled(true);
                    }
                });
    }

    // Validate email and password inputs
    private boolean validateInput(String email, String password) {
        boolean isValid = true;

        // Email validation
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(email.isEmpty() ? "Email is required" : "Invalid email address");
            isValid = false;
        }

        // Password validation
        if (password.isEmpty() || password.length() < 6) {
            passwordEditText.setError(password.isEmpty() ? "Password is required" : "Password must be at least 6 characters");
            isValid = false;
        }

        return isValid;
    }

    public boolean validateInput() {
        boolean isValid = true;

        String strEmail = emailEditText.getText().toString().trim();
        String strPwd = passwordEditText.getText().toString().trim();

        // Email validation
        if (strEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            emailEditText.setError(strEmail.isEmpty() ? "Email is required" : "Invalid email address");
            isValid = false;
        }

        if (strPwd.isEmpty() || strPwd.length()>=6){
            passwordEditText.setError(strPwd.isEmpty() ? "Password is required" : "Short password");
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
            validateInput();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            validateInput();
        }
    }
}