package fr.iutrodez.jarspeed.ui;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.jarspeed.R;

import java.nio.charset.StandardCharsets;

import es.dmoral.toasty.Toasty;
import fr.iutrodez.jarspeed.model.user.UserRegistrationRequest;
import fr.iutrodez.jarspeed.network.ApiUtils;
import fr.iutrodez.jarspeed.utils.PasswordEncryptor;
import fr.iutrodez.jarspeed.utils.ValidationUtils;

/**
 * The type Register activity.
 */
public class RegisterActivity extends AppCompatActivity {

    /**
     * The Name edit text.
     */
    private EditText nameEditText;
    /**
     * The Firstname edit text.
     */
    private EditText firstnameEditText;
    /**
     * The Email edit text.
     */
    private EditText emailEditText;
    /**
     * The Password edit text.
     */
    private EditText passwordEditText;
    /**
     * The Password confirmation edit text.
     */
    private EditText passwordConfirmationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        // Initialize EditText fields
        nameEditText = findViewById(R.id.name);
        firstnameEditText = findViewById(R.id.firstname);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        passwordConfirmationEditText = findViewById(R.id.password_confirmation);

        setHintColors(nameEditText, firstnameEditText, emailEditText, passwordEditText, passwordConfirmationEditText);
        // Creation of a callback for pressing the back button.
        // disable back button.
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        };
        // Get the OnBackPressedDispatcher and add the callback
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * On login click.
     *
     * @param view the view
     */
    public void onLoginClick(View view) {
        // Start login screen
        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);
    }

    /**
     * On register click.
     *
     * @param view the view
     */
    public void onRegisterClick(View view) {
        if (validateFields()) {
            String name = nameEditText.getText().toString();
            String firstname = firstnameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = PasswordEncryptor.encryptPassword(passwordEditText.getText().toString());

            UserRegistrationRequest registrationRequest = new UserRegistrationRequest(name, firstname, email, password);

            ApiUtils.registerUser(this, registrationRequest, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toasty.success(RegisterActivity.this, "Inscription réussie", Toast.LENGTH_SHORT, true).show();
                    goToLoginActivity();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMsg = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        Toasty.error(RegisterActivity.this, "Erreur d'inscription : " + errorMsg, Toast.LENGTH_SHORT, true).show();
                    } else {
                        Toasty.error(RegisterActivity.this, "Erreur de connexion " , Toast.LENGTH_SHORT, true).show();
                    }
                }
            });
        }
    }

    /**
     * Go to login activity.
     */
    private void goToLoginActivity() {
        Intent loginIntent = new Intent(this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
        finish();
    }

    /**
     * Validate fields boolean.
     *
     * @return the boolean
     */
    private boolean validateFields() {
        boolean isValid = true;

        // Reset all edits text fields borders.
        ValidationUtils.resetFieldBorders(this, nameEditText, firstnameEditText, emailEditText, passwordEditText, passwordConfirmationEditText);

        // Check not empty field
        if (ValidationUtils.isEmpty(nameEditText)) {
            ValidationUtils.setErrorBorder(this, nameEditText);
            isValid = false;
        }

        if (ValidationUtils.isEmpty(firstnameEditText)) {
            ValidationUtils.setErrorBorder(this, firstnameEditText);
            isValid = false;
        }

        boolean emailInvalid = ValidationUtils.isEmpty(emailEditText) || !ValidationUtils.isValidEmail(emailEditText.getText().toString());
        if (emailInvalid) {
            ValidationUtils.setErrorBorder(this, emailEditText);
            isValid = false;
        }

        if (ValidationUtils.isEmpty(passwordEditText)) {
            ValidationUtils.setErrorBorder(this, passwordEditText);
            isValid = false;
        }

        if (ValidationUtils.isEmpty(passwordConfirmationEditText)) {
            ValidationUtils.setErrorBorder(this, passwordConfirmationEditText);
            isValid = false;
        }

        // If email is the only invalid field
        if (emailInvalid && !ValidationUtils.isEmpty(nameEditText) && !ValidationUtils.isEmpty(firstnameEditText) && !ValidationUtils.isEmpty(passwordEditText) && !ValidationUtils.isEmpty(passwordConfirmationEditText)) {
            Toasty.warning(this, "Format email non respecté", Toast.LENGTH_SHORT, true).show();
            return false;
        }

        // If other fields are also invalid
        if (!isValid) {
            Toasty.warning(this, "Veuillez remplir tous les champs pour vous inscrire", Toast.LENGTH_SHORT, true).show();
            return false;
        }
        // Check Strength password
        if (!ValidationUtils.isValidPassword(passwordEditText.getText().toString())) {
            ValidationUtils.setErrorBorder(this, passwordEditText);
            Toasty.warning(this, "Le mot de passe doit contenir au moins 8 caractères, dont une majuscule et un caractère spécial", Toast.LENGTH_SHORT, true).show();
            isValid = false;
        }
        // check passwords
        if (!passwordEditText.getText().toString().equals(passwordConfirmationEditText.getText().toString())) {
            ValidationUtils.setErrorBorder(this, passwordEditText);
            ValidationUtils.setErrorBorder(this, passwordConfirmationEditText);
            Toasty.warning(this, "Veuillez saisir deux mots de passe identiques", Toast.LENGTH_SHORT, true).show();

            return false;
        }

        return isValid;
    }

    /**
     * Sets hint colors.
     *
     * @param editsText the edits text
     */
    private void setHintColors(EditText... editsText) {
        for (EditText editText : editsText) {
            editText.setHintTextColor(ContextCompat.getColor(this, R.color.gray));
        }
    }
}
