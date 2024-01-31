package fr.iutrodez.jarspeed.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jarspeed.R;

/**
 * The type Profil activity.
 */
public class ProfilActivity extends AppCompatActivity {

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
