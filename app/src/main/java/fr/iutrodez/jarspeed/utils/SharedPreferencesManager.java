package fr.iutrodez.jarspeed.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import fr.iutrodez.jarspeed.model.gender.Gender;
import fr.iutrodez.jarspeed.model.user.Login;
import fr.iutrodez.jarspeed.model.user.User;

/**
 * The type Shared preferences manager.
 */
public class SharedPreferencesManager {

    /**
     * The constant SHARED_PREFS_NAME.
     */
    private static final String SHARED_PREFS_NAME = "api_shared_prefs";

    /**
     * The constant dateFormat.
     */
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy");

    /**
     * Save auth token.
     *
     * @param context the context
     * @param pLogin  the p login
     */
    public static void saveAuthUserData(Context context, Login pLogin) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString("token", pLogin.getUser().getToken()).apply();
        prefs.edit().putInt("id", pLogin.getUser().getId()).apply();
        prefs.edit().putString("email", pLogin.getUser().getEmail()).apply();
        prefs.edit().putString("lastname", pLogin.getUser().getLastname()).apply();
        prefs.edit().putString("firstname", pLogin.getUser().getFirstname()).apply();
        prefs.edit().putString("birthdate", pLogin.getUser().getBirthdate().toString()).apply();
        prefs.edit().putString("genderLabel", pLogin.getUser().getGender().getLabel()).apply();
        prefs.edit().putInt("genderId", pLogin.getUser().getGender().getId()).apply();
        prefs.edit().putString("weight", pLogin.getUser().getWeight().toString()).apply();
        prefs.edit().putString("password", pLogin.getUser().getPassword()).apply();
    }

    /**
     * Save auth user data.
     *
     * @param context the context
     * @param pUser   the p user
     */
    public static void saveAuthUserData(Context context, User pUser) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString("token", pUser.getToken()).apply();
        prefs.edit().putInt("id", pUser.getId()).apply();
        prefs.edit().putString("email", pUser.getEmail()).apply();
        prefs.edit().putString("lastname", pUser.getLastname()).apply();
        prefs.edit().putString("firstname", pUser.getFirstname()).apply();
        prefs.edit().putString("birthdate", pUser.getBirthdate().toString()).apply();
        prefs.edit().putString("genderLabel", pUser.getGender().getLabel()).apply();
        prefs.edit().putInt("genderId", pUser.getGender().getId()).apply();
        prefs.edit().putString("weight", pUser.getWeight().toString()).apply();
        prefs.edit().putString("password", pUser.getPassword()).apply();
    }

    /**
     * Gets auth user data.
     *
     * @param context the context
     * @return the auth user data
     */
    public static User getAuthUserData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        User user = new User();
        user.setId(prefs.getInt("id", -1));
        user.setEmail(prefs.getString("email", null));
        user.setLastname(prefs.getString("lastname", null));
        user.setFirstname(prefs.getString("firstname", null));
        try {
            Log.e("birthdatePref", prefs.getString("birthdate", null));
            user.setBirthdate(dateFormat.parse(prefs.getString("birthdate", null)));
        } catch (ParseException pE) {
            user.setBirthdate(null);
        }
        Gender gender = new Gender(prefs.getInt("genderId", 3), prefs.getString("genderLabel", null));
        user.setGender(gender);
        user.setWeight(Double.parseDouble(prefs.getString("weight", "0")));
        user.setPassword(prefs.getString("password", ""));
        return user;
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
     * @return weight weight
     */
    public static double getWeight(Context pContext) {
        SharedPreferences prefs = pContext.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return Double.parseDouble(prefs.getString("weight", null));
    }
}
