package com.example.skillshub;

import android.annotation.SuppressLint;
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
import androidx.appcompat.app.AppCompatActivity;

import com.example.skillshub.firebaseModel.AuthManager;
import com.example.skillshub.signupform.AuthenticateActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private CheckBox rememberMeCheckBox;
    private Button loginButton;
    private TextView forgotPasswordTextView;
    private TextView signUpTextView;
    private AuthManager authManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        rememberMeCheckBox = findViewById(R.id.remember_me);
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