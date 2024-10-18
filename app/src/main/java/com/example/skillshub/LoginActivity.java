package com.example.skillshub;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

    View view;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordTextView;
    private TextView signUpTextView;
    private AuthManager authManager;
    FirebaseAuth auth;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        forgotPasswordTextView = findViewById(R.id.forgot_password);
        signUpTextView = findViewById(R.id.text3);
        authManager = new AuthManager();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();
                    loginUserAuth();
                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if(!password.isEmpty()){
                        auth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, clientHome.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, "Invalid User.Please Signup", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }else {
                        passwordEditText.setError("Password cannot be empty");
                    }

                } else if (email.isEmpty()) {
                    emailEditText.setError("Email cannot be empty");
                }else {
                    emailEditText.setError("Please enter a valid email");
                }
            }
        });
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Forgot Password clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Sign Up clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, ChooseUserActivity.class);
                startActivity(intent);
            }
        });
        // Handle system back button press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                exit(view);
            }
        });
    }

    // Add exit dialogAlert to completely exit
    public void exit(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirm Exit!");
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher_square);
        alertDialogBuilder.setMessage("Are you sure,You want to exit");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // Exit the app
                finishAffinity();
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

    private void loginUserAuth(){
        String strEmail = emailEditText.getText().toString().trim();
        String strPwd = passwordEditText.getText().toString().trim();
        authManager.loginUser(LoginActivity.this, strEmail, strPwd,
                new Runnable() {
                    @Override
                    public void run() {
                        // Success: Navigate to the main activity or client view
                        Intent intent = new Intent(LoginActivity.this, clientHome.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
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

    public boolean validateInput() {
        boolean isValid = true;

        String strEmail = emailEditText.getText().toString().trim();
        String strPwd = passwordEditText.getText().toString().trim();

        // Email validation
        if (strEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            emailEditText.setError(strEmail.isEmpty() ? "Email is required" : "Invalid email address");
            isValid = false;
        }

        // Password validation
        if (strPwd.isEmpty() || strPwd.length() < 6) {
            passwordEditText.setError(strPwd.isEmpty() ? "Password is required" : "Password must be at least 6 characters");
            isValid = false;
        }

        return isValid;
    }
}