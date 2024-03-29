package fr.iutrodez.jarspeed.network;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.iutrodez.jarspeed.model.gender.Gender;
import fr.iutrodez.jarspeed.model.route.Route;
import fr.iutrodez.jarspeed.model.user.UserRegistrationRequest;
import fr.iutrodez.jarspeed.model.user.UserUpdateRequest;
import fr.iutrodez.jarspeed.utils.SharedPreferencesManager;

/**
 * The type Api utils.
 */
public class ApiUtils {

    /**
     * Login user.
     *
     * @param context       the context
     * @param email         the email
     * @param password      the password
     * @param listener      the listener
     * @param errorListener the error listener
     */
    public static void loginUser(Context context, String email, String password, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(Request.Method.POST, ApiConstants.LOGIN_URL, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        Volley.newRequestQueue(context).add(request);
    }

    /**
     * Gets auth headers.
     *
     * @param context the context
     * @return the auth headers
     */
    private static Map<String, String> getAuthHeaders(Context context) {
        Map<String, String> headers = new HashMap<>();
        String token = SharedPreferencesManager.getAuthToken(context);
        if (token != null && !token.isEmpty()) {
            headers.put("Authorization", "Bearer " + token);
        }
        return headers;
    }

    /**
     * Register user.
     *
     * @param context             the context
     * @param registrationRequest the registration request
     * @param listener            the listener
     * @param errorListener       the error listener
     */
    public static void registerUser(Context context, UserRegistrationRequest registrationRequest, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(Request.Method.POST, ApiConstants.REGISTER_URL, listener, errorListener) {
            @Override
            public byte[] getBody() {
                return new Gson().toJson(registrationRequest).getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        Volley.newRequestQueue(context).add(request);
    }

    /**
     * Update route.
     *
     * @param context       the context
     * @param pRoute        the p route
     * @param listener      the listener
     * @param errorListener the error listener
     */
    public static void updateRoute(Context context, Route pRoute, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        StringRequest request = new StringRequest(StringRequest.Method.PUT, ApiConstants.ROUTE_BASE_URL, listener, errorListener) {
            @Override
            public byte[] getBody() {
                return new Gson().toJson(pRoute).getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String token = SharedPreferencesManager.getAuthToken(context);
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        Volley.newRequestQueue(context).add(request);
    }

    /**
     * Save route.
     *
     * @param context       the context
     * @param pRoute        the p route
     * @param listener      the listener
     * @param errorListener the error listener
     */
    public static void saveRoute(Context context, Route pRoute, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        StringRequest request = new StringRequest(StringRequest.Method.POST, ApiConstants.ROUTE_BASE_URL, listener, errorListener) {
            @Override
            public byte[] getBody() {
                return new Gson().toJson(pRoute).getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String token = SharedPreferencesManager.getAuthToken(context);
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        Volley.newRequestQueue(context).add(request);
    }

    /**
     * Update user.
     *
     * @param context       the context
     * @param updateRequest the update request
     * @param listener      the listener
     * @param errorListener the error listener
     */
    public static void updateUser(Context context, UserUpdateRequest updateRequest, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = ApiConstants.UPDATE_USER_URL;
        StringRequest request = new StringRequest(StringRequest.Method.PUT, url, listener, errorListener) {
            @Override
            public byte[] getBody() {
                return new Gson().toJson(updateRequest).getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String token = SharedPreferencesManager.getAuthToken(context);
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        Volley.newRequestQueue(context).add(request);
    }

    /**
     * Fetch genders.
     *
     * @param context       the context
     * @param listener      the listener
     * @param errorListener the error listener
     */
    public static void fetchGenders(Context context, Response.Listener<List<Gender>> listener, Response.ErrorListener errorListener) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, ApiConstants.GENDERS_URL, null,
                response -> {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Gender>>() {}.getType();
                    List<Gender> genders = gson.fromJson(response.toString(), listType);
                    listener.onResponse(genders);
                }, errorListener);
        Volley.newRequestQueue(context).add(request);
    }

    /**
     * Delete account.
     *
     * @param context       the context
     * @param listener      the listener
     * @param errorListener the error listener
     */
    public static void deleteAccount(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(Request.Method.DELETE, ApiConstants.DELETE_URL,
                listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getAuthHeaders(context);
            }
        };
        Volley.newRequestQueue(context).add(request);
    }

    /**
     * Load user profile.
     *
     * @param context       the context
     * @param listener      the listener
     * @param errorListener the error listener
     */
    public static void loadUserProfile(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = ApiConstants.USER_INFO_URL; // Assurez-vous que cela pointe vers votre endpoint /profile
        StringRequest request = new StringRequest(Request.Method.GET, url, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getAuthHeaders(context);
            }
        };
        Volley.newRequestQueue(context).add(request);
    }

    /**
     * Load all routes.
     *
     * @param context       the context
     * @param listener      the listener
     * @param errorListener the error listener
     */
    public static void loadAllRoutes(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = ApiConstants.ALL_ROUTES_URL;
        StringRequest request = new StringRequest(Request.Method.GET, url, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getAuthHeaders(context);
            }
        };
        Volley.newRequestQueue(context).add(request);
    }

    /**
     * Delete route.
     *
     * @param context       the context
     * @param url           the url
     * @param listener      the listener
     * @param errorListener the error listener
     */
    public static void deleteRoute(Context context, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(Request.Method.DELETE, url, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getAuthHeaders(context);
            }
        };
        Volley.newRequestQueue(context).add(request);
    }



}
