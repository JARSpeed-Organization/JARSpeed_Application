package fr.iutrodez.jarspeed.network;

import android.content.Context;
import android.content.SharedPreferences;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Request api.
 */
public class RequestApi extends StringRequest {
    /**
     * The Context.
     */
    private Context context;

    /**
     * Instantiates a new Request api.
     *
     * @param context       the context
     * @param method        the method
     * @param url           the url
     * @param listener      the listener
     * @param errorListener the error listener
     */
    public RequestApi(Context context, int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.context = context;
    }

    /**
     * Gets headers.
     *
     * @return the headers
     * @throws AuthFailureError the auth failure error
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();

        // Accédez aux préférences partagées pour obtenir le token
        SharedPreferences sharedPreferences = context.getSharedPreferences("api_shared_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        // Ajoutez le token à l'en-tête d'autorisation
        headers.put("Authorization", "Bearer " + token);
        return headers;
    }
}
