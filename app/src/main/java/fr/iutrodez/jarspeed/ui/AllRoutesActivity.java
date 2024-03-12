package fr.iutrodez.jarspeed.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.jarspeed.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.Marker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import fr.iutrodez.jarspeed.network.ApiConstants;
import fr.iutrodez.jarspeed.network.ApiUtils;
import fr.iutrodez.jarspeed.utils.RouteAdapter;
import fr.iutrodez.jarspeed.utils.SharedPreferencesManager;

import fr.iutrodez.jarspeed.model.route.Route;

public class AllRoutesActivity extends AppCompatActivity implements RouteAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private RouteAdapter adapter;
    private List<Route> routeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_courses);

        recyclerView = findViewById(R.id.recyclerViewCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        routeList = new ArrayList<>();

        // On charge les routes dans routeList et initialise l'adaptateur
        loadAllRoutes();

        // Ecouteur de texte modifié à l'EditText pour déclencher le filtrage
        EditText filterTitle = findViewById(R.id.filterTitle); // Ajoutez cette ligne pour récupérer l'EditText de filtrage
        filterTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim(); // Obtenir le texte saisi
                adapter.filter(query); // Appliquer le filtrage avec le texte saisi
            }
        });

        // Création d'un callback pour l'appui sur le bouton de retour
        // désactivation du bouton retour.
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        };
        // Obtention du OnBackPressedDispatcher et ajout du callback
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void darkenBackground() {
        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.2f;
        getWindow().setAttributes(params);
    }

    private void restoreBackgroundOnDismiss(AlertDialog dialog) {
        dialog.setOnDismissListener(d -> {
            final WindowManager.LayoutParams params = getWindow().getAttributes();
            params.alpha = 1.0f;
            getWindow().setAttributes(params);
        });
    }

    private AlertDialog createEditRouteDialog(Route route) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.edit_route_dialog, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        configureDialogFields(dialogView, route);
        // Modification ici pour passer 'dialog' au lieu de 'dialogView'
        setupSaveButtonListener(dialog, dialogView, route);
        setupCancelButton(dialogView, dialog); // Ajoutez cette ligne pour gérer le bouton Annuler

        restoreBackgroundOnDismiss(dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setupMinimap(dialogView, route); // Configurez la minimap ici
        return dialog;
    }

    private void setupMinimap(View dialogView, Route route) {
        // Assurez-vous que la route et son chemin ne sont pas nuls
        if (route == null || route.getPath() == null || route.getPath().isEmpty()) {
            Log.d("setupMinimap", "Parcours ou liste des points est nulle ou vide.");
            return; // Sortez de la méthode si la route ou le chemin est nul ou vide
        }

        MapView miniMapView = dialogView.findViewById(R.id.mapViewMini);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        miniMapView.setTileSource(TileSourceFactory.MAPNIK);
        miniMapView.setBuiltInZoomControls(false);
        miniMapView.setMultiTouchControls(true);

        IMapController mapController = miniMapView.getController();
        mapController.setZoom(15.0);

        Polyline line = new Polyline();
        line.setColor(Color.RED);
        line.setWidth(2.0f);

        List<GeoPoint> geoPoints = new ArrayList<>();
        for (Route.Coordinate coord : route.getPath()) {
            geoPoints.add(new GeoPoint(coord.getLatitude(), coord.getLongitude()));
        }

        line.setPoints(geoPoints);
        miniMapView.getOverlays().add(line);

        // Centrez la carte sur le premier point du parcours
        if (!geoPoints.isEmpty()) {
            mapController.setCenter(geoPoints.get(0));
        }

        for (Route.PointOfInterest poi : route.getPointsOfInterest()) {

            Route.Coordinate coorPoi = poi.getCoordinates();
            Marker marker = new Marker(miniMapView);
            marker.setPosition(new GeoPoint(coorPoi.getLatitude(), coorPoi.getLongitude()));
            marker.setTitle(poi.getName());

            // Ajoutez le marqueur à la carte
            miniMapView.getOverlays().add(marker);
        }

        miniMapView.invalidate(); // Rafraîchissez la carte pour afficher les modifications
    }


    private void configureDialogFields(View dialogView, Route route) {
        // Initialisation des composants du dialogue
        EditText editTextTitle = dialogView.findViewById(R.id.editTextTitle);
        EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        TextView textViewStartDate = dialogView.findViewById(R.id.textViewStartDate);
        TextView textViewEndDate = dialogView.findViewById(R.id.textViewEndDate);
        TextView textViewPointsOfInterest = dialogView.findViewById(R.id.textViewPointsOfInterest);

        // Configuration initiale des champs
        editTextTitle.setText(route.getTitle() != null && !route.getTitle().isEmpty() ? route.getTitle() : "");
        editTextDescription.setText(route.getDescription() != null && !route.getDescription().isEmpty() ? route.getDescription() : "");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy à HH:mm", Locale.FRANCE);
        textViewStartDate.setText(route.getStartDate() != null ? route.getStartDate().format(formatter) : "Non spécifiée");
        textViewEndDate.setText(route.getEndDate() != null ? route.getEndDate().format(formatter) : "Non spécifiée");

        // Construction et affichage des points d'intérêt
        StringBuilder poiBuilder = new StringBuilder();
        if (route.getPointsOfInterest() != null && !route.getPointsOfInterest().isEmpty()) {
            for (Route.PointOfInterest poi : route.getPointsOfInterest()) {
                poiBuilder.append(poi.getName()).append("\n");
            }
        } else {
            poiBuilder.append("Aucun");
        }
        textViewPointsOfInterest.setText(poiBuilder.toString());
    }

    private void setupSaveButtonListener(AlertDialog dialog, View dialogView, Route route) {
        Button buttonSave = dialogView.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(v -> updateRoute(dialog, route));
    }
    private void setupCancelButton(View dialogView, AlertDialog dialog) {
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancelEditRoute);
        buttonCancel.setOnClickListener(v -> dialog.dismiss());
    }
    private void updateRoute(AlertDialog dialog, Route route) {
        // Récupération des nouvelles valeurs
        String newTitle = ((EditText) dialog.findViewById(R.id.editTextTitle)).getText().toString().trim();
        String newDescription = ((EditText) dialog.findViewById(R.id.editTextDescription)).getText().toString().trim();

        // Préparation de l'objet JSON pour la requête
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", route.getId());
            parameters.put("title", newTitle);
            parameters.put("description", newDescription);
            parameters.put("endDate", route.getEndDate().toString());
            parameters.put("startDate", route.getStartDate().toString());

            // Ajout du chemin
            JSONArray pathArray = new JSONArray();
            for (Route.Coordinate coord : route.getPath()) {
                JSONObject coordJSON = new JSONObject();
                coordJSON.put("latitude", coord.getLatitude());
                coordJSON.put("longitude", coord.getLongitude());
                pathArray.put(coordJSON);
            }
            parameters.put("path", pathArray);

            // Ajout des points d'intérêt
            JSONArray poiArray = new JSONArray();
            for (Route.PointOfInterest poi : route.getPointsOfInterest()) {
                JSONObject poiJSON = new JSONObject();
                poiJSON.put("name", poi.getName());
                JSONObject coordJSON = new JSONObject();
                coordJSON.put("latitude", poi.getCoordinates().getLatitude());
                coordJSON.put("longitude", poi.getCoordinates().getLongitude());
                poiJSON.put("coordinates", coordJSON);
                poiArray.put(poiJSON);
            }
            parameters.put("pointsOfInterest", poiArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ApiConstants.ROUTE_BASE_URL + "/" + route.getId();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, parameters, response -> {
            loadAllRoutes();
            Toasty.success(getApplicationContext(), "Votre parcours a été mis à jour avec succès", Toast.LENGTH_SHORT, true).show();
            dialog.dismiss(); // Fermez directement le dialogue ici
        }, error -> Toasty.error(getApplicationContext(), "Erreur de réseau: " + error.getMessage(), Toast.LENGTH_SHORT, true).show())

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    @Override
    public void showEditRoutePopup(Route route) {
        // Assombrir l'arrière-plan
        darkenBackground();
        createEditRouteDialog(route);
        AlertDialog dialog = createEditRouteDialog(route);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private void loadAllRoutes() {
        String token = SharedPreferencesManager.getAuthToken(this);
        if (token == null || token.isEmpty()) {
            Toasty.error(this, "Vous n'êtes pas connecté.", Toast.LENGTH_SHORT, true).show();
            return;
        }

        ApiUtils.loadAllRoutes(this, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    Log.e("reponse", jsonArray.toString());

                    // Vider la liste existante pour éviter les doublons
                    routeList.clear();

                    // Boucle pour ajouter chaque parcours dans la liste
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject routeObject = jsonArray.getJSONObject(i);

                        Route route = new Route();

                        // Supposons que votre objet Route a des setters pour chaque champ
                        if(routeObject.has("id") && !routeObject.isNull("id")) {
                            route.setId(routeObject.getString("id"));
                        }
                        if(routeObject.has("title") && !routeObject.isNull("title")) {
                            route.setTitle(routeObject.getString("title"));
                        }
                        if(routeObject.has("description") && !routeObject.isNull("description")) {
                            route.setDescription(routeObject.getString("description"));
                        }
                        if(routeObject.has("startDate") && !routeObject.isNull("startDate")) {
                            try {
                                String startDateStr = routeObject.getString("startDate");
                                // Parsing de la chaîne de caractères en LocalDateTime
                                LocalDateTime startDate = LocalDateTime.parse(startDateStr);
                                route.setStartDate(startDate);
                            } catch (DateTimeParseException e) {
                                e.printStackTrace();
                                // Gérer l'exception si la date n'est pas au format attendu
                            }
                        }
                        if(routeObject.has("endDate") && !routeObject.isNull("endDate")) {
                            try {
                                String endDateStr = routeObject.getString("endDate");
                                // Parsing de la chaîne de caractères en LocalDateTime

                                LocalDateTime endDate = LocalDateTime.parse(endDateStr);
                                route.setEndDate(endDate);
                            } catch (DateTimeParseException e) {
                                e.printStackTrace();
                                // Gérer l'exception si la date n'est pas au format attendu
                            }
                        }
                        // À l'intérieur de la boucle for, après avoir extrait les autres champs
                        List<Route.Coordinate> path = new ArrayList<>();
                        JSONArray pathArray = routeObject.optJSONArray("path");
                        if (pathArray != null) {
                            for (int j = 0; j < pathArray.length(); j++) {
                                JSONObject pathObject = pathArray.getJSONObject(j);
                                double latitude = pathObject.optDouble("latitude");
                                double longitude = pathObject.optDouble("longitude");
                                Route.Coordinate coordinate = new Route.Coordinate(latitude, longitude);
                                path.add(coordinate);
                            }
                        }
                        route.setPath(path); // Assurez-vous que votre classe Route a une méthode setPath(List<Coordinate> path)

                        List<Route.PointOfInterest> pointsOfInterest = new ArrayList<>();
                        JSONArray poiArray = routeObject.optJSONArray("pointsOfInterest");
                        if (poiArray != null) {
                            for (int k = 0; k < poiArray.length(); k++) {
                                JSONObject poiObject = poiArray.getJSONObject(k);
                                String name = poiObject.optString("name");
                                JSONObject coordObject = poiObject.optJSONObject("coordinates");
                                double latitude = coordObject.optDouble("latitude");
                                double longitude = coordObject.optDouble("longitude");
                                Route.Coordinate coordinate = new Route.Coordinate(latitude, longitude);
                                Route.PointOfInterest poi = new Route.PointOfInterest(name, coordinate);
                                pointsOfInterest.add(poi);
                            }
                        }
                        route.setPointsOfInterest(pointsOfInterest); // Assurez-vous que votre classe Route a une méthode setPointsOfInterest(List<PointOfInterest> pointsOfInterest)


                        routeList.add(route);
                    }

                    // Une fois les données chargées avec succès, configurez l'adaptateur et définissez-le sur le RecyclerView
                    adapter = new RouteAdapter(routeList, AllRoutesActivity.this);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    Toasty.error(AllRoutesActivity.this, "Erreur lors du chargement des données", Toast.LENGTH_SHORT, true).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LoadRoutes", "Error loading routes: " + error.toString());
                Toasty.error(AllRoutesActivity.this, "Erreur lors du chargement des parcours", Toast.LENGTH_SHORT, true).show();
            }
        });
    }


    public void onDeleteRouteClicked(Route route) {
        View dialogView = getLayoutInflater().inflate(R.layout.confirmation_popup, null);

        // Configure le texte de confirmation pour la suppression du parcours
        TextView textViewGeneric = dialogView.findViewById(R.id.editTextGeneric);
        textViewGeneric.setText(getString(R.string.confirm_delete_route));

        // Construit la boîte de dialogue
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        // Gère le clic sur le bouton Annuler
        dialogView.findViewById(R.id.buttonCancelGeneric).setOnClickListener(v -> dialog.dismiss());

        // Gère le clic sur le bouton Confirmer pour la suppression du parcours
        dialogView.findViewById(R.id.buttonConfirmGeneric).setOnClickListener(v -> {
            String url = ApiConstants.ROUTE_BASE_URL + "/" + route.getId();
            String token = SharedPreferencesManager.getAuthToken(this);
            if (token == null || token.isEmpty()) {
                Toasty.error(this, "Vous n'êtes pas connecté.", Toast.LENGTH_SHORT, true).show();

                dialog.dismiss(); // Ferme la boîte de dialogue
                return;
            }

            Response.Listener<String> responseListener = response -> {
                Toasty.success(this, "Parcours supprimé avec succès.", Toast.LENGTH_SHORT, true).show();
                loadAllRoutes(); // Recharge les parcours après suppression
                dialog.dismiss(); // Ferme la boîte de dialogue
            };

            Response.ErrorListener errorListener = error -> {
                Toasty.error(this, "Erreur lors de la suppression du parcours.", Toast.LENGTH_SHORT, true).show();
                dialog.dismiss(); // Ferme la boîte de dialogue
            };

            ApiUtils.deleteRoute(this, url, responseListener, errorListener);
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    /**
     * On home button click.
     * @param view the view
     */
    public void onHomeButtonClick(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    /**
     * Sort ascending on start date for all routes.
     * @param view the view
     */
    public void onSortAscendingButtonClick(View view) {
        adapter.sortAscending();
    }

    /**
     * Sort descending on start date for all routes.
     * @param view the view
     */
    public void onSortDescendingButtonClick(View view) {
        adapter.sortDescending();
    }

    @Override
    public void onBackPressed() {
        // Laissez vide pour ne rien faire sur le pression du bouton Retour
        // Ou ajoutez votre propre logique ici
    }
}