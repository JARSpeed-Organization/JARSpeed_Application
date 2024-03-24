package fr.iutrodez.jarspeed.model.route;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Custom line string.
 */
public final class CustomLineString {
    /**
     * The Type.
     */
    private final String type = "LineString";
    /**
     * The Coordinates.
     */
    private List<List<Double>> coordinates;

    /**
     * Instantiates a new Custom line string.
     */
    public CustomLineString() {
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets coordinates.
     *
     * @return the coordinates
     */
    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    /**
     * Sets coordinates.
     *
     * @param pCoordinates the p coordinates
     */
    public void setCoordinates(List<List<Double>> pCoordinates) {
        coordinates = pCoordinates;
    }

    /**
     * Add.
     *
     * @param pLongitude the p longitude
     * @param pLatitude  the p latitude
     */
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