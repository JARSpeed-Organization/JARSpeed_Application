package fr.iutrodez.jarspeed.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
     * On delete account click.
     *
     * @param view the view
     */
// Ajoutez cette méthode pour être appelée lorsque l'utilisateur souhaite supprimer son compte
    public void onDeleteAccountClick(View view) {
        ApiUtils.deleteAccount(this, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Gestion de la réponse, par exemple en affichant un message et en redirigeant vers l'écran de connexion
                Toast.makeText(ProfilActivity.this, "Compte supprimé avec succès.", Toast.LENGTH_SHORT).show();
                clearUserSession();
                redirectToLoginScreen();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Gérer l'erreur, par exemple en affichant un message d'erreur
                Toast.makeText(ProfilActivity.this, "Erreur lors de la suppression du compte.", Toast.LENGTH_SHORT).show();
            }
        });
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
