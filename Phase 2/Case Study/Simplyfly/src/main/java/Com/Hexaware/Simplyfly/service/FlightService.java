package Com.Hexaware.Simplyfly.service;

import java.util.List;

import Com.Hexaware.Simplyfly.model.Flight;
import Com.Hexaware.Simplyfly.model.Route;

public interface FlightService {
    Flight addFlight(Flight flight);
    List<Flight> getAllFlights();
    Route addRouteToFlight(Long flightId, Route route);
    List<Route> searchRoutes(String origin, String destination);
    void deleteFlight(Long id);
}
