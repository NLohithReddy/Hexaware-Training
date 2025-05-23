package Com.Hexaware.Simplyfly.service;

import java.util.List;

import Com.Hexaware.Simplyfly.model.Booking;
import Com.Hexaware.Simplyfly.model.Flight;
import Com.Hexaware.Simplyfly.model.User;

public interface AdminService {
    List<User> getAllUsers();
    List<Flight> getAllFlights();
    List<Booking> getAllBookings();
    void deleteUser(Long userId);
    void deleteFlight(Long flightId);
    void deleteRoute(Long routeId);
}
