package com.example.skillshub.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalDataManager {
    public static final String SHARED_PREFS = "userPrefs";

    // Save data to SharedPreferences
    public static void saveDataLocal(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // Retrieve data from SharedPreferences
    public static String getDataLocal(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    // Remove data from SharedPreferences
    public static void removeDataLocal(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    // Clear all data from SharedPreferences
    public static void clearAllData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

//    SharedPrefsUtil.saveDataLocal(context, "userEmail", strEmail);
//    String email = SharedPrefsUtil.getDataLocal(context, "userEmail");
//    SharedPrefsUtil.removeDataLocal(context, "userEmail");
//    SharedPrefsUtil.clearAllData(context);
}
