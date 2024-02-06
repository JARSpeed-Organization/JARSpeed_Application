package fr.iutrodez.jarspeed.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.jarspeed.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import fr.iutrodez.jarspeed.model.Coordinate;
import fr.iutrodez.jarspeed.model.RouteDTO;
import fr.iutrodez.jarspeed.network.ApiUtils;
import fr.iutrodez.jarspeed.network.RouteUtils;
import fr.iutrodez.jarspeed.model.RouteDTO.PointOfInterest;

/**
 * The type Map activity.
 */
public class MapActivity extends AppCompatActivity implements SensorEventListener {

    private static final String MY_USER_AGENT = "USER_AGENT";
    private boolean isOngoing;
    /**
     * The Sensor manager.
     */
    private boolean isStarted;
    private SensorManager sensorManager;
    /**
     * The Compass.
     */
    private Sensor compass;
    /**
     * The constant REQUEST_LOCATION_PERMISSION.
     */
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    /**
     * The Map view.
     */
    private MapView mapView;
    /**
     * The Map controller.
     */
    private IMapController mapController;
    /**
     * The Fused location client.
     */
    private FusedLocationProviderClient fusedLocationClient;
    /**
     * The Current location marker.
     */
    private Marker currentLocationMarker;

    private Polyline line;
    private ImageButton btnRun;
    private long startDate;
    private ImageView imgAccount;
    private ImageView imgListRoute;
    private List<PointOfInterest> listPointOfInterests;

    /**
     * Create location request location request.
     *
     * @return the location request
     */
    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // Mise à jour de la position toutes les 10 secondes
        locationRequest.setFastestInterval(5000); // Mise à jour la plus rapide acceptée
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    /**
     * The Location callback.
     */
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                updateMarker(location);
            }
        }
    };

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
        btnRun = findViewById(R.id.fabAdd);

        // Gestion de la localisation
        isOngoing = false;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            setupLocation();
            startLocationUpdates();
        }

        /* Lancer l'activité pour voir tout les parcours */
        ImageView imageViewAllParcours = findViewById(R.id.imageViewAllParcours);
        imageViewAllParcours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, AllCoursesActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Start location updates.
     */
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Vérifiez si l'autorisation a été accordée
            return;
        }
        fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, Looper.getMainLooper());
    }

    /**
     * On profile button click.
     *
     * @param view the view
     */
    public void onProfileButtonClick(View view) {
        Intent intent = new Intent(this, ProfilActivity.class);
        startActivity(intent);
    }

    public void onListRouteOnClicked(View view) {
        if (isStarted) {
            //durant une course lancé (point of interest)
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.point_of_interest_dialog, null);
            builder.setView(dialogView);

            Button buttonAddPointOfInterest = dialogView.findViewById(R.id.buttonAddPointOfInterest);
            Button buttonCancel = dialogView.findViewById(R.id.buttonCancelPointOfInterest);
            AlertDialog dialog = builder.create();

            final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
            final Drawable originalBackground = rootView.getBackground();
            final WindowManager.LayoutParams params = getWindow().getAttributes();
            params.alpha = 0.2f;
            getWindow().setAttributes(params);
            buttonCancel.setOnClickListener(v -> {
                dialog.dismiss();
                params.alpha = 1.0f;
                getWindow().setAttributes(params);
            });

            buttonAddPointOfInterest.setOnClickListener(v -> {
                EditText title = dialogView.findViewById(R.id.editTextTitlePointOfInterest);
                String titleOfThePointOfInterest = title.getText().toString();
                double latitude = line.getPoints().get(line.getPoints().size() - 1).getLatitude();
                double longitude = line.getPoints().get(line.getPoints().size() - 1).getLongitude();
                Coordinate coor = new Coordinate(latitude, longitude);
                PointOfInterest point = new PointOfInterest(titleOfThePointOfInterest, coor);
                listPointOfInterests.add(point);
                dialog.dismiss();
                params.alpha = 1.0f;
                getWindow().setAttributes(params);
            });

            dialog.setOnDismissListener(d -> {
                rootView.setBackground(originalBackground);
                params.alpha = 1.0f;
                getWindow().setAttributes(params);
            });

            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } else {
            //course pas lancé (burger Menu pour liste de route)
            /* Lancer l'activité pour voir tout les parcours */
            Intent intent = new Intent(MapActivity.this, AllCoursesActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Start recording.
     * @param view View
     */
    public void onRunButtonClick(View view) {
        if (isOngoing) {
            openPopupPause();
        } else {
            // Lancement de l'enregistrement
            imgAccount.setVisibility(View.GONE);
            btnRun.setImageResource(R.drawable.ic_pause);
            imgListRoute.setImageResource(R.drawable.vector);
            isOngoing = true;
            isStarted = true;
            startDate = System.currentTimeMillis();
            List<GeoPoint> geoPoints = new ArrayList<>(); // Points de la route
            setupLocation();
            line = new Polyline(); // Créez une nouvelle polyline
            listPointOfInterests = new ArrayList<PointOfInterest>();
            line.setPoints(geoPoints); // Ajoutez les points à la polyline
            mapView.getOverlays().add(line); // Ajoutez la polyline à la carte
            mapView.invalidate(); // Rafraîchissez la carte
        }
    }

    private void openPopupPause() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.pause_dialog, null);
        builder.setView(dialogView);

        Button buttonStopAndSave = dialogView.findViewById(R.id.buttonStopAndSave);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        AlertDialog dialog = builder.create();

        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        final Drawable originalBackground = rootView.getBackground();
        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.2f;
        getWindow().setAttributes(params);
        buttonCancel.setOnClickListener(v -> {
            dialog.dismiss();
            params.alpha = 1.0f;
            getWindow().setAttributes(params);
        });
        buttonStopAndSave.setOnClickListener(v -> {

            // Get informations of route
            EditText title = dialogView.findViewById(R.id.editTextTitle);
            EditText description = dialogView.findViewById(R.id.editTextDescription);
            long endDate = System.currentTimeMillis();
            RouteDTO routeDTO =
                    new RouteDTO(null,
                            startDate,
                            endDate,
                            RouteUtils.pointsToCoordinates(line.getPoints()),
                            listPointOfInterests,
                            RouteUtils.generateTitle(title.getText().toString(), endDate),
                            description.getText().toString());

            ApiUtils.saveRoute(this, routeDTO, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (isStarted) {
                        Toast.makeText(MapActivity.this, getText(R.string.route_register), Toast.LENGTH_SHORT).show();
                        mapView.getOverlays().remove(line);
                        line = null;
                        mapView.invalidate();
                        btnRun.setImageResource(R.drawable.ic_add);
                        isOngoing = false;
                        isStarted = false;
                        imgAccount.setVisibility(View.VISIBLE);
                        imgListRoute.setImageResource(R.drawable.ic_parcours);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(MapActivity.this, "Erreur : " + error + " " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    // TODO Manage errors
                }
            });
            dialog.dismiss();
            params.alpha = 1.0f;
            getWindow().setAttributes(params);
        });
        dialog.setOnDismissListener(d -> {
            rootView.setBackground(originalBackground);
            params.alpha = 1.0f;
            getWindow().setAttributes(params);
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void setupLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        updateMarker(location);
                    }
                })
                .addOnFailureListener(this, e -> {
                    // Gérez l'échec ici
                });
        }
    }

    /**
     * Update marker.
     *
     * @param location the location
     */
    private void updateMarker(Location location) {
        GeoPoint newLocation = new GeoPoint(location.getLatitude(), location.getLongitude());

        // Manamgment line overlay
        if (line != null && isStarted) {
            line.addPoint(newLocation);
            mapView.invalidate(); // Rafraîchissez la carte
        }

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

    // TODO Arret recuperer position quand arret application
}
