package fr.iutrodez.jarspeed.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jarspeed.R;

import java.util.ArrayList;
import java.util.List;

import fr.iutrodez.jarspeed.model.Route;
import fr.iutrodez.jarspeed.utils.RouteAdapter;


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
        routeList.add(new Route("Titre 1", "01/01/2024", "10:00"));
        routeList.add(new Route("Titre 2", "01/01/2024", "10:00"));
        routeList.add(new Route("Titre 3", "01/01/2024", "10:00"));

        //TODO
        //BOucler pour ajouter dans routelist tout les parcours

        adapter = new RouteAdapter(routeList);
        recyclerView.setAdapter(adapter);
    }


    //Code pour afficher une popup si on clique sur un item de la recyclerview


}