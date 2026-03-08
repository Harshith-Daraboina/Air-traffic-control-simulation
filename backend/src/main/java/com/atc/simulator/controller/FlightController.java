package com.atc.simulator.controller;

import com.atc.simulator.model.Flight;
import com.atc.simulator.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    @Autowired
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping("/seed")
    public ResponseEntity<List<Flight>> seedFlights(@RequestParam double lat, @RequestParam double lon,
            @RequestParam(defaultValue = "10") int count) {
        java.util.List<Flight> seeded = new java.util.ArrayList<>();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < count; i++) {
            Flight f = new Flight(
                    "TEST" + (1000 + random.nextInt(9000)),
                    lat + (random.nextDouble() - 0.5) * 5.0, // spread across ~500km
                    lon + (random.nextDouble() - 0.5) * 5.0,
                    30000 + (random.nextDouble() - 0.5) * 10000,
                    400 + random.nextDouble() * 200,
                    random.nextDouble() * 360,
                    System.currentTimeMillis());
            seeded.add(flightService.createFlight(f));
        }
        return new ResponseEntity<>(seeded, HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<Flight> createFlight(@RequestBody Flight flight) {
        Flight createdFlight = flightService.createFlight(flight);
        return new ResponseEntity<>(createdFlight, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Flight>> getAllFlights() {
        List<Flight> flights = flightService.getAllFlights();
        return new ResponseEntity<>(flights, HttpStatus.OK);
    }

    @GetMapping("/{flightNumber}")
    public ResponseEntity<Flight> getFlightByFlightNumber(@PathVariable String flightNumber) {
        Optional<Flight> flight = flightService.getFlightByFlightNumber(flightNumber);
        return flight.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{flightNumber}")
    public ResponseEntity<Flight> updateFlight(@PathVariable String flightNumber, @RequestBody Flight flightDetails) {
        Flight updatedFlight = flightService.updateFlight(flightNumber, flightDetails);
        if (updatedFlight != null) {
            return new ResponseEntity<>(updatedFlight, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{flightNumber}")
    public ResponseEntity<Void> deleteFlight(@PathVariable String flightNumber) {
        flightService.deleteFlight(flightNumber);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
