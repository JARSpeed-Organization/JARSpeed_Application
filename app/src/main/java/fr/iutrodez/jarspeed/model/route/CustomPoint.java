package fr.iutrodez.jarspeed.model.route;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Custom point.
 */
public class CustomPoint {
    /**
     * The Type.
     */
    private final String TYPE = "Point";
    /**
     * The Coordinates.
     */
    private List<Double> coordinates;

    /**
     * Instantiates a new Custom point.
     */
    public CustomPoint() {

    }

    /**
     * Instantiates a new Custom point.
     *
     * @param coordinates the coordinates
     */
    public CustomPoint(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Instantiates a new Custom point.
     *
     * @param pLongitude the p longitude
     * @param pLatitude  the p latitude
     */
    public CustomPoint(Double pLongitude, Double pLatitude) {
        coordinates = new ArrayList<>();
        coordinates.add(pLongitude);
        coordinates.add(pLatitude);
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getTYPE() {
        return TYPE;
    }

    /**
     * Sets type.
     *
     * @param pType the p type
     */
    public void setTYPE(String pType) {
        // Empty method (JPA)
    }

    /**
     * Gets coordinates.
     *
     * @return the coordinates
     */
    public List<Double> getCoordinates() {
        return coordinates;
    }

    /**
     * Sets coordinates.
     *
     * @param pCoordinates the p coordinates
     */
    public void setCoordinates(List<Double> pCoordinates) {
        coordinates = pCoordinates;
    }
}