package fr.iutrodez.jarspeed.ui;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import fr.iutrodez.jarspeed.model.route.Route;
import fr.iutrodez.jarspeed.model.user.Login;
import fr.iutrodez.jarspeed.network.ApiUtils;
import fr.iutrodez.jarspeed.utils.SharedPreferencesManager;
import fr.iutrodez.jarspeed.utils.ValidationUtils;
import fr.iutrodez.jarspeed.utils.PasswordEncryptor;

/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        // Applying animation to the logo
        ImageView logoImageView = findViewById(R.id.logo);
        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        logoImageView.startAnimation(logoAnimation);

        // Creation of a callback for pressing the back button
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        };
        // Get the OnBackPressedDispatcher and add the callback
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * Handles the user's click on the register button, navigating to the RegisterActivity.
     *
     * @param view The view that was clicked.
     */
    public void onRegisterClick(View view) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    /**
     * Handles the user's click on the login button, performing field validation and attempting
     * to login the user with the provided credentials.
     *
     * @param view The view that was clicked.
     */
    public void onLoginClick(View view) {

        if (!isNetworkConnected()) {
            // Afficher un message d'erreur si pas de connexion internet
            Toasty.error(this, "Aucune connexion Internet. Veuillez vous connecter et réessayer.", Toast.LENGTH_LONG, true).show();
            return; // Arrête l'exécution de la méthode ici
        }
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);
        String email = emailEditText.getText().toString();
        String password = PasswordEncryptor.encryptPassword(passwordEditText.getText().toString());

        // Reset field borders on each attempt
        ValidationUtils.resetFieldBorders(this, emailEditText, passwordEditText);

        // Check for empty fields
        if (email.trim().isEmpty() || password.trim().isEmpty()) {
            ValidationUtils.setErrorBorder(this, emailEditText);
            ValidationUtils.setErrorBorder(this, passwordEditText);
            Toasty.error(this, "Le champ ne doit pas être vide.", Toast.LENGTH_SHORT, true).show();
        } else {
            loginUser(email, password);
        }
    }

    /**
     * Attempts to login the user with the given credentials by sending a request to the API.
     *
     * @param email    The user's email.
     * @param password The user's password.
     */
    private void loginUser(String email, String password) {
        ApiUtils.loginUser(this, email, password, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    Login userConnection = objectMapper.readValue(response, Login.class);
                    SharedPreferencesManager.saveAuthUserData(MainActivity.this, userConnection);

                    goToMapActivity();
                } catch (JsonMappingException pE) {
                    throw new RuntimeException(pE);
                } catch (JsonProcessingException pE) {
                    throw new RuntimeException(pE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setErrorFields();
                Toasty.error(MainActivity.this, "Identifiants invalides.", Toast.LENGTH_SHORT, true).show();
                Log.e("login", error.toString());
            }
        });
    }

    /**
     * Sets error borders on email and password fields to indicate an issue with the login attempt.
     */
    private void setErrorFields() {
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);
        ValidationUtils.setErrorBorder(this, emailEditText);
        ValidationUtils.setErrorBorder(this, passwordEditText);
    }

    /**
     * Navigates to the MapActivity screen.
     */
    private void goToMapActivity() {
        Intent mapIntent = new Intent(this, MapActivity.class);
        startActivity(mapIntent);
        Toasty.success(MainActivity.this, "Connexion réussie", Toast.LENGTH_SHORT, true).show();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
