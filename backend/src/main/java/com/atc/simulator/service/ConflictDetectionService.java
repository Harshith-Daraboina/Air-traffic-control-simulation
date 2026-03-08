package com.atc.simulator.service;

import com.atc.simulator.model.Flight;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConflictDetectionService {

    // Threshold in kilometers (e.g., 5 km horizontal separation)
    private static final double SAFETY_THRESHOLD_KM = 5.0; 
    // Altitude threshold in feet (e.g., 1000 ft vertical separation min)
    private static final double ALTITUDE_THRESHOLD_FT = 1000.0;

    public List<ConflictAlert> detectConflicts(List<Flight> activeFlights) {
        List<ConflictAlert> alerts = new ArrayList<>();

        for (int i = 0; i < activeFlights.size(); i++) {
            for (int j = i + 1; j < activeFlights.size(); j++) {
                Flight f1 = activeFlights.get(i);
                Flight f2 = activeFlights.get(j);

                double distance = calculateHaversineDistance(f1.getLatitude(), f1.getLongitude(), f2.getLatitude(), f2.getLongitude());
                double altDiff = Math.abs(f1.getAltitude() - f2.getAltitude());

                if (distance < SAFETY_THRESHOLD_KM && altDiff < ALTITUDE_THRESHOLD_FT) {
                    alerts.add(new ConflictAlert(f1.getFlightNumber(), f2.getFlightNumber(), distance, altDiff, System.currentTimeMillis()));
                }
            }
        }
        return alerts;
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public static class ConflictAlert {
        private String flight1;
        private String flight2;
        private double distanceKm;
        private double altitudeDiffFt;
        private long timestamp;

        public ConflictAlert(String flight1, String flight2, double distanceKm, double altitudeDiffFt, long timestamp) {
            this.flight1 = flight1;
            this.flight2 = flight2;
            this.distanceKm = distanceKm;
            this.altitudeDiffFt = altitudeDiffFt;
            this.timestamp = timestamp;
        }

        public String getFlight1() { return flight1; }
        public String getFlight2() { return flight2; }
        public double getDistanceKm() { return distanceKm; }
        public double getAltitudeDiffFt() { return altitudeDiffFt; }
        public long getTimestamp() { return timestamp; }
    }
}
