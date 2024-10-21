package com.example.skillshub.model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.skillshub.R;

public class CustomDialog extends AppCompatDialogFragment {

    CustomDialogInterface customDialogInterface;
    TextView name;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder bilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_edit_custom_dialog,null);
        bilder .setView(view)
                .setTitle("Edit")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterfaece, int i) {
                        String newName = name.getText().toString();
                        customDialogInterface.applyTexts(newName);
                    }
                });



        name = view.findViewById(R.id.name);

        return bilder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            customDialogInterface = (CustomDialogInterface) context;
    }

    public interface CustomDialogInterface {
        void applyTexts(String name);
    }
}