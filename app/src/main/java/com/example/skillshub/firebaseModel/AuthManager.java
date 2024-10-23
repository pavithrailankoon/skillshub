package com.example.skillshub.firebaseModel;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
    private FirebaseUser user;

    public AuthManager() {
        this.auth = FirebaseAuth.getInstance();
        this.user = auth.getCurrentUser();
        this.context = context;
    }

    public FirebaseUser getCurrentLoginUser(){
        if (user != null) {
            return user;
        } else {
            // Optionally handle the case when no user is logged in
            Log.e("FirebaseAuth", "No user is currently logged in.");
            return null; // or throw an exception based on your requirements
        }
    }

    // Method to check if the user's email is verified
    public void isEmailVerifiedorNot(Runnable onVerified, Runnable onNotVerified) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Reload the user's data
            currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Check if the email is verified after reloading
                        if (currentUser.isEmailVerified()) {
                            onVerified.run(); // Trigger success callback
                        } else {
                            onNotVerified.run(); // Trigger failure callback
                        }
                    } else {
                        // Handle failure of reloading (e.g., network issues)
                        onNotVerified.run();
                    }
                }
            });
        } else {
            // Handle case where user is null
            onNotVerified.run();
        }
    }

    public void loginUserActivity(Context context, String email, String password, Runnable onSuccess, Runnable onWrongEmail, Runnable onWrongPassword, Runnable onFailure) {
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
                            onSuccess.run(); // Call success callback
                        } else {
                            // Handle login failure by analyzing the exception
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                // The email address is wrong or the account doesn't exist
                                onWrongEmail.run(); // Call wrong email callback
                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The password is wrong
                                onWrongPassword.run(); // Call wrong password callback
                            } else {
                                // Handle general failure (e.g., network issue, etc.)
                                onFailure.run(); // Call general failure callback
                            }
                        }
                    }
                });
    }



    public void createAuthAccount(Context context, String email, String password, Runnable onSuccess, Runnable onFailure) {
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

    // Method to log in the user
    public void loginUser(Context context, String email, String password, Runnable onSuccess, Runnable onFailure) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login successful, check for email verification
                            FirebaseUser currentUser = auth.getCurrentUser();
                            if (currentUser != null && !currentUser.isEmailVerified()) {
                                // Email is not verified, send the verification link
                                sendVerificationLink(context, onSuccess, onFailure);
                            } else {
                                // Email is already verified or there's no current user
                                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show();
                                onSuccess.run(); // Trigger success callback
                            }
                        } else {
                            // Login failed
                            Toast.makeText(context, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            onFailure.run(); // Trigger failure callback
                        }
                    }
                });
    }

    // Existing method to send email verification link
    public void sendVerificationLink(Context context, Runnable onSuccess, Runnable onFailure) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null && !currentUser.isEmailVerified()) {
            currentUser.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Verification email sent successfully
                                Toast.makeText(context, "Verification link sent to: " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                                onSuccess.run(); // Trigger success callback
                            } else {
                                // Failed to send verification email
                                Toast.makeText(context, "Failed to send verification link.", Toast.LENGTH_SHORT).show();
                                onFailure.run(); // Trigger failure callback
                                if (task.getException() != null) {
                                    Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        } else if (currentUser != null && currentUser.isEmailVerified()) {
            Toast.makeText(context, "Email already verified", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "No logged-in user found.", Toast.LENGTH_SHORT).show();
            onFailure.run(); // Trigger failure callback if no user
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
                            //Toast.makeText(context, "User account deleted successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Failed to delete user, handle the exception
                            //Toast.makeText(context, "Failed to delete user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // No user is currently signed in
            Toast.makeText(context, "No user is currently signed in.", Toast.LENGTH_SHORT).show();
        }
    }
}
