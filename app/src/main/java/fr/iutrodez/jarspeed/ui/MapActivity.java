package fr.iutrodez.jarspeed.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.jarspeed.R;

public class MapActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor compass;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private MapView mapView;
    private IMapController mapController;
    private LocationManager locationManager;
    private Marker currentLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialisation de la configuration d'OSMdroid
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        compass = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if (compass != null) {
            sensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_GAME);
        }
        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapController = mapView.getController();
        mapController.setZoom(20.0);

        // Gestion de la localisation
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            setupLocation();
        }
    }

    // Méthode pour gérer le clic sur le bouton de profil
    public void onProfileButtonClick(View view) {
        Intent intent = new Intent(this, ProfilActivity.class);
        startActivity(intent);
    }

    private void setupLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 800, 1, locationListener, Looper.getMainLooper());
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                updateMarker(location);
            }
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateMarker(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    private void updateMarker(Location location) {
        GeoPoint newLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
        if (currentLocationMarker == null) {
            currentLocationMarker = new Marker(mapView);
            currentLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(currentLocationMarker);
        }
        currentLocationMarker.setPosition(newLocation);
        mapController.animateTo(newLocation);

        // Configuration de l'icône du marqueur
        Drawable icon = getResources().getDrawable(R.drawable.ic_direct_location, getApplicationContext().getTheme());
        Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
        Drawable resizedIcon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true)); // Redimensionner à 50x50 pixels
        currentLocationMarker.setIcon(resizedIcon);

        mapView.invalidate(); // Rafraîchir la carte
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupLocation();
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            // Obtenez l'angle de la boussole
            float azimuth = event.values[0];

            // Faites pivoter la carte en fonction de l'angle de la boussole
            mapView.setMapOrientation(-azimuth);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Vous pouvez implémenter la gestion des changements de précision ici
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (compass != null) {
            sensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
