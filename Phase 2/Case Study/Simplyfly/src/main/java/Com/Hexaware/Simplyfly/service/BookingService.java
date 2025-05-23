package Com.Hexaware.Simplyfly.service;

import java.util.List;

import Com.Hexaware.Simplyfly.model.Booking;

public interface BookingService {
    Booking createBooking(Long userId, Long routeId, List<Long> seatIds);
    Booking cancelBooking(Long bookingId, String reason);
    List<Booking> getBookingsByUser(Long userId);
}
