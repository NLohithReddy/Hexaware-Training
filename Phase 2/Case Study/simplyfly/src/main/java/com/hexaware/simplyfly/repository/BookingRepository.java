package com.hexaware.simplyfly.repository;

import com.hexaware.simplyfly.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
List<Booking> findByUsername(String username);
List<Booking> findByFlightId(Long flightId);
}