package fr.iutrodez.jarspeed.model;

import java.util.Date;
import java.util.List;

public class Route {
    /**
     * The Id.
     */

    private String id;
    /**
     * The Date.
     */
    private Date date;
    /**
     * The Start point.
     */
    private Coordinate startPoint;
    /**
     * The End point.
     */
    private Coordinate endPoint;
    /**
     * The Path.
     */
    private List<Coordinate> path;
    /**
     * The Points of interest.
     */
    private List<PointOfInterest> pointsOfInterest;
    /**
     * The Title.
     */
    private String title;
    /**
     * The Description.
     */
    private String description;

    // Getters and Setters

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param pId the id
     */
    public void setId(final String pId) {
        this.id = pId;
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets date.
     *
     * @param pDate the date
     */
    public void setDate(final Date pDate) {
        this.date = pDate;
    }

    /**
     * Gets start point.
     *
     * @return the start point
     */
    public Coordinate getStartPoint() {
        return startPoint;
    }

    /**
     * Sets start point.
     *
     * @param pStartPoint the start point
     */
    public void setStartPoint(final Coordinate pStartPoint) {
        this.startPoint = pStartPoint;
    }

    /**
     * Gets end point.
     *
     * @return the end point
     */
    public Coordinate getEndPoint() {
        return endPoint;
    }

    /**
     * Sets end point.
     *
     * @param pEndPoint the end point
     */
    public void setEndPoint(final Coordinate pEndPoint) {
        this.endPoint = pEndPoint;
    }

    /**
     * Gets path.
     *
     * @return the path
     */
    public List<Coordinate> getPath() {
        return path;
    }

    /**
     * Sets path.
     *
     * @param pPath the path
     */
    public void setPath(final List<Coordinate> pPath) {
        this.path = pPath;
    }

    /**
     * Gets points of interest.
     *
     * @return the points of interest
     */
    public List<PointOfInterest> getPointsOfInterest() {
        return pointsOfInterest;
    }

    /**
     * Sets points of interest.
     *
     * @param pPointsOfInterest the points of interest
     */
    public void setPointsOfInterest(final List<PointOfInterest>
                                            pPointsOfInterest) {
        this.pointsOfInterest = pPointsOfInterest;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param pTitle the title
     */
    public void setTitle(final String pTitle) {
        this.title = pTitle;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param pDescription the description
     */
    public void setDescription(final String pDescription) {
        this.description = pDescription;
    }

    /**
     * Inner class representing geographical coordinates.
     */
    public static class Coordinate {
        /**
         * The Latitude.
         */
        private double latitude;
        /**
         * The Longitude.
         */
        private double longitude;

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

    /**
     * Inner class representing a point of interest along a route.
     */
    public static class PointOfInterest {
        /**
         * The Name.
         */
        private String name;
        /**
         * The Coordinates.
         */
        private Coordinate coordinates;

        /**
         * Gets name.
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Sets name.
         *
         * @param pName the name
         */
        public void setName(final String pName) {
            this.name = pName;
        }

        /**
         * Gets coordinates.
         *
         * @return the coordinates
         */
        public Coordinate getCoordinates() {
            return coordinates;
        }

        /**
         * Sets coordinates.
         *
         * @param pCoordinates the coordinates
         */
        public void setCoordinates(final Coordinate pCoordinates) {
            this.coordinates = pCoordinates;
        }
    }
}
