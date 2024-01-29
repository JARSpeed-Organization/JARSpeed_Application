package fr.iutrodez.jarspeed.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String SHARED_PREFS_NAME = "api_shared_prefs";

    public static void saveAuthToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString("token", token).apply();
    }

    public static String getAuthToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString("token", null);
    }

    // ... autres méthodes pour gérer SharedPreferences
}
