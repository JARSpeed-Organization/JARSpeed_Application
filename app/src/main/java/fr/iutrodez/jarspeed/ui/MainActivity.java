package fr.iutrodez.jarspeed.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import fr.iutrodez.jarspeed.utils.ValidationUtils;

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

        // Application de l'animation au logo
        ImageView logoImageView = findViewById(R.id.logo);
        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        logoImageView.startAnimation(logoAnimation);
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

        // Réinitialisez les bordures des champs à chaque tentative
        ValidationUtils.resetFieldBorders(this, emailEditText, passwordEditText);

        // Vérifiez si les champs sont vides
        if (email.trim().isEmpty() || password.trim().isEmpty()) {
            ValidationUtils.setErrorBorder(this, emailEditText);
            ValidationUtils.setErrorBorder(this, passwordEditText);
            Toast.makeText(this, "Le champ ne doit pas être vide.", Toast.LENGTH_SHORT).show();
        } else {
            loginUser(email, password);
        }
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

                    SharedPreferencesManager.saveAuthToken(MainActivity.this, token);

                    goToMapActivity();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setErrorFields();
                Toast.makeText(MainActivity.this, "Identifiants invalides.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setErrorFields() {
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);
        ValidationUtils.setErrorBorder(this, emailEditText);
        ValidationUtils.setErrorBorder(this, passwordEditText);
    }

    /**
     * Navigue vers l'activité MapActivity.
     */
    private void goToMapActivity() {
        Intent mapIntent = new Intent(this, MapActivity.class);
        startActivity(mapIntent);
    }
}
