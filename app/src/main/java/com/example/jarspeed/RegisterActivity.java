package com.example.jarspeed;

import static com.example.jarspeed.ValidationUtils.isEmpty;
import static com.example.jarspeed.ValidationUtils.isValidEmail;
import static com.example.jarspeed.ValidationUtils.isValidPassword;
import static com.example.jarspeed.ValidationUtils.resetFieldBorders;
import static com.example.jarspeed.ValidationUtils.setErrorBorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
        resetFieldBorders(this);
        if (validateFields()) {
            // All fields are valid, proceed to the home activity
            Intent homeIntent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(homeIntent);
            finish();
        }
    }

    private boolean validateFields() {
        boolean isValid = true;

        // Reset all edits text fields borders.
        resetFieldBorders(this, nameEditText, firstnameEditText, emailEditText, passwordEditText, passwordConfirmationEditText);

        // Check not empty field
        if (isEmpty(nameEditText)) {
            setErrorBorder(this, nameEditText);
            isValid = false;
        }

        if (isEmpty(firstnameEditText)) {
            setErrorBorder(this, firstnameEditText);
            isValid = false;
        }

        boolean emailInvalid = isEmpty(emailEditText) || !isValidEmail(emailEditText.getText().toString());
        if (emailInvalid) {
            setErrorBorder(this, emailEditText);
            isValid = false;
        }

        if (isEmpty(passwordEditText)) {
            setErrorBorder(this, passwordEditText);
            isValid = false;
        }

        if (isEmpty(passwordConfirmationEditText)) {
            setErrorBorder(this, passwordConfirmationEditText);
            isValid = false;
        }

        // If email is the only invalid field
        if (emailInvalid && !isEmpty(nameEditText) && !isEmpty(firstnameEditText) && !isEmpty(passwordEditText) && !isEmpty(passwordConfirmationEditText)) {
            Toast.makeText(this, "Format email non respecté", Toast.LENGTH_LONG).show();
            return false;
        }

        // If other fields are also invalid
        if (!isValid) {
            Toast.makeText(this, "Veuillez remplir tous les champs pour vous inscrire", Toast.LENGTH_LONG).show();
            return false;
        }
        // Check Strength password
        if (!isValidPassword(passwordEditText.getText().toString())) {
            setErrorBorder(this, passwordEditText);
            Toast.makeText(this, "Le mot de passe doit contenir au moins 8 caractères, dont une majuscule et un caractère spécial", Toast.LENGTH_LONG).show();
            isValid = false;
        }
        // check passwords
        if (!passwordEditText.getText().toString().equals(passwordConfirmationEditText.getText().toString())) {
            setErrorBorder(this, passwordEditText);
            setErrorBorder(this, passwordConfirmationEditText);
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
