package com.example.skillshub.utils;

import android.text.Editable;
import android.text.TextWatcher; // Import the Android TextWatcher interface
import android.widget.EditText;

public class GenericTextWatcher implements TextWatcher { // Renamed the class
    private final EditText editText;
    private final int minLength; // Minimum length for validation
    private final String errorMessage; // Error message for validation
    private final EditText confirmPasswordEditText;
    private final String passwordMismatchError;

    public GenericTextWatcher(EditText editText, int minLength, String errorMessage, EditText confirmPasswordEditText, String passwordMismatchError) {
        this.editText = editText;
        this.minLength = minLength;
        this.errorMessage = errorMessage;
        this.confirmPasswordEditText = confirmPasswordEditText;
        this.passwordMismatchError = passwordMismatchError;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        // Clear any previous error
        editText.setError(null);
        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        // Validate the input length
        if (charSequence.length() < minLength) {
            editText.setError(errorMessage);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        // Call your validation method if needed
        validateInput();
    }

    // Replace this method with your actual validation logic
    private void validateInput() {
        // Check if both password and confirm password fields are available
        if (confirmPasswordEditText != null) {
            String password = editText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                confirmPasswordEditText.setError(passwordMismatchError);
            }
        }
    }
}
