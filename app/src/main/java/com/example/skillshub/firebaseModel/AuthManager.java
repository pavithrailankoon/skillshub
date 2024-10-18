package com.example.skillshub.firebaseModel;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AuthManager {
    private FirebaseAuth auth;
    private Context context;
    private static FirebaseUser user;

    public AuthManager() {
        this.auth = FirebaseAuth.getInstance();
        this.user = auth.getCurrentUser();
        this.context = context;
    }

    public FirebaseUser getCurrentUser() {
        return user;
    }

    // Method to check if the user's email is verified
    public static boolean isEmailVerified() {
        // Ensure the FirebaseUser is not null
        if (user != null) {
            // Return the result of whether the email is verified or not
            return user.isEmailVerified();
        }
        return false;
    }

    public void loginUser(Context context, String email, String password, Runnable onSuccess, Runnable onWrongEmail, Runnable onWrongPassword, Runnable onFailure) {
        FirebaseAuth auth = FirebaseAuth.getInstance(); // Get FirebaseAuth instance

        // Check if email and password are not empty
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Email and password are required", Toast.LENGTH_SHORT).show();
            onFailure.run(); // General failure callback for empty inputs
            return;
        }

        // Attempt to log in with email and password
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login successful
                            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show();
                            onSuccess.run(); // Call success callback
                        } else {
                            // Handle login failure by analyzing the exception
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                // The email address is wrong or the account doesn't exist
                                Toast.makeText(context, "The email address is incorrect or not registered", Toast.LENGTH_LONG).show();
                                onWrongEmail.run(); // Call wrong email callback
                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The password is wrong
                                Toast.makeText(context, "Incorrect password", Toast.LENGTH_LONG).show();
                                onWrongPassword.run(); // Call wrong password callback
                            } else {
                                // Handle general failure (e.g., network issue, etc.)
                                Toast.makeText(context, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                onFailure.run(); // Call general failure callback
                            }
                        }
                    }
                });
    }



    public FirebaseUser createAuthAccount(Context context, String email, String password, Runnable onSuccess, Runnable onFailure) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Account created successfully.", Toast.LENGTH_SHORT).show();
                            onSuccess.run(); // Call success callback
                        } else {
                            // Handle the error (e.g., display message)
                            Toast.makeText(context, "Signup Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            onFailure.run(); // Call failure callback
                        }
                    }
                });
        user = auth.getCurrentUser();
        return user;
    }

    public void createAuthAccountWithPhone(Context context, String phoneNumber, Activity activity, PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)        // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS)  // Timeout and unit
                        .setActivity(activity)              // Activity (for callback binding)
                        .setCallbacks(callbacks)            // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    public void sendVerificationLink(Context context, FirebaseUser user, Runnable onSuccess, Runnable onFailure) {
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Verification email sent successfully
                                Toast.makeText(context, "Verification link sent to: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                onSuccess.run(); // Trigger success callback
                            } else {
                                // Failed to send verification email
                                Toast.makeText(context, "Failed to send verification link.", Toast.LENGTH_SHORT).show();
                                onFailure.run(); // Trigger failure callback
                            }
                        }
                    });
        } else {
            Toast.makeText(context, "No logged-in user found or invalid email.", Toast.LENGTH_SHORT).show();
            onFailure.run(); // Trigger failure callback if user is not logged in or email is invalid
        }
    }

            // Method to check if an email is already registered
    public void checkIfEmailExists(String email, EmailCheckCallback callback) {
        auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get sign-in methods associated with this email
                        List<String> signInMethods = task.getResult().getSignInMethods();

                        if (signInMethods != null && !signInMethods.isEmpty()) {
                            // Email is already registered
                            callback.onEmailCheckComplete(true);  // Return true (email exists)
                        } else {
                            // Email is not registered
                            callback.onEmailCheckComplete(false);  // Return false (email does not exist)
                        }
                    } else {
                        // Handle the error case
                        callback.onError(task.getException());
                    }
                });
    }

    // Interface for handling the result asynchronously
    public interface EmailCheckCallback {
        void onEmailCheckComplete(boolean exists);    // Called if the email is not registered
        void onError(Exception e);      // Called in case of error
    }

    // Method to delete the current authenticated user
    public void deleteUser(Context context) {
        user = auth.getCurrentUser();

        if (user != null) {
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // User deletion was successful
                            Toast.makeText(context, "User account deleted successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Failed to delete user, handle the exception
                            Toast.makeText(context, "Failed to delete user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // No user is currently signed in
            Toast.makeText(context, "No user is currently signed in.", Toast.LENGTH_SHORT).show();
        }
    }
}
