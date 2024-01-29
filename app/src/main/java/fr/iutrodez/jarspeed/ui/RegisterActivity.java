package fr.iutrodez.jarspeed.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.jarspeed.R;

import java.nio.charset.StandardCharsets;

import fr.iutrodez.jarspeed.model.UserRegistrationRequest;
import fr.iutrodez.jarspeed.network.ApiUtils;
import fr.iutrodez.jarspeed.utils.ValidationUtils;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText firstnameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
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
    }

    public void onLoginClick(View view) {
        // Démarrez login page ici
        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);
    }
    // This method is called when the register button is clicked
    public void onRegisterClick(View view) {
        if (validateFields()) {
            String name = nameEditText.getText().toString();
            String firstname = firstnameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            UserRegistrationRequest registrationRequest = new UserRegistrationRequest(name, firstname, email, password);

            ApiUtils.registerUser(this, registrationRequest, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(RegisterActivity.this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                    goToLoginActivity();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMsg = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        Toast.makeText(RegisterActivity.this, "Erreur d'inscription : " + errorMsg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void goToLoginActivity() {
        Intent loginIntent = new Intent(this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
        finish();
    }

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
            Toast.makeText(this, "Format email non respecté", Toast.LENGTH_LONG).show();
            return false;
        }

        // If other fields are also invalid
        if (!isValid) {
            Toast.makeText(this, "Veuillez remplir tous les champs pour vous inscrire", Toast.LENGTH_LONG).show();
            return false;
        }
        // Check Strength password
        if (!ValidationUtils.isValidPassword(passwordEditText.getText().toString())) {
            ValidationUtils.setErrorBorder(this, passwordEditText);
            Toast.makeText(this, "Le mot de passe doit contenir au moins 8 caractères, dont une majuscule et un caractère spécial", Toast.LENGTH_LONG).show();
            isValid = false;
        }
        // check passwords
        if (!passwordEditText.getText().toString().equals(passwordConfirmationEditText.getText().toString())) {
            ValidationUtils.setErrorBorder(this, passwordEditText);
            ValidationUtils.setErrorBorder(this, passwordConfirmationEditText);
            Toast.makeText(this, "Veuillez saisir deux mots de passe identiques", Toast.LENGTH_LONG).show();
            return false;
        }

        return isValid;
    }

    private void setHintColors(EditText... editsText) {
        for (EditText editText : editsText) {
            editText.setHintTextColor(ContextCompat.getColor(this, R.color.gray));
        }
    }
}
