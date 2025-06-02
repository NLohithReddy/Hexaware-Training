package com.hexaware.simplyfly.controller;


import com.hexaware.simplyfly.entity.Flight;
import com.hexaware.simplyfly.service.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * FlightController handles all flight-related operations and routes.
 * It provides endpoints to add, view, search, and delete flights.
 *
 * Accessible functionalities based on roles:
 *
 * ADMIN and FLIGHT_OWNER roles:
 * Can add new flights
 * Can delete existing flights
 *
 * All users:
 * Can view all available flights
 * Can search flights by source and destination
 *
 * The controller delegates business logic to the FlightService layer.
 *
 * Developer: N Lohith Reddy  
 * Created on: May 30, 2025  
 * Last Modified: May 31, 2025  
 */

@RestController
@RequestMapping("/api/v1/flights")
public class FlightController {

private final FlightService flightService;

public FlightController(FlightService flightService) {
    this.flightService = flightService;
}

@PostMapping("/add")
@PreAuthorize("hasRole('ADMIN') or hasRole('FLIGHT_OWNER')")
public ResponseEntity<Flight> addFlight(@RequestBody Flight flight) {
    return ResponseEntity.ok(flightService.addFlight(flight));
}

@GetMapping("/all")
public ResponseEntity<List<Flight>> getAllFlights() {
    return ResponseEntity.ok(flightService.getAllFlights());
}

@GetMapping("/search")
public ResponseEntity<List<Flight>> searchFlights(
        @RequestParam String source,
        @RequestParam String destination) {
    return ResponseEntity.ok(flightService.searchFlights(source, destination));
}

@DeleteMapping("/{id}")
@PreAuthorize("hasRole('ADMIN') or hasRole('FLIGHT_OWNER')")
public ResponseEntity<String> deleteFlight(@PathVariable Long id) {
    flightService.deleteFlight(id);
    return ResponseEntity.ok("Flight deleted successfully");
}
}