package com.example.jarspeed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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
}