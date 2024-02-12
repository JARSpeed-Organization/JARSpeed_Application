package fr.iutrodez.jarspeed.utils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jarspeed.R;

import java.util.ArrayList;
import java.util.List;

import fr.iutrodez.jarspeed.model.route.Route;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    private List<Route> originalRoutes;
    private List<Route> filteredRoutes;

    private OnItemClickListener listener;

    public ImageButton deleteButton;

    public interface OnItemClickListener {
        void showEditRoutePopup(Route route);
        void onDeleteRouteClicked(Route route);

    }

    public RouteAdapter(List<Route> pOriginalRoutes, OnItemClickListener listener) {
        this.originalRoutes = pOriginalRoutes;
        this.listener = listener;
        this.filteredRoutes = new ArrayList<>(originalRoutes);
    }

    public void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            Log.e("filtre", "Vide");
            Log.e("original", originalRoutes.toString());
            filteredRoutes = new ArrayList<>(originalRoutes);
        } else {
            filteredRoutes = new ArrayList<>();
            Log.e("filtre", "Plein");
            for (Route route : originalRoutes) {
                if (route.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredRoutes.add(route); // Ajouter l'élément à la liste filtrée si le nom correspond à la requête
                }
            }
        }
        Log.e("filtre", filteredRoutes.toString());
        notifyDataSetChanged(); // Actualiser l'affichage
    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.route_item, parent, false);
        return new RouteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RouteViewHolder holder, int position) {
        Route route = filteredRoutes.get(position);
        holder.title.setText(route.getTitle());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.showEditRoutePopup(route);
            }
        });
        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteRouteClicked(route);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredRoutes.size();
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView deleteButton;

        public RouteViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            deleteButton = view.findViewById(R.id.deleteButton);
        }
    }
}
