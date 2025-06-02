package com.hexaware.simplyfly.repository;

import com.hexaware.simplyfly.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
List<Flight> findBySourceAndDestination(String source, String destination);
List<Flight> findByAirlineNameContainingIgnoreCase(String airlineName);
}
