package fr.iutrodez.jarspeed.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * The type Shared preferences manager.
 */
public class SharedPreferencesManager {

    /**
     * The constant SHARED_PREFS_NAME.
     */
    private static final String SHARED_PREFS_NAME = "api_shared_prefs";

    /**
     * Save auth token.
     *
     * @param context the context
     * @param token   the token
     */
    public static void saveAuthToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString("token", token).apply();
    }

    /**
     * Gets auth token.
     *
     * @param context the context
     * @return the auth token
     */
    public static String getAuthToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString("token", null);
    }

    // ... autres méthodes pour gérer SharedPreferences
}
