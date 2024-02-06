package fr.iutrodez.jarspeed.model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Represents a route with a start point, end point, path, points of interest,
 * and additional information.
 * This class is annotated as a MongoDB document.
 */
public class RouteDTO {
    /**
     * The Id.
     */
    private String id;
    /**
     * The start date.
     */
    private long startDate;
    /**
     * The end date.
     */
    private long endDate;
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

    /**
     * Instantiates a new Route dto.
     *
     * @param pId               the p id
     * @param pStartDate        the p start date
     * @param pEndDate          the p end date
     * @param pPath             the p path
     * @param pPointsOfInterest the p points of interest
     * @param pTitle            the p title
     * @param pDescription      the p description
     */
    public RouteDTO(String pId, long pStartDate, long pEndDate, List<Coordinate> pPath, List<PointOfInterest> pPointsOfInterest, String pTitle, String pDescription) {
        id = pId;
        startDate = pStartDate;
        endDate = pEndDate;
        path = pPath;
        pointsOfInterest = pPointsOfInterest;
        title = pTitle;
        description = pDescription;
    }

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
     * Gets start date.
     *
     * @return the start date
     */
    public long getStartDate() {
        return startDate;
    }

    /**
     * Sets start date.
     *
     * @param pStartDate the p start date
     */
    public void setStartDate(long pStartDate) {
        startDate = pStartDate;
    }

    /**
     * Gets end date.
     *
     * @return the end date
     */
    public long getEndDate() {
        return endDate;
    }

    /**
     * Sets end date.
     *
     * @param pEndDate the p end date
     */
    public void setEndDate(long pEndDate) {
        endDate = pEndDate;
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
     * Inner class representing a point of interest along a route.
     */
    public static class PointOfInterest {

        public PointOfInterest(final String pName, final Coordinate pCoordinates) {
            name = pName;
            coordinates = pCoordinates;
        }

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
            coordinates = pCoordinates;
        }
    }
}
