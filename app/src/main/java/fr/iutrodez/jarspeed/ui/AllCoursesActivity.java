package fr.iutrodez.jarspeed.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.jarspeed.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import fr.iutrodez.jarspeed.model.Route.Route;
import fr.iutrodez.jarspeed.network.ApiUtils;
import fr.iutrodez.jarspeed.utils.RouteAdapter;
import fr.iutrodez.jarspeed.utils.SharedPreferencesManager;


public class AllCoursesActivity extends AppCompatActivity {

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
        // Ajouter des données de test à routeList
        // Exemple : routeList.add(new Route("Titre 1", "01/01/2024", "10:00"));
        //routeList.add(new Route("Titre 1", "01/01/2024", "10:00"));
        //routeList.add(new Route("Titre 2", "01/01/2024", "10:00"));
        routeList.add(new Route("Titre 3", "01/01/2024", "10:00"));

        //TODO Boucler pour ajouter dans routelist tout les parcours
        loadAllRoutes();

        adapter = new RouteAdapter(routeList);
        recyclerView.setAdapter(adapter);
    }


    //TODO Code pour afficher une popup si on clique sur un item de la recyclerview
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
                    //JSONObject jsonResponse = new JSONObject(response);

                    JSONArray jsonArray = new JSONArray(response);

                    Log.d("reponse json", jsonArray.toString());

                    // TODO Mise à jour du recyclerview avec les données recues


                } catch (JSONException e) {
                    Log.e("LoadRoutes", "Error parsing JSON", e);
                    Toast.makeText(AllCoursesActivity.this, "Erreur lors du chargement des données", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LoadRoutes", "Error loading routes: " + error.toString());
                Toast.makeText(AllCoursesActivity.this, "Erreur lors du chargement des parcours", Toast.LENGTH_SHORT).show();
            }
        });
    }

}