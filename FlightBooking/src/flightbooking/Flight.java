package flightbooking;

public class Flight {
    private String flightNumber;
    private String departure;
    private String arrival;
    private String departureTime;
    private String arrivalTime;
    public double price;  
    public int adultPassenger;
    public int infantPassenger;

    public Flight(String flightNumber, String departure, String arrival, String departureTime, String arrivalTime, double price) {
        this.flightNumber = flightNumber;
        this.departure = departure;
        this.arrival = arrival;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price; 
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getDeparture() {
        return departure;
    }

    public String getArrival() {
        return arrival;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public double getPrice() { 
        return price;
    }
    
    public int getAdultPassenger() { 
        return adultPassenger;
    }
    
    public int getChildPassenger() { 
        return infantPassenger;    }
}
