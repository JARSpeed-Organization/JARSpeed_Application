package fr.iutrodez.jarspeed;

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
import android.widget.Spinner;
import android.widget.Toast;

import com.example.jarspeed.R;


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

        // Edits texts de la popup
        EditText editTextOldPassword = dialogView.findViewById(R.id.editTextOldPassword);
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
            String oldPassword = editTextOldPassword.getText().toString();
            String password = editTextPassword.getText().toString();
            String confirmPassword = editTextConfirmPassword.getText().toString();

            // TODO: Récupérez l'ancien mot de passe de l'utilisateur depuis la base de données ou le système de gestion des utilisateurs.
            String userCurrentPassword = "admin"; // bouchon : à remplacer par la valeur récupérée dans l'API ou autre

            if (!oldPassword.equals(userCurrentPassword)) {
                Toast.makeText(this, "L'ancien mot de passe est incorrect.", Toast.LENGTH_LONG).show();
                editTextOldPassword.setError("Ancien mot de passe incorrect");
                return;
            }

            if (ValidationUtils.isValidPassword(password) && password.equals(confirmPassword)) {
                // TODO: Insérez ici la logique pour changer le mot de passe
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

    public void onGenericEditClick(View view) {
        // Assombrir l'arrière-plan
        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        final Drawable originalBackground = rootView.getBackground();
        final WindowManager.LayoutParams params = getWindow().getAttributes();
        final EditText targetEditText = (EditText) view; // Conserver une référence au EditText cliqué

        params.alpha = 0.2f;
        getWindow().setAttributes(params);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.generic_edit_dialog, null);
        builder.setView(dialogView);

        EditText editTextGeneric = dialogView.findViewById(R.id.editTextGeneric);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancelGeneric);
        Button buttonConfirm = dialogView.findViewById(R.id.buttonConfirmGeneric);
        boolean isEmailField = view.getId() == R.id.emailItem;

        String typeModification = "";
        // Configurez le hint de l'EditText en fonction du type de modification
        if (view.getId() == R.id.nameItem) {
            editTextGeneric.setHint("Nouveau nom");
            typeModification = "name";
        } else if (view.getId() == R.id.firstNameItem) {
            editTextGeneric.setHint("Nouveau prénom");
            typeModification = "firstname";
        } else {
            editTextGeneric.setHint("Nouvel email");
            typeModification = "email";
        }
        AlertDialog dialog = builder.create();

        buttonCancel.setOnClickListener(v -> {
            dialog.dismiss();
            params.alpha = 1.0f;
            getWindow().setAttributes(params);
        });

        buttonConfirm.setOnClickListener(v -> {
            String newValue = editTextGeneric.getText().toString().trim();
            if (!newValue.isEmpty()) {
                if (isEmailField && !ValidationUtils.isValidEmail(newValue)) {
                    Toast.makeText(this, "Format de l'email invalide.", Toast.LENGTH_LONG).show();
                    editTextGeneric.setError("Format de l'email invalide");
                    return;
                }
                // Mettre à jour la valeur du EditText concerné
                targetEditText.setText(newValue);
                // TODO: Mettre à jour la valeur dans l'API pour l'utilisateur
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Veuillez entrer une valeur valide.", Toast.LENGTH_LONG).show();
            }
        });

        dialog.setOnDismissListener(d -> {
            rootView.setBackground(originalBackground);
            params.alpha = 1.0f;
            getWindow().setAttributes(params);
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void onChangeHealthData(View view) {
        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        final Drawable originalBackground = rootView.getBackground();
        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.2f;
        getWindow().setAttributes(params);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.health_data_dialog, null);
        builder.setView(dialogView);

        EditText editTextAge = dialogView.findViewById(R.id.editTextAge);
        Spinner spinnerGender = dialogView.findViewById(R.id.spinnerGender);
        EditText editTextWeight = dialogView.findViewById(R.id.editTextWeight);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancelHealthData);
        Button buttonConfirm = dialogView.findViewById(R.id.buttonConfirmHealthData);

        AlertDialog dialog = builder.create();

        buttonCancel.setOnClickListener(v -> {
            dialog.dismiss();
            params.alpha = 1.0f;
            getWindow().setAttributes(params);
        });
        buttonConfirm.setOnClickListener(v -> {
            // Récupérer et valider les données saisies par l'utilisateur
            // TODO: Mettre à jour les données de l'utilisateur dans l'API ou la base de données
            dialog.dismiss();
        });

        dialog.setOnDismissListener(d -> {
            rootView.setBackground(originalBackground);
            params.alpha = 1.0f;
            getWindow().setAttributes(params);
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


}