package fr.iutrodez.jarspeed.ui;

import androidx.activity.OnBackPressedCallback;
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

        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);

        emailEditText.setHintTextColor(ContextCompat.getColor(this, R.color.gray));
        passwordEditText.setHintTextColor(ContextCompat.getColor(this, R.color.gray));

        // Application de l'animation au logo
        ImageView logoImageView = findViewById(R.id.logo);
        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        logoImageView.startAnimation(logoAnimation);

        // Création d'un callback pour l'appui sur le bouton de retour
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        };
        // Obtention du OnBackPressedDispatcher et ajout du callback
        getOnBackPressedDispatcher().addCallback(this, callback);
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
        String password = PasswordEncryptor.encryptPassword(passwordEditText.getText().toString());

        // Réinitialisez les bordures des champs à chaque tentative
        ValidationUtils.resetFieldBorders(this, emailEditText, passwordEditText);

        // Vérifiez si les champs sont vides
        if (email.trim().isEmpty() || password.trim().isEmpty()) {
            ValidationUtils.setErrorBorder(this, emailEditText);
            ValidationUtils.setErrorBorder(this, passwordEditText);
            Toasty.error(this, "Le champ ne doit pas être vide.", Toast.LENGTH_SHORT, true).show();
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
