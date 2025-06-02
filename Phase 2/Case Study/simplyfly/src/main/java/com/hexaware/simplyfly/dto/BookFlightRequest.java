package com.hexaware.simplyfly.dto;
/**
 * BookFlightRequest is a Data Transfer Object for booking flights.
 * It carries the flight ID and the number of seats to be booked.
 *
 * This DTO is used in the flight booking endpoint by users.
 *
 * Developer: N Lohith Reddy  
 * Created on: May 30, 2025  
 * Last Modified: May 31, 2025  
 */

public class BookFlightRequest {
private Long flightId;
private int seats;

public Long getFlightId() {
    return flightId;
}

public void setFlightId(Long flightId) {
    this.flightId = flightId;
}

public int getSeats() {
    return seats;
}

public void setSeats(int seats) {
    this.seats = seats;
}
}