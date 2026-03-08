package com.atc.simulator.service;

import com.atc.simulator.model.Flight;
import com.atc.simulator.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    @Autowired
    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public Flight createFlight(Flight flight) {
        flight.setTimestamp(System.currentTimeMillis());
        return flightRepository.save(flight);
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Optional<Flight> getFlightByFlightNumber(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber);
    }

    public Flight updateFlight(String flightNumber, Flight flightDetails) {
        Optional<Flight> optionalFlight = flightRepository.findByFlightNumber(flightNumber);
        if (optionalFlight.isPresent()) {
            Flight existingFlight = optionalFlight.get();
            existingFlight.setLatitude(flightDetails.getLatitude());
            existingFlight.setLongitude(flightDetails.getLongitude());
            existingFlight.setAltitude(flightDetails.getAltitude());
            existingFlight.setSpeed(flightDetails.getSpeed());
            existingFlight.setHeading(flightDetails.getHeading());
            existingFlight.setTimestamp(System.currentTimeMillis());
            return flightRepository.save(existingFlight);
        }
        return null; // Handle this carefully in the controller
    }

    public void deleteFlight(String flightNumber) {
        flightRepository.deleteByFlightNumber(flightNumber);
    }

    public void saveAll(List<Flight> flights) {
        flightRepository.saveAll(flights);
    }
}
