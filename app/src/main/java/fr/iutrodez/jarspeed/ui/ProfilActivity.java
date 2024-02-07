package fr.iutrodez.jarspeed.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.jarspeed.R;

import fr.iutrodez.jarspeed.network.ApiUtils;

/**
 * The type Profil activity.
 */
public class ProfilActivity extends AppCompatActivity {

    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_activity);
    }

    /**
     * On home button click.
     *
     * @param view the view
     */
    public void onHomeButtonClick(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    /**
     * On edit profile click.
     *
     * @param view the view
     */
    public void onEditProfileClick(View view) {
        Intent intent = new Intent(this, EditProfilActivity.class);
        startActivity(intent);
    }

    /**
     * On logout click.
     *
     * @param view the view
     */
    public void onLogoutClick(View view) {
        clearUserSession();
        redirectToLoginScreen();
    }

    /**
     * On routes click.
     *
     * @param view the view
     */
    public void onRoutesClick(View view) {
        /* Lancer l'activité pour voir tout les parcours */
        Intent intent = new Intent(ProfilActivity.this, AllRoutesActivity.class);
        startActivity(intent);
    }

    /**
     * On delete account click.
     *
     * @param view the view
     */
// Ajoutez cette méthode pour être appelée lorsque l'utilisateur souhaite supprimer son compte
    public void onDeleteAccountClick(View view) {
        // Inflate le layout de la popup de confirmation
        View dialogView = getLayoutInflater().inflate(R.layout.confirmation_popup_deleteaccount, null);

        // Construit la boîte de dialogue
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Crée et affiche la boîte de dialogue
        final AlertDialog dialog = builder.create();

        // Gère le clic sur le bouton Annuler
        dialogView.findViewById(R.id.buttonCancelGeneric).setOnClickListener(v -> dialog.dismiss());

        // Gère le clic sur le bouton Confirmer
        dialogView.findViewById(R.id.buttonConfirmGeneric).setOnClickListener(v -> {
            ApiUtils.deleteAccount(this, response -> {
                Toast.makeText(ProfilActivity.this, "Compte supprimé avec succès.", Toast.LENGTH_SHORT).show();
                clearUserSession();
                redirectToLoginScreen();
                dialog.dismiss();
            }, error -> {
                Toast.makeText(ProfilActivity.this, "Erreur lors de la suppression du compte.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }


    /**
     * Clear user session.
     */
    private void clearUserSession() {
        SharedPreferences preferences = getSharedPreferences("api_shared_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Redirect to login screen.
     */
    private void redirectToLoginScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
