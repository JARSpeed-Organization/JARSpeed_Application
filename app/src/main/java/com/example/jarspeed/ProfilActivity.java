package com.example.jarspeed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class ProfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_activity);

        // Ajoutez un OnClickListener pour le bouton home
        ImageView homeButton = findViewById(R.id.homeButton);
        TextView editProfil = findViewById(R.id.profileItem);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent pour ouvrir la vue profil
                Intent intent = new Intent(ProfilActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
        editProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent pour ouvrir la vue edition profil
                Intent intent = new Intent(ProfilActivity.this, EditProfilActivity.class);
                startActivity(intent);
            }
        });
    }
}
