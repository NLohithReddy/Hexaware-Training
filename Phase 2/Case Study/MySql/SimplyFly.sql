-- 1. Create Database
CREATE DATABASE simplyfly_db;
USE simplyfly_db;



-- 2. Users Table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    contact_number VARCHAR(15),
    address TEXT,
    role ENUM('PASSENGER', 'FLIGHT_OWNER', 'ADMIN') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



-- 3. Flights Table
CREATE TABLE flights (
    id INT AUTO_INCREMENT PRIMARY KEY,
    flight_name VARCHAR(100) NOT NULL,
    flight_number VARCHAR(20) UNIQUE NOT NULL,
    baggage_checkin_kg INT,
    baggage_cabin_kg INT,
    total_seats INT NOT NULL,
    owner_id INT NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);


-- 4. Routes Table
CREATE TABLE routes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    flight_id INT NOT NULL,
    origin VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    departure_time DATETIME NOT NULL,
    arrival_time DATETIME NOT NULL,
    fare DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (flight_id) REFERENCES flights(id) ON DELETE CASCADE
);


-- 5. Seats Table
CREATE TABLE seats (
    id INT AUTO_INCREMENT PRIMARY KEY,
    route_id INT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    is_booked BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE
);



-- 6. Bookings Table
CREATE TABLE bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    route_id INT NOT NULL,
    booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('BOOKED', 'CANCELLED') DEFAULT 'BOOKED',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE
);



-- 7. Booking_Seats Table (many-to-many mapping)
CREATE TABLE booking_seats (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    seat_id INT NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id) REFERENCES seats(id) ON DELETE CASCADE
);





-- 8. Payments Table 
CREATE TABLE payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    payment_method ENUM('CREDIT_CARD', 'DEBIT_CARD', 'UPI', 'NET_BANKING') NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_status ENUM('SUCCESS', 'FAILED', 'PENDING') DEFAULT 'SUCCESS',
    transaction_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
);



-- 9. Cancellations Table 
CREATE TABLE cancellations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    cancelled_by INT NOT NULL,
    cancellation_reason TEXT,
    refund_amount DECIMAL(10,2),
    refund_status ENUM('INITIATED', 'COMPLETED', 'FAILED') DEFAULT 'INITIATED',
    cancelled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (cancelled_by) REFERENCES users(id)
);



-- Srikanth Reddy booked 2 seats (1A, 1B) on IndiGo flight 6E123 from Hyderabad to Chennai and paid via UPI.
-- Users
INSERT INTO users (name, email, password, contact_number, address, role) VALUES
('Srikanth Reddy', 'srikanth@example.com', 'pass123', '9876543210', 'Hyderabad', 'PASSENGER'),
('Tejaswi N', 'tejaswi@example.com', 'pass123', '9876543211', 'Vijayawada', 'PASSENGER'),
('Praveen Kumar', 'praveen@example.com', 'pass123', '9876543212', 'Chennai', 'FLIGHT_OWNER'),
('Lohith Reddy', 'lohith.reddy@example.com', 'admin123', '9876543213', 'Bangalore', 'ADMIN');

-- Flights
INSERT INTO flights (flight_name, flight_number, baggage_checkin_kg, baggage_cabin_kg, total_seats, owner_id) VALUES
('IndiGo', '6E123', 15, 7, 180, 3),
('Air India', 'AI456', 25, 10, 150, 3),
('Vistara', 'UK789', 20, 8, 170, 3);

-- Routes
INSERT INTO routes (flight_id, origin, destination, departure_time, arrival_time, fare) VALUES
(1, 'Hyderabad', 'Chennai', '2025-05-12 08:00:00', '2025-05-12 09:30:00', 3500.00),
(2, 'Vizag', 'Bangalore', '2025-05-13 11:00:00', '2025-05-13 13:00:00', 4000.00),
(3, 'Tirupati', 'Hyderabad', '2025-05-14 06:00:00', '2025-05-14 07:00:00', 2800.00);

-- Seats for Route 1
INSERT INTO seats (route_id, seat_number, is_booked) VALUES
(1, '1A', FALSE),
(1, '1B', FALSE),
(1, '1C', TRUE),
(1, '2A', FALSE),
(1, '2B', FALSE);

-- Booking by Srikanth
INSERT INTO bookings (user_id, route_id, total_amount, status) VALUES
(1, 1, 7000.00, 'BOOKED');

-- Booking Seats
INSERT INTO booking_seats (booking_id, seat_id) VALUES
(1, 1),  -- Seat 1A
(1, 2);  -- Seat 1B

-- Payment for Booking
INSERT INTO payments (booking_id, payment_method, amount, payment_status) VALUES
(1, 'UPI', 7000.00, 'SUCCESS');
