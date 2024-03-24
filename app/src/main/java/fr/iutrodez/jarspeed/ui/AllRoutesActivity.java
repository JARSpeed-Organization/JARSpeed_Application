package fr.iutrodez.jarspeed.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.jarspeed.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.Marker;
import org.w3c.dom.Text;

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
import fr.iutrodez.jarspeed.model.route.CustomPoint;
import fr.iutrodez.jarspeed.model.route.Route;
import fr.iutrodez.jarspeed.network.ApiConstants;
import fr.iutrodez.jarspeed.network.ApiUtils;
import fr.iutrodez.jarspeed.utils.RouteAdapter;
import fr.iutrodez.jarspeed.utils.SharedPreferencesManager;

/**
 * The type All routes activity.
 */
public class AllRoutesActivity extends AppCompatActivity implements RouteAdapter.OnItemClickListener {

    /**
     * The Recycler view.
     */
    private RecyclerView recyclerView;
    /**
     * The Adapter.
     */
    private RouteAdapter adapter;
    /**
     * The Route list.
     */
    private List<Route> routeList;

    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
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
        EditText filterTitle = findViewById(R.id.filterTitle); // Pour récupérer l'EditText de filtrage
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


    /**
     * Darkens the background of the activity. This is typically used to visually emphasize a dialog or a popup
     * by reducing the brightness of the rest of the UI.
     */
    private void darkenBackground() {
        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.2f;
        getWindow().setAttributes(params);
    }


    /**
     * Restores the background brightness when a dialog is dismissed. It is typically used in conjunction with
     * {@link #darkenBackground()} to reset the UI appearance after a dialog is closed.
     *
     * @param dialog The dialog whose dismissal will trigger the restoration of the background brightness.
     */
    private void restoreBackgroundOnDismiss(AlertDialog dialog) {
        dialog.setOnDismissListener(d -> {
            final WindowManager.LayoutParams params = getWindow().getAttributes();
            params.alpha = 1.0f;
            getWindow().setAttributes(params);
        });
    }


    /**
     * Creates and configures an AlertDialog for editing a route's details. This dialog includes functionality
     * to edit the route's title, description, and other properties, with changes being savable.
     * Additionally, this method configures the dialog to darken the background and to restore it upon dismissal.
     *
     * @param route The route object to be edited.
     * @return An AlertDialog instance ready to be shown.
     */
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


    /**
     * Sets up a miniature map (minimap) in the edit route dialog to display the route's path and points of interest.
     * This method is responsible for configuring the map view within the dialog, including setting the zoom level,
     * adding polyline to represent the route path, and markers for each point of interest.
     *
     * @param dialogView The view of the dialog where the minimap is to be set up.
     * @param route The route object whose details are to be displayed on the minimap.
     */
    private void setupMinimap(View dialogView, Route route) {
        // Assurez-vous que la route et son chemin ne sont pas nuls
        if (route == null || route.getPath().getCoordinates() == null || route.getPath().getCoordinates().isEmpty()) {
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
        mapController.setZoom(17.0);

        Polyline line = new Polyline();
        line.setColor(Color.RED);
        line.setWidth(2.0f);

        List<GeoPoint> geoPoints = new ArrayList<>();
        for (List<Double> coord : route.getPath().getCoordinates()) {
            geoPoints.add(new GeoPoint(coord.get(1), coord.get(0)));
        }

        line.setPoints(geoPoints);
        miniMapView.getOverlays().add(line);

        // Centrez la carte sur le premier point du parcours
        if (!geoPoints.isEmpty()) {
            mapController.setCenter(geoPoints.get(0));
        }

        for (Route.PointOfInterest poi : route.getPointsOfInterest()) {

            CustomPoint coorPoi = poi.getCoordinates();
            Marker marker = new Marker(miniMapView);
            marker.setPosition(new GeoPoint(coorPoi.getCoordinates().get(1), coorPoi.getCoordinates().get(0)));
            marker.setTitle(poi.getName());
            Drawable customIcon = ContextCompat.getDrawable(ctx, R.drawable.custom_marker);
            marker.setIcon(customIcon);

            // Ajoutez le marqueur à la carte
            miniMapView.getOverlays().add(marker);
        }

        miniMapView.invalidate(); // Rafraîchissez la carte pour afficher les modifications
    }


    /**
     * Configures the fields of the edit route dialog with the current data of the route.
     * This includes setting the title, description, start and end dates, and elevation gain and loss.
     *
     * @param dialogView The view of the dialog containing the fields to be configured.
     * @param route The route object whose data is to fill in the dialog fields.
     */
    private void configureDialogFields(View dialogView, Route route) {
        // Initialisation des composants du dialogue
        EditText editTextTitle = dialogView.findViewById(R.id.editTextTitle);
        EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        TextView textViewStartDate = dialogView.findViewById(R.id.textViewStartDate);
        TextView textViewElevationGain = dialogView.findViewById(R.id.textViewElevationGain);
        TextView textViewElevationLoss = dialogView.findViewById(R.id.textViewElevationLoss);
        TextView textViewDistance = dialogView.findViewById(R.id.textViewDistance);
        TextView textViewTime = dialogView.findViewById(R.id.textViewTime);
        TextView textViewSpeed = dialogView.findViewById(R.id.textViewSpeed);

        // Configuration initiale des champs
        editTextTitle.setText(route.getTitle() != null && !route.getTitle().isEmpty() ? route.getTitle() : "");
        editTextDescription.setText(route.getDescription() != null && !route.getDescription().isEmpty() ? route.getDescription() : "");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm", Locale.FRANCE);
        textViewStartDate.setText(route.getStartDate() != null ? LocalDateTime.parse(route.getStartDate()).format(formatter) : "Non spécifiée");
        textViewElevationGain.setText(route.getElevationGain() != null ? String.format ("%.2f", route.getElevationGain()) + " m" : "Non spécifiée");
        textViewElevationLoss.setText(route.getElevationLoss() != null ? String.format ("%.2f", route.getElevationLoss()) + " m" : "Non spécifiée");
        textViewDistance.setText(route.getDistance());
        textViewTime.setText(route.getTime());
        textViewSpeed.setText(route.getSpeed());
       }

    /**
     * Sets a click listener for the save button in the edit route dialog.
     * This listener triggers the update of the route's details with the new values entered by the user.
     *
     * @param dialog The dialog containing the save button.
     * @param dialogView The view of the dialog containing the fields to extract the updated route details from.
     * @param route The route object to be updated.
     */
    private void setupSaveButtonListener(AlertDialog dialog, View dialogView, Route route) {
        Button buttonSave = dialogView.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(v -> updateRoute(dialog, route));
    }

    /**
     * Sets a click listener for the cancel button in the edit route dialog.
     * This listener simply dismisses the dialog without making any changes to the route's details.
     *
     * @param dialogView The view of the dialog containing the cancel button.
     * @param dialog The dialog to be dismissed upon clicking the cancel button.
     */
    private void setupCancelButton(View dialogView, AlertDialog dialog) {
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancelEditRoute);
        buttonCancel.setOnClickListener(v -> dialog.dismiss());
    }

    /**
     * Updates the route details based on the user's input and sends the update to the server.
     * Upon successful update, it reloads the list of routes to reflect the changes and notifies the user of the success.
     *
     * @param dialog The dialog containing the editable fields for the route details.
     * @param route The route object to be updated.
     */
    private void updateRoute(AlertDialog dialog, Route route) {
        // Récupération des nouvelles valeurs
        String newTitle = ((EditText) dialog.findViewById(R.id.editTextTitle)).getText().toString().trim();
        String newDescription = ((EditText) dialog.findViewById(R.id.editTextDescription)).getText().toString().trim();
        // Mise à jour des champs modifiables
        route.setTitle(newTitle);
        route.setDescription(newDescription);
        // Préparation de l'objet JSON pour la requête
        ApiUtils.updateRoute(this, route, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadAllRoutes();
                Toasty.success(getApplicationContext(), "Votre parcours a été mis à jour avec succès", Toast.LENGTH_SHORT, true).show();
                dialog.dismiss(); // Fermez directement le dialogue ici
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(getApplicationContext(), "Erreur de réseau: " + error.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    /**
     * Displays the edit route dialog for a given route.
     * This method is responsible for both darkening the background to focus on the dialog
     * and for creating and displaying the dialog itself using {@link #createEditRouteDialog(Route)}.
     *
     * @param route The route object to be edited.
     */
    @Override
    public void showEditRoutePopup(Route route) {
        // Assombrir l'arrière-plan
        darkenBackground();
        createEditRouteDialog(route);
        AlertDialog dialog = createEditRouteDialog(route);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    /**
     * Loads all available routes from the server and updates the UI accordingly.
     * This method sends a network request to fetch all routes. On successful response,
     * it updates the `routeList` and notifies the adapter to refresh the display. It handles
     * any errors or issues with connectivity by displaying an appropriate message to the user.
     * Authentication token is required to access the routes, and the user is notified if not logged in.
     */
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

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    // Boucle pour ajouter chaque parcours dans la liste
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Route route = objectMapper.readValue(jsonArray.getJSONObject(i).toString(), Route.class);
                        Log.e("route", route.toString());
                        routeList.add(route);
                    }

                    // Une fois les données chargées avec succès, configurez l'adaptateur et définissez-le sur le RecyclerView
                    adapter = new RouteAdapter(routeList, AllRoutesActivity.this);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    Toasty.error(AllRoutesActivity.this, "Erreur lors du chargement des données", Toast.LENGTH_SHORT, true).show();
                } catch (JsonMappingException pE) {
                    throw new RuntimeException(pE);
                } catch (JsonProcessingException pE) {
                    throw new RuntimeException(pE);
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


    /**
     * Presents a confirmation dialog to delete a specific route.
     * This method creates and displays a dialog asking the user to confirm the deletion of a route.
     * On confirmation, it sends a request to the server to delete the route and refreshes the list of routes.
     * It handles user cancellation by simply dismissing the dialog without any further action.
     *
     * @param route The route to be deleted.
     */
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
     * Navigates the user back to the home screen (map view).
     * This method is called when the home button is clicked. It creates an intent to start
     * the `MapActivity` and navigates the user to the home screen of the application.
     *
     * @param view The view that was clicked to trigger this method.
     */
    public void onHomeButtonClick(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    /**
     * Initiates sorting of the displayed routes in ascending order based on their start date.
     * This method is called when the sort ascending button is clicked. It triggers the adapter
     * to sort the current list of routes in ascending order by their start date.
     *
     * @param view The view that was clicked to trigger this method.
     */
    public void onSortAscendingButtonClick(View view) {
        adapter.sortAscending();
    }

    /**
     * Initiates sorting of the displayed routes in descending order based on their start date.
     * This method is called when the sort descending button is clicked. It triggers the adapter
     * to sort the current list of routes in descending order by their start date.
     *
     * @param view The view that was clicked to trigger this method.
     */
    public void onSortDescendingButtonClick(View view) {
        adapter.sortDescending();
    }
}