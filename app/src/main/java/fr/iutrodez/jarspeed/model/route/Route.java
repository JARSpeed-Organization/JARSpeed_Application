package fr.iutrodez.jarspeed.model.route;

import java.util.List;

import fr.iutrodez.jarspeed.model.route.CustomLineString;

/**
 * Represents a route with a start point, end point, path, points of interest,
 * and additional information.
 * This class is annotated as a MongoDB document.
 */
public class Route {
    /**
     * The Id.
     */
    public String id;
    /**
     * The User id.
     */
    public String userId;
    /**
     * The start date.
     */
    public String startDate;
    /**
     * The end date.
     */
    public String endDate;
    /**
     * The Path.
     */
    public CustomLineString path;
    /**
     * The Points of interest.
     */
    public List<PointOfInterest> pointsOfInterest;
    /**
     * The Title.
     */
    public String title;
    /**
     * The Description.
     */
    public String description;
    /**
     * The Elevation gain.
     */
    public Double elevationGain;
    /**
     * The Elevation loss.
     */
    public Double elevationLoss;

    /**
     * The Time.
     */
    public String time;

    /**
     * The Speed.
     */
    public String speed;

    /**
     * The Distance.
     */
    public String distance;

    /**
     * Instantiates a new Route.
     */
    public Route() {
    }

    /**
     * Instantiates a new Route dto.
     *
     * @param pId               the p id
     * @param pUserId           the p user id
     * @param pStartDate        the p start date
     * @param pEndDate          the p end date
     * @param pPath             the p path
     * @param pPointsOfInterest the p points of interest
     * @param pTitle            the p title
     * @param pDescription      the p description
     * @param pElevationGain    the p elevation gain
     * @param pElevationLoss    the p elevation loss
     * @param pSpeed            the p speed
     * @param pTime             the p time
     * @param pDistance         the p distance
     */
    public Route(String pId, String pUserId, String pStartDate, String pEndDate, CustomLineString pPath, List<PointOfInterest> pPointsOfInterest, String pTitle, String pDescription, Double pElevationGain, Double pElevationLoss, String pSpeed, String pTime, String pDistance) {
        id = pId;
        userId = pUserId;
        startDate = pStartDate;
        endDate = pEndDate;
        path = pPath;
        pointsOfInterest = pPointsOfInterest;
        title = pTitle;
        description = pDescription;
        elevationGain = pElevationGain;
        elevationLoss = pElevationLoss;
        time = pTime;
        speed = pSpeed;
        distance = pDistance;
    }

    // Getters and Setters

    /**
     * Gets speed.
     *
     * @return the speed
     */
    public String getSpeed() {return speed;}

    /**
     * Gets time.
     *
     * @return the time
     */
    public String getTime() {return time;}

    /**
     * Gets distance.
     *
     * @return the distance
     */
    public String getDistance() {return distance;}

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
     * Gets user id.
     *
     * @return the user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets user id.
     *
     * @param pUserId the p user id
     */
    public void setUserId(String pUserId) {
        userId = pUserId;
    }

    /**
     * Gets start date.
     *
     * @return the start date
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Sets start date.
     *
     * @param pStartDate the p start date
     */
    public void setStartDate(String pStartDate) {
        startDate = pStartDate;
    }

    /**
     * Gets end date.
     *
     * @return the end date
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Sets end date.
     *
     * @param pEndDate the p end date
     */
    public void setEndDate(String pEndDate) {
        endDate = pEndDate;
    }

    /**
     * Gets path.
     *
     * @return the path
     */
    public CustomLineString getPath() {
        return path;
    }

    /**
     * Sets path.
     *
     * @param pPath the path
     */
    public void setPath(final CustomLineString pPath) {
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


        /**
         * The Name.
         */
        public String name;
        /**
         * The Coordinates.
         */
        public CustomPoint point;

        /**
         * Instantiates a new Point of interest.
         */
        public PointOfInterest() {
        }

        /**
         * Instantiates a new Point of interest.
         *
         * @param pName  the p name
         * @param pPoint the point
         */
        public PointOfInterest(final String pName, final CustomPoint pPoint) {
            name = pName;
            point = pPoint;
        }

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
        public CustomPoint getCoordinates() {
            return point;
        }

        /**
         * Sets coordinates.
         *
         * @param pPoint the p point
         */
        public void setCoordinates(final CustomPoint pPoint) {
            point = pPoint;
        }
    }

    /**
     * Gets elevation gain.
     *
     * @return the elevation gain
     */
    public Double getElevationGain() {
        return elevationGain;
    }

    /**
     * Sets elevation gain.
     *
     * @param pElevationGain the p elevation gain
     */
    public void setElevationGain(Double pElevationGain) {
        elevationGain = pElevationGain;
    }

    /**
     * Gets elevation loss.
     *
     * @return the elevation loss
     */
    public Double getElevationLoss() {
        return elevationLoss;
    }

    /**
     * Sets elevation loss.
     *
     * @param pElevationLoss the p elevation loss
     */
    public void setElevationLoss(Double pElevationLoss) {
        elevationLoss = pElevationLoss;
    }
}
