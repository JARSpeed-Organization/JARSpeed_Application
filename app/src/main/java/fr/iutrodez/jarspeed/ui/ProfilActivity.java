package fr.iutrodez.jarspeed.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jarspeed.R;

public class ProfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_activity);
    }

    public void onHomeButtonClick(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
    public void onEditProfileClick(View view) {
        Intent intent = new Intent(this, EditProfilActivity.class);
        startActivity(intent);
    }

    public void onLogoutClick(View view) {
        clearUserSession();
        redirectToLoginScreen();
    }

    private void clearUserSession() {
        SharedPreferences preferences = getSharedPreferences("api_shared_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    private void redirectToLoginScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
