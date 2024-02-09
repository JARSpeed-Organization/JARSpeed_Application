package fr.iutrodez.jarspeed.ui;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import fr.iutrodez.jarspeed.model.user.User;
import fr.iutrodez.jarspeed.network.ApiUtils;
import fr.iutrodez.jarspeed.network.RouteUtils;
import fr.iutrodez.jarspeed.model.RouteDTO.PointOfInterest;
import fr.iutrodez.jarspeed.utils.SharedPreferencesManager;

/**
 * The type Map activity.
 */
public class MapActivity extends AppCompatActivity implements SensorEventListener {

    /**
     * The constant MY_USER_AGENT.
     */
    private static final String MY_USER_AGENT = "USER_AGENT";
    /**
     * The Is ongoing.
     */
    private boolean isOngoing;
    /**
     * The Sensor manager.
     */
    private boolean isStarted;
    /**
     * The Sensor manager.
     */
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

    /**
     * The Line.
     */
    private Polyline line;
    /**
     * The Btn run.
     */
    private ImageButton btnRun;
    /**
     * The Start date.
     */
    private LocalDateTime startDate;
    /**
     * The Img account.
     */
    private ImageView imgAccount;
    /**
     * The Img list route.
     */
    private ImageView imgListRoute;
    /**
     * The List point of interests.
     */
    private List<PointOfInterest> listPointOfInterests;
    /**
     * The Datas block.
     */
    private RelativeLayout datasBlock;
    /**
     * The Text view timer.
     */
    private TextView textViewTimer;
    /**
     * The Handler.
     */
    private Handler handler;
    /**
     * The Time spend millisecond.
     */
    private long timeSpendMillisecond;

    /**
     * The Timer in pause.
     */
    private boolean timerInPause;

    /**
     * The Runnable.
     */
    private Runnable runnable;
    /**
     * The Initialise time.
     */
    private final String INITIALISE_TIME = "00:00:00";

    /**
     * The constant EARTH_RADIUS.
     */
    private static final double EARTH_RADIUS = 6371;

    /**
     * The Kilometers run.
     */
    private double kilometersRun;

    /**
     * The Kilometers.
     */
    private TextView kilometers;

    /**
     * The Time for one kilometer.
     */
    private TextView timeForOneKilometer;

    /**
     * The Time in hour.
     */
    private double timeInHour;

    /**
     * The Constante calcul kcal.
     */
    private final double CONSTANTE_CALCUL_KCAL = 1.036;

    /**
     * The Kilocal.
     */
    private TextView kilocal;

    private String weight;


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
        imgAccount = findViewById(R.id.imageViewProfile);
        imgListRoute = findViewById(R.id.imageViewAllParcours);
        datasBlock = findViewById(R.id.datas_while_running);
        datasBlock.setVisibility(View.INVISIBLE);
        textViewTimer = findViewById(R.id.timerText);
        handler = new Handler();
        timeSpendMillisecond = -1;
        timerInPause = false;
        isOngoing = false;
        kilometers = findViewById(R.id.km);
        timeForOneKilometer = findViewById(R.id.time_km);
        timeInHour = 0;
        kilocal = findViewById(R.id.kcal);

        // Gestion de la localisation
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            setupLocation();
            startLocationUpdates();
        }

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

    /**
     * On list route on clicked.
     *
     * @param view the view
     */
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
            Intent intent = new Intent(MapActivity.this, AllRoutesActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Start recording.
     *
     * @param view View
     */
    @SuppressLint("ResourceType")
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
            startDate = LocalDateTime.now();
            List<GeoPoint> geoPoints = new ArrayList<>(); // Points de la route
            setupLocation();
            line = new Polyline(); // Créez une nouvelle polyline
            listPointOfInterests = new ArrayList<PointOfInterest>();
            line.setPoints(geoPoints); // Ajoutez les points à la polyline
            mapView.getOverlays().add(line); // Ajoutez la polyline à la carte
            mapView.invalidate(); // Rafraîchissez la carte
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_to_bot);
            datasBlock.setVisibility(View.VISIBLE);
            datasBlock.startAnimation(animation);
            startTimer();
            kilometersRun = 0;
            String strValue = String.format("%.2f", kilometersRun);
            kilometers.setText(strValue + " km");
        }
    }

    /**
     * Open popup pause.
     */
    private void openPopupPause() {
        pauseTimer();
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
            restartTimer();
            dialog.dismiss();
            params.alpha = 1.0f;
            getWindow().setAttributes(params);
        });
        buttonStopAndSave.setOnClickListener(v -> {

            // Get informations of route
            EditText title = dialogView.findViewById(R.id.editTextTitle);
            EditText description = dialogView.findViewById(R.id.editTextDescription);
            LocalDateTime endDate = LocalDateTime.now();
            RouteDTO routeDTO =
                    new RouteDTO(null,
                            startDate.toString(),
                            endDate.toString(),
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
                        animationEndForBlockData();
                        reinitialiserTimer();
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

    /**
     * Sets location.
     */
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
            if (line.getPoints().size() > 1) {
                GeoPoint beforeLast = line.getPoints().get(line.getPoints().size()-2);
                GeoPoint last = line.getPoints().get(line.getPoints().size()-1);
                kilometersRun += calculateDistanceBetweenTwoPoints(beforeLast, last);
                String strValue = String.format("%.2f", kilometersRun);
                kilometers.setText(strValue + " km");

                double forOneKmInHour = timeInHour / kilometersRun;
                double forOneKmInMin = forOneKmInHour*60;
                int minutes = (int) forOneKmInMin;
                int secondes = (int) ((forOneKmInMin - minutes) * 60);
                strValue = String.format("%02d:%02d", minutes, secondes);
                timeForOneKilometer.setText(strValue + " /km");

                loadUserWeight();
                double userWeight = Double.parseDouble(weight);
                double kcalBurn = userWeight * kilometersRun * CONSTANTE_CALCUL_KCAL;
                strValue = String.format("%.2F", kcalBurn);
                kilocal.setText(strValue + " Kcal");
            }
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

    private void loadUserWeight() {
        String token = SharedPreferencesManager.getAuthToken(this);
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Vous n'êtes pas connecté.", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiUtils.loadUserProfile(this, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    weight = jsonResponse.optString("weight");

                } catch (JSONException e) {
                    Log.e("LoadUserProfile", "Error parsing JSON", e);
                    Toast.makeText(MapActivity.this, "Erreur lors du parsing des données", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LoadUserProfile", "Error loading weight: " + error.toString());
                Toast.makeText(MapActivity.this, "Erreur lors du chargement du poids", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Animation end for block data.
     */
    private void animationEndForBlockData() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_to_top);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                datasBlock.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        datasBlock.startAnimation(animation);
    }

    /**
     * Start timer.
     */
    private void startTimer() {
        runnable = new Runnable() {
            @Override
            public void run() {
                timeSpendMillisecond += 1000;

                int hour = (int) (timeSpendMillisecond / 3600000);
                int minutes = (int) ((timeSpendMillisecond % 3600000) / 60000);
                int secondes = (int) ((timeSpendMillisecond % 60000) / 1000);

                timeInHour = hour + (minutes / 60.0) + (secondes / 3600.0);
                String timeSpend = String.format("%02d:%02d:%02d", hour, minutes, secondes);
                textViewTimer.setText(timeSpend);

                handler.postDelayed(runnable, 1000);
            }
        };
        handler.post(runnable);
    }


    /**
     * Pause timer.
     */
    private void pauseTimer() {
        if (!timerInPause) {
            timerInPause = true;
            handler.removeCallbacks(runnable);
        }
    }

    /**
     * Restart timer.
     */
    private void restartTimer() {
        if (timerInPause) {
            timerInPause = false;
            handler.postDelayed(runnable, 1000);
        }
    }

    /**
     * Reinitialiser timer.
     */
    private void reinitialiserTimer() {
        timeSpendMillisecond = -1;
        textViewTimer.setText(INITIALISE_TIME);
        handler.removeCallbacks(runnable);
    }

    /**
     * Calculate distance between two points double.
     *
     * @param coord1 the coord 1
     * @param coord2 the coord 2
     * @return the double
     */
    public static double calculateDistanceBetweenTwoPoints(GeoPoint coord1, GeoPoint coord2) {
        double lat1 = Math.toRadians(coord1.getLatitude());
        double lon1 = Math.toRadians(coord1.getLongitude());
        double lat2 = Math.toRadians(coord2.getLatitude());
        double lon2 = Math.toRadians(coord2.getLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Assurez-vous de supprimer les callbacks du Handler lorsque l'activité est détruite
        handler.removeCallbacksAndMessages(null);
    }

    // TODO Arret recuperer position quand arret application
}
