package com.atc.simulator.service;

import com.atc.simulator.model.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AircraftSimulationService {

    private final FlightService flightService;
    private final ConflictDetectionService conflictDetectionService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public AircraftSimulationService(FlightService flightService, ConflictDetectionService conflictDetectionService, SimpMessagingTemplate messagingTemplate) {
        this.flightService = flightService;
        this.conflictDetectionService = conflictDetectionService;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 2000) // Update every 2 seconds
    public void simulateFlights() {
        List<Flight> activeFlights = flightService.getAllFlights();
        if (activeFlights.isEmpty()) {
            return;
        }

        for (Flight flight : activeFlights) {
            updateFlightPosition(flight);
        }

        flightService.saveAll(activeFlights);

        // Broadcast updated positions to frontend
        messagingTemplate.convertAndSend("/topic/flights", activeFlights);

        // Check for conflicts
        List<ConflictDetectionService.ConflictAlert> alerts = conflictDetectionService.detectConflicts(activeFlights);
        if (!alerts.isEmpty()) {
            messagingTemplate.convertAndSend("/topic/alerts", alerts);
        }
    }

    private void updateFlightPosition(Flight flight) {
        // Simple simulation: move aircraft in the direction of its heading
        double speedKmPerSec = flight.getSpeed() / 3600.0; // speed is in km/h
        double distanceTraveled = speedKmPerSec * 2; // for 2 seconds

        // 1 degree of latitude is ~111km
        // Longitude distance varies with latitude
        double deltaLat = (distanceTraveled * Math.cos(Math.toRadians(flight.getHeading()))) / 111.0;
        double deltaLon = (distanceTraveled * Math.sin(Math.toRadians(flight.getHeading()))) / (111.0 * Math.cos(Math.toRadians(flight.getLatitude())));

        flight.setLatitude(flight.getLatitude() + deltaLat);
        flight.setLongitude(flight.getLongitude() + deltaLon);
        flight.setTimestamp(System.currentTimeMillis());
    }
}
