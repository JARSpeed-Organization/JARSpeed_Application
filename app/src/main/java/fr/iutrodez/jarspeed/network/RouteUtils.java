package fr.iutrodez.jarspeed.network;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import fr.iutrodez.jarspeed.model.Coordinate;

public class RouteUtils {

    public static List<Coordinate> pointsToCoordinates(List<GeoPoint> points) {
        List<Coordinate> coordinates = new ArrayList<>();
        for (GeoPoint point : points) {
            coordinates.add(new Coordinate(point.getLongitude(), point.getLatitude()));
        }
        return coordinates;
    }
}
