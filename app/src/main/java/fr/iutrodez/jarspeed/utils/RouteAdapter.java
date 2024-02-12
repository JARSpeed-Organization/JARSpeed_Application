package fr.iutrodez.jarspeed.utils;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jarspeed.R;

import java.util.List;

import fr.iutrodez.jarspeed.model.route.Route;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    private List<Route> routeList;

    private OnItemClickListener listener;

    public ImageButton deleteButton;

    public interface OnItemClickListener {
        void showEditRoutePopup(Route route);
        void onDeleteRouteClicked(Route route);

    }

    public RouteAdapter(List<Route> routeList, OnItemClickListener listener) {
        this.routeList = routeList;
        this.listener = listener;
    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.route_item, parent, false);
        return new RouteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RouteViewHolder holder, int position) {
        Route route = routeList.get(position);
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
        return routeList.size();
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
