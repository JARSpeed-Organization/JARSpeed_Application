package fr.iutrodez.jarspeed.network;

import org.osmdroid.util.GeoPoint;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import fr.iutrodez.jarspeed.model.Coordinate;

public class RouteUtils {

    public static List<Coordinate> pointsToCoordinates(List<GeoPoint> points) {
        List<Coordinate> coordinates = new ArrayList<>();
        for (GeoPoint point : points) {
            coordinates.add(new Coordinate(point.getLatitude(), point.getLongitude()));
        }
        return coordinates;
    }

    public static String generateTitle(String pTitle, LocalDateTime pDate) {
        if (pTitle != null && !pTitle.trim().equals("")) {
            return pTitle;
        }
        return "Parcours du " + pDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
