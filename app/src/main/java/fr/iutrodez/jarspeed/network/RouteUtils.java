package fr.iutrodez.jarspeed.network;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import fr.iutrodez.jarspeed.model.route.CustomLineString;
import fr.iutrodez.jarspeed.model.route.CustomPoint;

/**
 * The type Route utils.
 */
public class RouteUtils {

    /**
     * Polyline to line string custom line string.
     *
     * @param pPolyline the p polyline
     * @return the custom line string
     */
    public static CustomLineString polylineToLineString(Polyline pPolyline) {
        CustomLineString lineString = new CustomLineString();

        for (GeoPoint geoPoint : pPolyline.getActualPoints()) {
            lineString.add(geoPoint.getLongitude(), geoPoint.getLatitude());
        }

        return lineString;
    }

    /**
     * Generate title string.
     *
     * @param pTitle the p title
     * @param pDate  the p date
     * @return the string
     */
    public static String generateTitle(String pTitle, LocalDateTime pDate) {
        if (pTitle != null && !pTitle.trim().equals("")) {
            return pTitle;
        }
        return "Parcours du " + pDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
