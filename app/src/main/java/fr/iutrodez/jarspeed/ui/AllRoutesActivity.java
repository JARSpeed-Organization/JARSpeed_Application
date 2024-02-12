package fr.iutrodez.jarspeed.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

        // On charge les routes dans routeList
        loadAllRoutes();

        adapter = new RouteAdapter(routeList, this);
        recyclerView.setAdapter(adapter);
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
        return dialog;
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
        String newTitle = ((EditText) dialog.findViewById(R.id.editTextTitle)).getText().toString().trim();
        String newDescription = ((EditText) dialog.findViewById(R.id.editTextDescription)).getText().toString().trim();
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("id", route.getId());
        paramsMap.put("title", newTitle);
        paramsMap.put("description", newDescription);
        paramsMap.put("endDate", route.getEndDate().toString());
        paramsMap.put("startDate", route.getStartDate().toString());

        JSONObject parameters = new JSONObject(paramsMap);

        String url = ApiConstants.ROUTE_BASE_URL + "/" + route.getId();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, parameters, response -> {
            loadAllRoutes();
            Toast.makeText(getApplicationContext(), "Votre parcours a été mis à jour avec succès", Toast.LENGTH_SHORT).show();
            dialog.dismiss(); // Fermez directement le dialogue ici
        }, error -> Toast.makeText(getApplicationContext(), "Erreur de réseau: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
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
            Toast.makeText(this, "Vous n'êtes pas connecté.", Toast.LENGTH_SHORT).show();
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

                        routeList.add(route);
                    }

                    // Notifier l'adaptateur du changement de données
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    Toast.makeText(AllRoutesActivity.this, "Erreur lors du chargement des données", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LoadRoutes", "Error loading routes: " + error.toString());
                Toast.makeText(AllRoutesActivity.this, "Erreur lors du chargement des parcours", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void onDeleteRouteClicked(Route route) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmer la suppression")
                .setMessage("Êtes-vous sûr de vouloir supprimer ce parcours ?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Effectuer la requête de suppression à l'API ici
                    String url = ApiConstants.ROUTE_BASE_URL + "/" + route.getId();

                    String token = SharedPreferencesManager.getAuthToken(this);
                    if (token == null || token.isEmpty()) {
                        Toast.makeText(this, "Vous n'êtes pas connecté.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Préparation des écouteurs de réponse et d'erreur
                    Response.Listener<String> responseListener = response -> {
                        // Gérer la réponse réussie ici
                        Toast.makeText(this, "Parcours supprimé avec succès.", Toast.LENGTH_SHORT).show();
                        loadAllRoutes();
                    };
                    Response.ErrorListener errorListener = error -> {
                        // Gérer l'erreur ici
                        Toast.makeText(this, "Erreur lors de la suppression du parcours.", Toast.LENGTH_SHORT).show();
                    };

                    // Effectuer la requête de suppression
                    ApiUtils.deleteRoute(this, url, responseListener, errorListener);

                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    /**
     * On home button click.
     * @param view the view
     */
    public void onHomeButtonClick(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}