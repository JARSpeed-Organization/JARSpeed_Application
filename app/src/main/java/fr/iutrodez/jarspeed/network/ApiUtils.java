package fr.iutrodez.jarspeed.network;

import android.content.Context;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import fr.iutrodez.jarspeed.model.UserRegistrationRequest;
import fr.iutrodez.jarspeed.utils.SharedPreferencesManager;

public class ApiUtils {

    // Méthode pour envoyer une requête de connexion à l'API.
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

    // Méthode pour charger le profil utilisateur
    public static void loadUserProfile(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(Request.Method.GET, ApiConstants.USER_INFO_URL, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getAuthHeaders(context);
            }
        };
        Volley.newRequestQueue(context).add(request);
    }

    // Méthode utilitaire pour obtenir les headers d'authentification
    private static Map<String, String> getAuthHeaders(Context context) {
        Map<String, String> headers = new HashMap<>();
        String token = SharedPreferencesManager.getAuthToken(context);
        if (token != null && !token.isEmpty()) {
            headers.put("Authorization", "Bearer " + token);
        }
        return headers;
    }

    // Méthode pour inscrire un nouvel utilisateur
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
}

