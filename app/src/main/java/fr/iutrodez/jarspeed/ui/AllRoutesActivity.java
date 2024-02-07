package fr.iutrodez.jarspeed.ui;

import android.app.Dialog;
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

    @Override
    public void showEditRoutePopup(Route route) {
        // Assombrir l'arrière-plan
        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.2f; // Assombrir l'arrière-plan
        getWindow().setAttributes(params);

        // Construit la boîte de dialogue
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.edit_route_dialog, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        // Initialisation des composants du dialogue
        EditText editTextTitle = dialogView.findViewById(R.id.editTextTitle);
        EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        TextView textViewStartDate = dialogView.findViewById(R.id.textViewStartDate);
        TextView textViewEndDate = dialogView.findViewById(R.id.textViewEndDate);
        TextView textViewPointsOfInterest = dialogView.findViewById(R.id.textViewPointsOfInterest);
        Button buttonSave = dialogView.findViewById(R.id.buttonSave);

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

        // Gestion du clic sur le bouton Enregistrer
        buttonSave.setOnClickListener(v -> {
            String newTitle = editTextTitle.getText().toString().trim();
            String newDescription = editTextDescription.getText().toString().trim();

            // Construction de l'objet de requête
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("title", newTitle);
            paramsMap.put("description", newDescription);
            // Ajoutez ici plus de champs si nécessaire
            JSONObject parameters = new JSONObject(paramsMap);

            // Remplacer 'localhost' par '10.0.2.2' pour l'émulateur Android Studio ou par l'adresse IP de votre serveur
            String baseUrl = "http://10.0.2.2:8080/routes/";
            String url = baseUrl + route.getId(); // Assurez-vous que l'ID est correctement attaché à l'URL

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, parameters, response -> {
                // Le code de réponse est géré par Volley, afficher un toast directement en cas de succès
                loadAllRoutes(); // Assurez-vous que cette méthode rafraîchit correctement l'affichage des routes
                Toast.makeText(getApplicationContext(), "Votre parcours a été mis à jour avec succès", Toast.LENGTH_SHORT).show();
                dialog.dismiss(); // Fermeture de la popup après enregistrement
            }, error -> {
                // Gestion des erreurs
                Toast.makeText(getApplicationContext(), "Erreur de réseau: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    // Ajoutez ici tout en-tête supplémentaire nécessaire, par exemple un token d'authentification
                    return headers;
                }
            };

            // Ajout de la requête à la file d'attente de Volley
            Volley.newRequestQueue(this).add(jsonObjectRequest);
        });

        // Restauration de l'arrière-plan à la fermeture du dialogue
        dialog.setOnDismissListener(d -> {
            params.alpha = 1.0f; // Restaurer la transparence
            getWindow().setAttributes(params);
        });

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

                        // Définir le formateur pour le parsing des dates
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

                        if(routeObject.has("startDate") && !routeObject.isNull("startDate")) {
                            try {
                                String startDateStr = routeObject.getString("startDate");
                                // Parsing de la chaîne de caractères en LocalDateTime
                                LocalDateTime startDate = LocalDateTime.parse(startDateStr, formatter);
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
                                LocalDateTime endDate = LocalDateTime.parse(endDateStr, formatter);
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
                    Log.e("LoadRoutes", "Error parsing JSON", e);
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

}