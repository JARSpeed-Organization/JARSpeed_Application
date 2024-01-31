package fr.iutrodez.jarspeed.ui;

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

import fr.iutrodez.jarspeed.network.ApiUtils;
import fr.iutrodez.jarspeed.utils.SharedPreferencesManager;

/**
 * The type Main activity.
 */
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

    /**
     * On register click.
     *
     * @param view the view
     */
// Méthode pour la gestion du clic sur le bouton d'inscription
    public void onRegisterClick(View view) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    /**
     * On login click.
     *
     * @param view the view
     */
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
     *
     * @param email    L'email de l'utilisateur.
     * @param password Le mot de passe de l'utilisateur.
     */
    private void loginUser(String email, String password) {
        ApiUtils.loginUser(this, email, password, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String token = jsonResponse.getString("token");
                    String refreshToken = jsonResponse.getString("refreshToken");

                    SharedPreferencesManager.saveAuthToken(MainActivity.this, token);

                    goToMapActivity();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
    }

    /**
     * Navigue vers l'activité MapActivity.
     */
    private void goToMapActivity() {
        Intent mapIntent = new Intent(this, MapActivity.class);
        startActivity(mapIntent);
    }
}
