package fr.iutrodez.jarspeed.model.route;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class Route {
    private String id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<Coordinate> path;
    private List<PointOfInterest> pointsOfInterest;
    private String title;
    private String description;

    // Constructeurs, getters et setters

    public Route() {}

    public Route(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public List<Coordinate> getPath() {
        return path;
    }

    public void setPath(List<Coordinate> path) {
        this.path = path;
    }

    public List<PointOfInterest> getPointsOfInterest() {
        return pointsOfInterest;
    }

    public void setPointsOfInterest(List<PointOfInterest> pointsOfInterest) {
        this.pointsOfInterest = pointsOfInterest;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class Coordinate {
        private double latitude;
        private double longitude;

        // Constructeurs, getters et setters
        public Coordinate() {
        }

        public Coordinate(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }

    public static class PointOfInterest {
        private String name;
        private Coordinate coordinates;

        // Constructeurs, getters et setters
        public PointOfInterest() {
        }

        public PointOfInterest(String name, Coordinate coordinates) {
            this.name = name;
            this.coordinates = coordinates;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Coordinate getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(Coordinate coordinates) {
            this.coordinates = coordinates;
        }
    }
}
