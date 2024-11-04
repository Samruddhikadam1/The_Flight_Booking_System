package flightbooking;

public class Booking {
    private String username;
    private String email;
    private Flight flight; // Assuming Flight is a class that contains flight details
    private String bookingID; // Unique identifier for the booking
    private String departureDate; // Add other fields as needed

    public Booking(String username, String email, Flight flight, String bookingID, String departureDate) {
        this.username = username;
        this.email = email;
        this.flight = flight;
        this.bookingID = bookingID;
        this.departureDate = departureDate;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Flight getFlight() {
        return flight;
    }

    public String getBookingID() {
        return bookingID;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    @Override
    public String toString() {
        return String.format("Booking[User: %s, Email: %s, Flight: %s, BookingID: %s, Departure Date: %s]",
                              username, email, flight.getFlightNumber(), bookingID, departureDate);
    }
}
