package com.example.skillshub.model;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.skillshub.R;

public class CustomDialog2 extends DialogFragment {

    private EditText name, phoneNumber, addressLine1, addressLine2,description;

    private Button saveButton;
    private CustomDialogInterface customDialogInterface;

    private String initialName, initialPhoneNumber, initialAddressLine1, initialAddressLine2,initialDescription;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_edit_custom_dialog2, null);

        // Initialize dialog fields
        name = view.findViewById(R.id.name);
        phoneNumber = view.findViewById(R.id.phoneNumber);
        addressLine1 = view.findViewById(R.id.addressLine1);
        addressLine2 = view.findViewById(R.id.addressLine2);
        description = view.findViewById(R.id.description);

        // Set initial values to the fields
        name.setText(initialName);
        phoneNumber.setText(initialPhoneNumber);
        addressLine1.setText(initialAddressLine1);
        addressLine2.setText(initialAddressLine2);
        description.setText(initialDescription);

        builder.setView(view)
                .setTitle("Edit User Details")
                .setNegativeButton("Cancel", (dialog, which) -> dismiss())
                .setPositiveButton("Save", null);

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Set the onClick listener for the positive button after creating the dialog
        dialog.setOnShowListener(dialogInterface -> {
            AlertDialog alertDialog = (AlertDialog) dialogInterface;
            Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                String updatedName = name.getText().toString();
                String updatedPhone = phoneNumber.getText().toString();
                String updatedAddress1 = addressLine1.getText().toString();
                String updatedAddress2 = addressLine2.getText().toString();

                // Pass the updated data back to the activity
                customDialogInterface.applyTexts(updatedName, updatedPhone, updatedAddress1, updatedAddress2);
                dismiss(); // Close the dialog
            });
        });

        return dialog;
    }

    public void setCustomDialogInterface(CustomDialogInterface customDialogInterface) {
        this.customDialogInterface = customDialogInterface;
    }

    public void setInitialValues(String name, String phoneNumber, String addressLine1, String addressLine2, String description) {
        this.initialName = name;
        this.initialPhoneNumber = phoneNumber;
        this.initialAddressLine1 = addressLine1;
        this.initialAddressLine2 = addressLine2;
        this.initialDescription = description;
    }

    public interface CustomDialogInterface {
        void applyTexts(String name, String phoneNumber, String addressLine1, String addressLine2);
    }
}