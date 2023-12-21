package com.example.jarspeed;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class EditProfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profil_activity);
    }


    public void returnToProfilPage(View view) {
        // Intent pour ouvrir la vue profil
        Intent intent = new Intent(this, ProfilActivity.class);
        startActivity(intent);
    }

    public void onChangePasswordClick(View view) {
        // Assombrir l'arrière-plan
        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        final Drawable originalBackground = rootView.getBackground();

        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.2f; // Modifier selon les besoins pour l'effet d'assombrissement
        getWindow().setAttributes(params);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.password_dialog, null);
        builder.setView(dialogView);

        EditText editTextPassword = dialogView.findViewById(R.id.editTextPassword);
        EditText editTextConfirmPassword = dialogView.findViewById(R.id.editTextConfirmPassword);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        Button buttonConfirm = dialogView.findViewById(R.id.buttonConfirm);

        AlertDialog dialog = builder.create();

        buttonCancel.setOnClickListener(v -> {
            dialog.dismiss();
            // Restaurer l'arrière-plan à la fermeture de la boîte de dialogue
            params.alpha = 1.0f;
            getWindow().setAttributes(params);
        });
        buttonConfirm.setOnClickListener(v -> {
            String password = editTextPassword.getText().toString();
            String confirmPassword = editTextConfirmPassword.getText().toString();
            if (isValidPassword(password) && password.equals(confirmPassword)) {
                // Insérez ici la logique pour changer le mot de passe
                // ...
                dialog.dismiss();
            } else {
                // Affichez un message d'erreur si les mots de passe ne correspondent pas ou ne sont pas valides
                Toast.makeText(this, "Les mots de passe ne correspondent pas ou ne sont pas valides.", Toast.LENGTH_LONG).show();
                editTextPassword.setError("Invalid Password");
                editTextConfirmPassword.setError("Passwords do not match");
            }
        });

        dialog.setOnDismissListener(d -> {
            // Restaurer l'arrière-plan à la fermeture de la boîte de dialogue
            rootView.setBackground(originalBackground);
            params.alpha = 1.0f;
            getWindow().setAttributes(params);
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    // Réutilisez la méthode isValidPassword de votre RegisterActivity ici.
    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,}$";
        return password.matches(passwordRegex);
    }
}