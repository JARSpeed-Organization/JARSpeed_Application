package fr.iutrodez.jarspeed.ui;

import static org.junit.Assert.*;
import org.junit.Test;
import org.osmdroid.util.GeoPoint;

public class MapActivityTest {
    @Test
    public void testCalculateDistanceBetweenTwoPoints() {
        GeoPoint point1 = new GeoPoint(48.8566, 2.3522); // Paris coordinates
        GeoPoint point2 = new GeoPoint(51.5074, -0.1278); // London coordinates
        double expectedDistance = 343.77; // Approx distance in kilometers

        double result = MapActivity.calculateDistanceBetweenTwoPoints(point1, point2);

        // Assert that the calculated distance is close to the expected value
        // Allow for a small error margin due to calculation differences
        assertEquals(expectedDistance, result, 0.5);
    }

    @Test
    public void testCalculateDistanceWithSamePoints() {
        GeoPoint point1 = new GeoPoint(40.7128, -74.0060); // New York coordinates

        double result = MapActivity.calculateDistanceBetweenTwoPoints(point1, point1);

        assertEquals(0.0, result, 0.0);
    }


}
