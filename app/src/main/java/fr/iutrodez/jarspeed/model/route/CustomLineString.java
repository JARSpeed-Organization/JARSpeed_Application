package fr.iutrodez.jarspeed.model.route;

import java.util.ArrayList;
import java.util.List;

public final class CustomLineString {
    private final String type = "LineString";
    private List<List<Double>> coordinates;

    public CustomLineString() {
    }

    public String getType() {
        return type;
    }

    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<Double>> pCoordinates) {
        coordinates = pCoordinates;
    }

    public void add(Double pLongitude, Double pLatitude) {
        if (coordinates == null) {
            coordinates = new ArrayList<>();
        }
        List<Double> point = new ArrayList<>();
        point.add(pLongitude);
        point.add(pLatitude);
        coordinates.add(point);
    }
}