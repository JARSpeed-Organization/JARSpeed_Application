package fr.iutrodez.jarspeed.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jarspeed.R;

import es.dmoral.toasty.Toasty;
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
        View dialogView = getLayoutInflater().inflate(R.layout.confirmation_popup, null);

        TextView textViewGeneric = dialogView.findViewById(R.id.editTextGeneric);
        textViewGeneric.setText(R.string.confirm_logout);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        // Handles clicks on the Cancel button
        dialogView.findViewById(R.id.buttonCancelGeneric).setOnClickListener(v -> dialog.dismiss());

        // Manages the click on the Confirm button to disconnect
        dialogView.findViewById(R.id.buttonConfirmGeneric).setOnClickListener(v -> {
            clearUserSession();
            redirectToLoginScreen();
            dialog.dismiss();
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }


    /**
     * On routes click.
     *
     * @param view the view
     */
    public void onRoutesClick(View view) {
        /* Launch activity to see all routes */
        Intent intent = new Intent(ProfilActivity.this, AllRoutesActivity.class);
        startActivity(intent);
    }

    /**
     * On delete account click.
     *
     * @param view the view
     */
    public void onDeleteAccountClick(View view) {
        // Inflate confirmation popup layout
        View dialogView = getLayoutInflater().inflate(R.layout.confirmation_popup, null);

        // Builds the dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        // Handles clicks on the Cancel button
        dialogView.findViewById(R.id.buttonCancelGeneric).setOnClickListener(v -> dialog.dismiss());

        // Manages the click on the Confirm button
        dialogView.findViewById(R.id.buttonConfirmGeneric).setOnClickListener(v -> {
            ApiUtils.deleteAccount(this, response -> {
                Toasty.success(ProfilActivity.this, "Compte supprimé avec succès.", Toast.LENGTH_SHORT, true).show();
                clearUserSession();
                redirectToLoginScreen();
                dialog.dismiss();
            }, error -> {
                Toasty.error(ProfilActivity.this, "Erreur lors de la suppression du compte.", Toast.LENGTH_SHORT, true).show();
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
        Toasty.success(ProfilActivity.this, "Déconnexion réussie", Toast.LENGTH_SHORT, true).show();


    }
}
