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
     * @param weight  the weight
     */
    public static void saveAuthTokenAndWeight(Context context, String token, String weight) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString("token", token).apply();
        prefs.edit().putString("weight", weight).apply();
    }

    /**
     * Gets auth token.
     *
     * @param pContext the context
     * @return the auth token
     */
    public static String getAuthToken(Context pContext) {
        SharedPreferences prefs = pContext.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString("token", null);
    }

    /**
     * Gets weight.
     *
     * @param pContext the p context
     * @return weight
     */
    public static double getWeight(Context pContext) {
        SharedPreferences prefs = pContext.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return Double.parseDouble(prefs.getString("weight", null));
    }
}
