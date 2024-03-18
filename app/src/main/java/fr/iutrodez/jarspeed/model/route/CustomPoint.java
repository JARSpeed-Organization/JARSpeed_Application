package fr.iutrodez.jarspeed.model.route;

import java.util.ArrayList;
import java.util.List;

public class CustomPoint {
    private final String TYPE = "Point";
    private List<Double> coordinates;

    public CustomPoint() {

    }

    public CustomPoint(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public CustomPoint(Double pLongitude, Double pLatitude) {
        coordinates = new ArrayList<>();
        coordinates.add(pLongitude);
        coordinates.add(pLatitude);
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String pType) {
        // Empty method (JPA)
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> pCoordinates) {
        coordinates = pCoordinates;
    }
}