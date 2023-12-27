package fr.iutrodez.jarspeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jarspeed.R;

import org.json.JSONException;
import org.json.JSONObject;

import fr.iutrodez.jarspeed.api.ApiConfig;
import fr.iutrodez.jarspeed.api.RequestHelper;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);

        emailEditText.setHintTextColor(ContextCompat.getColor(this, R.color.gray));
        passwordEditText.setHintTextColor(ContextCompat.getColor(this, R.color.gray));
    }

    // Méthode pour la gestion du clic sur le bouton d'inscription
    public void onRegisterClick(View view) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    public void onLoginClick(View view) {
        attemptLogin();
    }

    private void attemptLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (validateInputs(email, password)) {
            sendLoginRequest(email, password);
        }
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Les champs ne peuvent pas être vides", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void sendLoginRequest(String email, String password) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("email", email);
            postData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ApiConfig.LOGIN_URL, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        handleLoginSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Erreur lors de la connexion", Toast.LENGTH_LONG).show();
                    }
                });

        RequestHelper.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void handleLoginSuccess(JSONObject response) {
        try {
            // Extraire le JWT de la réponse
            String token = response.getString("token"); // Assurez-vous que la clé "token" correspond à la clé utilisée dans votre réponse JSON

            // Stocker le JWT dans SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.saved_token), token);
            editor.apply();

            // Naviguer vers la MapActivity
            navigateToMapActivity();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Erreur lors de l'extraction du token", Toast.LENGTH_SHORT).show();
        }
    }


    private void navigateToMapActivity() {
        Intent mapIntent = new Intent(this, MapActivity.class);
        startActivity(mapIntent);
    }
}
