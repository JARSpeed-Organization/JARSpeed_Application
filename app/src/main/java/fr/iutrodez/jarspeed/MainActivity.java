package fr.iutrodez.jarspeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jarspeed.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);

        emailEditText.setHintTextColor(ContextCompat.getColor(this, R.color.gray));
        passwordEditText.setHintTextColor(ContextCompat.getColor(this, R.color.gray));
    }

    // Méthode pour la gestion du clic sur le bouton d'inscription
    public void onRegisterClick(View view) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    // Méthode pour la gestion du clic sur le bouton de connexion
    public void onLoginClick(View view) {
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        loginUser(email, password);
    }

    /**
     * Méthode pour envoyer une requête de connexion à l'API.
     * @param email L'email de l'utilisateur.
     * @param password Le mot de passe de l'utilisateur.
     */
    private void loginUser(String email, String password) {
        // URL de l'API pour la connexion.
        String url = "http://10.0.2.2:8080/users/login";

        // Création d'une file d'attente pour les requêtes Volley.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Création d'une requête POST avec Volley.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("LoginResponse", response);
                        try {
                            // Analyser la réponse JSON pour obtenir les tokens.
                            JSONObject jsonResponse = new JSONObject(response);
                            String token = jsonResponse.getString("token");
                            String refreshToken = jsonResponse.getString("refreshToken");

                            // Sauvegarde des tokens dans SharedPreferences.
                            saveAuthToken(token, refreshToken);

                            // Navigation vers l'activité suivante après la connexion.
                            goToMapActivity();
                        } catch (JSONException e) {
                            System.out.println("Erreur lors de la récupération du token");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log des erreurs de la requête.
                        Log.e("LoginError", "Error: " + error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Paramètres à envoyer avec la requête POST.
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        // Ajouter la requête à la file d'attente Volley pour exécution.
        queue.add(stringRequest);
    }

    /**
     * Sauvegarde le token d'accès et le refresh token dans SharedPreferences.
     * @param token Le token d'accès.
     * @param refreshToken Le refresh token.
     */
    private void saveAuthToken(String token, String refreshToken) {
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.putString("refreshToken", refreshToken);
        editor.apply();
    }

    /**
     * Navigue vers l'activité MapActivity.
     */
    private void goToMapActivity() {
        Intent mapIntent = new Intent(this, MapActivity.class);
        startActivity(mapIntent);
    }
}
