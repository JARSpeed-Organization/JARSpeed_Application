package fr.iutrodez.jarspeed.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import fr.iutrodez.jarspeed.model.route.CustomPoint;
import fr.iutrodez.jarspeed.model.route.Route;
import fr.iutrodez.jarspeed.network.ApiUtils;
import fr.iutrodez.jarspeed.network.RouteUtils;
import fr.iutrodez.jarspeed.utils.SharedPreferencesManager;

/**
 * The type Map activity.
 */
public class MapActivity extends AppCompatActivity implements SensorEventListener {

    /**
     * The constant REQUEST_LOCATION_PERMISSION.
     */
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    /**
     * The constant EARTH_RADIUS.
     */
    private static final double EARTH_RADIUS = 6371;
    /**
     * The Constante calcul kcal.
     */
    private final double CONSTANTE_CALCUL_KCAL = 1.036;
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
    private List<Route.PointOfInterest> listPointOfInterests;
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
     * The Kilocal.
     */
    private TextView kilocal;
    /**
     * The Weight.
     */
    private double weight;
    /**
     * The Elevation gain.
     */
    private double elevationGain;
    /**
     * The Elevation loss.
     */
    private double elevationLoss;
    /**
     * The Last altitude.
     */
    private double lastAltitude;
    /**
     * The Long press button.
     */
    private FloatingActionButton longPressButton;

    /**
     * The Speed.
     */
    private String speed;

    /**
     * Configures and returns a LocationRequest with high accuracy and suitable intervals.
     * @return Configured LocationRequest object.
     */
    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // update location all 10 sec
        locationRequest.setFastestInterval(5000); // Fastest update accepted
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    /**
     * Handles location updates, updating the marker to reflect the user's current location.
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

        // Initialize OSMdroid configuration
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
        weight = SharedPreferencesManager.getWeight(ctx);
        Log.e("weight", weight + "");
        longPressButton = findViewById(R.id.fabAdd);
        longPress();

        // Location management
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            setupLocation();
            startLocationUpdates();
        }

        // Creation of a callback for pressing the back button.
        // disable back button.
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        };
        // Get the OnBackPressedDispatcher and add the callback
        getOnBackPressedDispatcher().addCallback(this, callback);
    }


    /**
     * Configures long press functionality on the main button to start or stop tracking.
     */
    private void longPress() {
        longPressButton.setOnTouchListener(new View.OnTouchListener() {
            private Handler handler = new Handler();
            private boolean isPressedLongEnough = false;

            private Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    // Mark as true if the user has held down the button long enough
                    isPressedLongEnough = true;
                    // Action to be taken after pressing and holding for 3 seconds
                    if (!isLocationEnabled()) {
                        promptEnableLocation();
                    } else {
                        onRunButtonClick(longPressButton);
                    }
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Reset this variable with each new press
                        isPressedLongEnough = false;
                        // Starts timer when user starts pressing
                        handler.postDelayed(runnable, 3000); // Modifier ici si nécessaire
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Cancels timer if user releases button within 3 seconds
                        handler.removeCallbacks(runnable);
                        if (!isPressedLongEnough) {
                            // Display a toasty to inform the user
                            Toasty.info(MapActivity.this, "Maintenez le bouton pressé pendant 3 secondes pour démarrer ou arrêter.", Toast.LENGTH_SHORT, true).show();
                        }
                        return true;
                }
                return false;
            }
        });
    }


    /**
     * Starts location updates, requesting updates from the fusedLocationClient.
     */
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check if authorization has been granted
            return;
        }
        fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, Looper.getMainLooper());
    }

    /**
     * Navigates to the profile activity when the profile button is clicked.
     * @param view The clicked view.
     */
    public void onProfileButtonClick(View view) {
        Intent intent = new Intent(this, ProfilActivity.class);
        startActivity(intent);
    }

    /**
     * Manages clicks on the route list button, presenting different options based on the current tracking state.
     * @param view The clicked view.
     */
    public void onListRouteOnClicked(View view) {
        if (isStarted) {
            //during a race launched (point of interest)
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
                CustomPoint coor = new CustomPoint(longitude, latitude);
                Route.PointOfInterest pointOfInterest = new Route.PointOfInterest(titleOfThePointOfInterest, coor);
                listPointOfInterests.add(pointOfInterest);
                Toasty.success(MapActivity.this, "Point d'intérêt créé.", Toast.LENGTH_SHORT, true).show();
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
            //course not launched (burger Menu for route list)
            /* Launch activity to see all routes */
            Intent intent = new Intent(MapActivity.this, AllRoutesActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Handles the start and stop of route recording.
     * @param view The view that was clicked.
     */
    @SuppressLint("ResourceType")
    public void onRunButtonClick(View view) {
        if (isOngoing) {
            openPopupPause();
        } else {
            // Start recording
            imgAccount.setVisibility(View.GONE);
            btnRun.setImageResource(R.drawable.ic_pause);
            imgListRoute.setImageResource(R.drawable.vector);
            isOngoing = true;
            isStarted = true;
            startDate = LocalDateTime.now();
            List<GeoPoint> geoPoints = new ArrayList<>(); // Points de la route
            elevationGain = 0;
            elevationLoss = 0;
            setupLocation();
            line = new Polyline(); // Create a new polyline
            line.setColor(Color.RED);

            listPointOfInterests = new ArrayList<Route.PointOfInterest>();
            line.setPoints(geoPoints);
            mapView.getOverlays().add(line);
            mapView.invalidate(); // Refresh map
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_to_bot);
            datasBlock.setVisibility(View.VISIBLE);
            datasBlock.startAnimation(animation);
            startTimer();
            kilometersRun = 0;
            String strValue = String.format("%.2f", kilometersRun);
            kilometers.setText(strValue + " km");
            Toasty.info(MapActivity.this,"L'enregistrement vient de démarrer",Toast.LENGTH_SHORT, true).show();

        }
    }

    /**
     * Opens a pause dialog that allows the user to stop and save the current route.
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
            Log.e("points", listPointOfInterests.toString());
            // Get informations of route
            EditText title = dialogView.findViewById(R.id.editTextTitle);
            EditText description = dialogView.findViewById(R.id.editTextDescription);
            LocalDateTime endDate = LocalDateTime.now();
            Route route =
                    new Route(null,
                            null, // Initialized userId in API
                            startDate.toString(),
                            endDate.toString(),
                            RouteUtils.polylineToLineString(line),
                            listPointOfInterests,
                            RouteUtils.generateTitle(title.getText().toString(), endDate),
                            description.getText().toString(),
                            elevationGain,
                            elevationLoss,
                            timeForOneKilometer.getText().toString(),
                            textViewTimer.getText().toString(),
                            kilometers.getText().toString());


            ApiUtils.saveRoute(this, route, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (isStarted) {
                        Toasty.success(MapActivity.this,  getText(R.string.route_register), Toast.LENGTH_SHORT, true).show();
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
                    Toasty.error(MapActivity.this, "Erreur : " + error + " " + error.getMessage(), Toast.LENGTH_SHORT, true).show();
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
     * Checks if location services are enabled.
     * @return true if location services are enabled, false otherwise.
     */
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * The Is location dialog showing.
     */
    private boolean isLocationDialogShowing = false;

    /**
     * Prompts the user to enable location services if they are disabled.
     */
    private void promptEnableLocation() {
        if (!isLocationDialogShowing) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Localisation désactivée");
            builder.setMessage("Cette application nécessite l'accès à votre localisation. Veuillez activer la localisation.");
            builder.setPositiveButton("Paramètres de localisation", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isLocationDialogShowing = false;
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isLocationDialogShowing = false;
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    isLocationDialogShowing = false;
                }
            });
            builder.show();
            isLocationDialogShowing = true;
        }
    }


    /**
     * Requests the current location and updates the UI accordingly.
     */
    private void setupLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        lastAltitude = location.getAltitude();
                        updateMarker(location);
                    }
                })
                .addOnFailureListener(this, e -> {
                    // Gérez l'échec ici
                });
        }
    }

    /**
     * Updates the location marker on the map based on the provided location.
     * @param location The new location.
     */
    private void updateMarker(Location location) {
        GeoPoint newLocation = new GeoPoint(location.getLatitude(), location.getLongitude());

        // Manamgment line overlay
        if (line != null && isStarted) {
            line.addPoint(newLocation);
            mapView.invalidate(); // Refresh map
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
                speed = strValue + "/km";
                timeForOneKilometer.setText(strValue + " /km");



                double kcalBurn = weight * kilometersRun * CONSTANTE_CALCUL_KCAL;
                strValue = String.format("%.2f", kcalBurn);
                kilocal.setText(strValue + " Kcal");

                if (lastAltitude < location.getAltitude()) {
                    elevationGain += location.getAltitude() - lastAltitude;
                } else {
                    elevationLoss += lastAltitude - location.getAltitude();
                }
                lastAltitude = location.getAltitude();
            }
        }

        if (currentLocationMarker == null) {
            currentLocationMarker = new Marker(mapView);
            currentLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(currentLocationMarker);
        }
        currentLocationMarker.setPosition(newLocation);
        mapController.animateTo(newLocation);

        // Configuration marker icon
        Drawable icon = getResources().getDrawable(R.drawable.ic_direct_location, getApplicationContext().getTheme());
        Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
        Drawable resizedIcon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true)); // Redimensionner à 50x50 pixels
        currentLocationMarker.setIcon(resizedIcon);

        mapView.invalidate(); // Refresh map
    }

    /**
     * Animates the data block to disappear after tracking stops.
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
     * Starts the timer for tracking duration.
     */
    private void startTimer() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (!isLocationEnabled()) {
                    promptEnableLocation();
                }
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
     * Pauses the timer.
     */
    private void pauseTimer() {
        if (!timerInPause) {
            timerInPause = true;
            handler.removeCallbacks(runnable);
        }
    }

    /**
     * Restart the timer.
     */
    private void restartTimer() {
        if (timerInPause) {
            timerInPause = false;
            handler.postDelayed(runnable, 1000);
        }
    }

    /**
     * Reinitialiser the timer.
     */
    private void reinitialiserTimer() {
        timeSpendMillisecond = -1;
        textViewTimer.setText(INITIALISE_TIME);
        handler.removeCallbacks(runnable);
    }

    /**
     * Calculates the distance between two points.
     *
     * @param coord1 First GeoPoint.
     * @param coord2 Second GeoPoint.
     * @return The distance in kilometers.
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
            // Get the compass angle
            float azimuth = event.values[0];

            // Rotate the map according to the compass angle
            mapView.setMapOrientation(-azimuth);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
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
        handler.removeCallbacksAndMessages(null);
        isStarted = false;
    }
}
