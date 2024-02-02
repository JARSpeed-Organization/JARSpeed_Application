package fr.iutrodez.jarspeed.model;

/**
 * The type Coordinate.
 */
public class Coordinate {
    /**
     * The Latitude.
     */
    private double latitude;
    /**
     * The Longitude.
     */
    private double longitude;

    /**
     * Instantiates a new Coordinate.
     *
     * @param pLatitude  the p latitude
     * @param pLongitude the p longitude
     */
    public Coordinate(double pLatitude, double pLongitude) {
        latitude = pLatitude;
        longitude = pLongitude;
    }

    /**
     * Gets latitude.
     *
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets latitude.
     *
     * @param pLatitude the latitude
     */
    public void setLatitude(final double pLatitude) {
        this.latitude = pLatitude;
    }

    /**
     * Gets longitude.
     *
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets longitude.
     *
     * @param pLongitude the longitude
     */
    public void setLongitude(final double pLongitude) {
        this.longitude = pLongitude;
    }
}