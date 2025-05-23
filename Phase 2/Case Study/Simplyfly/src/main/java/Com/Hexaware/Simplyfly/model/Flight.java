package Com.Hexaware.Simplyfly.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "flights")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_name")
    private String flightName;

    @Column(name = "flight_number", unique = true)
    private String flightNumber;

    @Column(name = "baggage_checkin_kg")
    private int baggageCheckinKg;

    @Column(name = "baggage_cabin_kg")
    private int baggageCabinKg;

    @Column(name = "total_seats")
    private int totalSeats;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL)
    private List<Route> routes;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFlightName() {
		return flightName;
	}

	public void setFlightName(String flightName) {
		this.flightName = flightName;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public int getBaggageCheckinKg() {
		return baggageCheckinKg;
	}

	public void setBaggageCheckinKg(int baggageCheckinKg) {
		this.baggageCheckinKg = baggageCheckinKg;
	}

	public int getBaggageCabinKg() {
		return baggageCabinKg;
	}

	public void setBaggageCabinKg(int baggageCabinKg) {
		this.baggageCabinKg = baggageCabinKg;
	}

	public int getTotalSeats() {
		return totalSeats;
	}

	public void setTotalSeats(int totalSeats) {
		this.totalSeats = totalSeats;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}

    // Getters & Setters
    
}
