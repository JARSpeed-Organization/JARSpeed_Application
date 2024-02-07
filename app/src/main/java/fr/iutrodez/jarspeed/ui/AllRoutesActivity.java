package fr.iutrodez.jarspeed.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.jarspeed.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    public void onItemClick(Route route) {
        StringBuilder message = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        if (route.getTitle() != null && !route.getTitle().isEmpty()) {
            message.append("Titre: ").append(route.getTitle()).append("\n");
        } else {
            message.append("Titre: Non spécifié\n");
        }

        if (route.getDescription() != null && !route.getDescription().isEmpty()) {
            message.append("Description: ").append(route.getDescription()).append("\n");
        } else {
            message.append("Description: Non spécifiée\n");
        }

        if (route.getStartDate() != null) {
            message.append("Date de début: ").append(sdf.format(route.getStartDate())).append("\n");
        } else {
            message.append("Date de début: Non spécifiée\n");
        }

        if (route.getEndDate() != null) {
            message.append("Date de fin: ").append(sdf.format(route.getEndDate())).append("\n");
        } else {
            message.append("Date de fin: Non spécifiée\n");
        }

        // Chemin
        if (route.getPath() != null && !route.getPath().isEmpty()) {
            message.append("Chemin:\n");
            for (Route.Coordinate coordinate : route.getPath()) {
                message.append("- Latitude: ").append(coordinate.getLatitude())
                        .append(", Longitude: ").append(coordinate.getLongitude()).append("\n");
            }
        } else {
            message.append("Chemin: Non spécifié\n");
        }

        // Points d'intérêt
        if (route.getPointsOfInterest() != null && !route.getPointsOfInterest().isEmpty()) {
            message.append("Points d'intérêt:\n");
            for (Route.PointOfInterest poi : route.getPointsOfInterest()) {
                message.append("- Nom: ").append(poi.getName())
                        .append(", Latitude: ").append(poi.getCoordinates().getLatitude())
                        .append(", Longitude: ").append(poi.getCoordinates().getLongitude()).append("\n");
            }
        } else {
            message.append("Points d'intérêt: Non spécifiés\n");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Détails de la Route");
        builder.setMessage(message.toString());
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
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
                        String titre = routeObject.getString("title");
                        Route route = new Route(titre);
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