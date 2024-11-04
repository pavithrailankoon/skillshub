package com.example.skillshub.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtils {
    public AlertDialog.Builder builder;
    // Reusable method to create and show an AlertDialog
    public static void showAlertDialog(Context context,
                                       String title,
                                       String message,
                                       String positiveButtonText,
                                       DialogInterface.OnClickListener positiveListener,
                                       String neutralButtonText,
                                       DialogInterface.OnClickListener neutralListener,
                                       String negativeButtonText,
                                       DialogInterface.OnClickListener negativeListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, positiveListener)
                .setNeutralButton(neutralButtonText, neutralListener)
                .setNegativeButton(negativeButtonText, negativeListener);

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

//DialogUtil.showAlertDialog(
//    this,  // context, typically 'this' in an Activity or 'getContext()' in a Fragment
//            "Delete Confirmation",  // Title
//            "Are you sure you want to delete this item?",  // Message
//            "Delete",  // Positive button text
//            new DialogInterface.OnClickListener() {
//    @Override
//    public void onClick(DialogInterface dialog, int which) {
//        // Handle positive button click (e.g., delete item)
//        deleteItem();
//    }
//},
//        "Cancel",  // Neutral button text
//        new DialogInterface.OnClickListener() {
//    @Override
//    public void onClick(DialogInterface dialog, int which) {
//        // Handle neutral button click (e.g., dismiss dialog)
//        dialog.dismiss();
//    }
//},
//        "Close",  // Negative button text
//        new DialogInterface.OnClickListener() {
//    @Override
//    public void onClick(DialogInterface dialog, int which) {
//        // Handle negative button click (e.g., cancel action)
//        dialog.cancel();
//    }
//}
//);