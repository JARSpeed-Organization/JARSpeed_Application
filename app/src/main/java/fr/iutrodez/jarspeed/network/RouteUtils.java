package fr.iutrodez.jarspeed.network;

import org.osmdroid.util.GeoPoint;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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

    public static String generateTitle(String pTitle, long pDate) {
        if (pTitle != null && !pTitle.trim().equals("")) {
            return pTitle;
        }

        return "Parcours du " + new SimpleDateFormat("dd/MM/yyyy").format(new Date(pDate));
    }
}
