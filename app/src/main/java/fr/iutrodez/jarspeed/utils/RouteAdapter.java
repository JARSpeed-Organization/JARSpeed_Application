package fr.iutrodez.jarspeed.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jarspeed.R;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.iutrodez.jarspeed.model.route.Route;

/**
 * Adapter for displaying a list of routes in a RecyclerView.
 * Supports filtering and sorting routes by date.
 */
public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    /**
     * The Original routes.
     */
    private List<Route> originalRoutes;
    /**
     * The Filtered routes.
     */
    private List<Route> filteredRoutes;

    /**
     * The Listener.
     */
    private OnItemClickListener listener;


    /**
     * Interface for handling clicks on route items.
     */
    public interface OnItemClickListener {
        /**
         * Show edit route popup.
         *
         * @param route the route
         */
        void showEditRoutePopup(Route route);

        /**
         * On delete route clicked.
         *
         * @param route the route
         */
        void onDeleteRouteClicked(Route route);

    }

    /**
     * Constructor for RouteAdapter.
     *
     * @param pOriginalRoutes The initial list of routes to display.
     * @param listener        Listener for item click events.
     */
    public RouteAdapter(List<Route> pOriginalRoutes, OnItemClickListener listener) {
        this.originalRoutes = pOriginalRoutes;
        this.listener = listener;
        this.filteredRoutes = new ArrayList<>(originalRoutes);
    }

    /**
     * Filters the list of routes based on a query string.
     *
     * @param query The string to filter the routes by.
     */
    public void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            filteredRoutes = new ArrayList<>(originalRoutes);
        } else {
            filteredRoutes = new ArrayList<>();
            for (Route route : originalRoutes) {
                if (route.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredRoutes.add(route); // Add matching routes to the filtered list
                }
            }
        }
        notifyDataSetChanged(); // Refresh the display
    }

    /**
     * Sorts the displayed routes in ascending order by start date.
     */
    public void sortAscending() {
        Collections.sort(filteredRoutes, new Comparator<Route>() {
            @Override
            public int compare(Route route1, Route route2) {
                LocalDateTime date1 = LocalDateTime.parse(route1.getStartDate());
                LocalDateTime date2 = LocalDateTime.parse(route2.getStartDate());
                return date1.compareTo(date2);
            }
        });
        notifyDataSetChanged();
    }

    /**
     * Sorts the displayed routes in descending order by start date.
     */
    public void sortDescending() {
        Collections.sort(filteredRoutes, new Comparator<Route>() {
            @Override
            public int compare(Route route1, Route route2) {
                LocalDateTime date1 = LocalDateTime.parse(route1.getStartDate());
                LocalDateTime date2 = LocalDateTime.parse(route2.getStartDate());
                return date2.compareTo(date1);
            }
        });
        notifyDataSetChanged();
    }

    /**
     * On create view holder route view holder.
     *
     * @param parent   the parent
     * @param viewType the view type
     * @return the route view holder
     */
    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.route_item, parent, false);
        return new RouteViewHolder(itemView);
    }

    /**
     * On bind view holder.
     *
     * @param holder   the holder
     * @param position the position
     */
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

    /**
     * Gets item count.
     *
     * @return the item count
     */
    @Override
    public int getItemCount() {
        return filteredRoutes.size();
    }

    /**
     * The type Route view holder.
     */
    public static class RouteViewHolder extends RecyclerView.ViewHolder {
        /**
         * The Title.
         */
        public TextView title;
        /**
         * The Delete button.
         */
        public ImageView deleteButton;

        /**
         * Instantiates a new Route view holder.
         *
         * @param view the view
         */
        public RouteViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            deleteButton = view.findViewById(R.id.deleteButton);
        }
    }
}
