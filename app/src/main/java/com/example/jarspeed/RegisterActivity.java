package com.example.jarspeed;

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

        TextView loginTextView = findViewById(R.id.registerPrompt);
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(loginIntent);
            }
        });
    }

    // This method is called when the register button is clicked
    public void onRegisterClick(View view) {
        resetFieldBorders();
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
        resetFieldBorders(nameEditText, firstnameEditText, emailEditText, passwordEditText, passwordConfirmationEditText);

        // Check not empty field
        if (isEmpty(nameEditText)) {
            setErrorBorder(nameEditText);
            isValid = false;
        }

        if (isEmpty(firstnameEditText)) {
            setErrorBorder(firstnameEditText);
            isValid = false;
        }

        boolean emailInvalid = isEmpty(emailEditText) || !isValidEmail(emailEditText.getText().toString());
        if (emailInvalid) {
            setErrorBorder(emailEditText);
            isValid = false;
        }

        if (isEmpty(passwordEditText)) {
            setErrorBorder(passwordEditText);
            isValid = false;
        }

        if (isEmpty(passwordConfirmationEditText)) {
            setErrorBorder(passwordConfirmationEditText);
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
            setErrorBorder(passwordEditText);
            Toast.makeText(this, "Le mot de passe doit contenir au moins 8 caractères, dont une majuscule et un caractère spécial", Toast.LENGTH_LONG).show();
            isValid = false;
        }
        // check passwords
        if (!passwordEditText.getText().toString().equals(passwordConfirmationEditText.getText().toString())) {
            setErrorBorder(passwordEditText);
            setErrorBorder(passwordConfirmationEditText);
            Toast.makeText(this, "Veuillez saisir deux mots de passe identiques", Toast.LENGTH_LONG).show();
            return false;
        }

        return isValid;
    }

    // Method to check email format
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailRegex);
    }

    // Check password strength
    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,}$";
        return password.matches(passwordRegex);
    }


    // Checks if an EditText is empty
    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

    private void setHintColors(EditText... editsText) {
        for (EditText editText : editsText) {
            editText.setHintTextColor(ContextCompat.getColor(this, R.color.gray));
        }
    }

    // Sets an error border for invalid EditText fields
    private void setErrorBorder(EditText editText) {
        int paddingLeft = editText.getPaddingLeft();
        int paddingTop = editText.getPaddingTop();
        int paddingRight = editText.getPaddingRight();
        int paddingBottom = editText.getPaddingBottom();
        // New background error
        editText.setBackground(ContextCompat.getDrawable(this, R.drawable.edittext_error_border));
        editText.setHintTextColor(ContextCompat.getColor(this, R.color.gray));


        // Re apply paddings
        editText.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }


    // Resets the borders and hint text color of EditText fields
    private void resetFieldBorders(EditText... editTexts) {
        for (EditText editText : editTexts) {
            int paddingLeft = editText.getPaddingLeft();
            int paddingTop = editText.getPaddingTop();
            int paddingRight = editText.getPaddingRight();
            int paddingBottom = editText.getPaddingBottom();
            editText.setBackground(ContextCompat.getDrawable(this, R.drawable.edittext_background));
            editText.setHintTextColor(ContextCompat.getColor(this, R.color.gray));
            editText.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        }
    }


}
