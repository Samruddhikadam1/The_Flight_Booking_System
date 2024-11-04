package flightbooking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.FlightOfferSearch;
import com.amadeus.resources.Location;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import org.json.JSONObject;  // Make sure to include JSON library (like org.json)

public class FlightService {

    private static final String API_KEY = "1qx1LJV5HX4qDhYsYK6Cnb9L80TqAHKr";  // Amadeus API Key
    private static final String API_SECRET = "Uw9Nn3FZvfXlBFb6";  // Amadeus API Secret

    private static final String EXCHANGE_RATE_API_URL = "https://v6.exchangerate-api.com/v6/6b8e62bd4989e7c34b7d5f8d/latest/USD"; //API endpoint
    private Amadeus amadeus;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> debounceFuture;
    private final Map<String, List<String>> airportSuggestionsCache = new HashMap<>();

    public FlightService() {
        // Initialize the Amadeus client
        amadeus = Amadeus.builder(API_KEY, API_SECRET).build();
    }

    // Fetch the exchange rate from the API
    private double fetchExchangeRate() {
        try {
            URL url = new URL(EXCHANGE_RATE_API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse the JSON response to get the INR exchange rate
                JSONObject jsonResponse = new JSONObject(response.toString());
                return jsonResponse.getJSONObject("conversion_rates").getDouble("INR"); // Assuming USD to INR conversion
            } else {
                throw new Exception("Failed to fetch exchange rate: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error fetching exchange rate: " + e.getMessage());
                alert.showAndWait();
            });
            return 1.0; // Fallback to 1.0 in case of failure
        }
    }

    // Search flights and pass the result to a callback
    public void searchFlights(String origin, String destination, String date, FlightCallback callback) {
        double exchangeRate = fetchExchangeRate(); // Fetch the current exchange rate

        // Retry logic for handling 400 errors
        int maxRetries = 3;  // Maximum number of retries
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                // Search for matching airports for the origin and destination
                Location[] originAirports = amadeus.referenceData.locations.get(
                        Params.with("keyword", origin).and("subType", "AIRPORT,CITY"));
                Location[] destinationAirports = amadeus.referenceData.locations.get(
                        Params.with("keyword", destination).and("subType", "AIRPORT,CITY"));

                if (originAirports.length == 0 || destinationAirports.length == 0) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "No matching airports found for the given locations.");
                        alert.showAndWait();
                    });
                    return;
                }

                // Extract the first matching airport IATA codes
                String originIata = originAirports[0].getIataCode();
                String destinationIata = destinationAirports[0].getIataCode();

                // Now search for flights between these IATA codes on the given date
                FlightOfferSearch[] flightOffers = amadeus.shopping.flightOffersSearch.get(
                        Params.with("originLocationCode", originIata)
                                .and("destinationLocationCode", destinationIata)
                                .and("departureDate", date)
                                .and("adults", 1)  // Number of passengers
                );

                // If flights are found, create flight objects and update the UI
                List<Flight> flights = new ArrayList<>();
                for (FlightOfferSearch offer : flightOffers) {
                    String flightNumber = offer.getItineraries()[0].getSegments()[0].getCarrierCode() +
                                          offer.getItineraries()[0].getSegments()[0].getNumber();
                    String departure = offer.getItineraries()[0].getSegments()[0].getDeparture().getIataCode();
                    String arrival = offer.getItineraries()[0].getSegments()[0].getArrival().getIataCode();

                    // Extract departure and arrival times
                    String departureTime = offer.getItineraries()[0].getSegments()[0].getDeparture().getAt().toString();
                    String arrivalTime = offer.getItineraries()[0].getSegments()[0].getArrival().getAt().toString();

                    // Extract price and convert it to double
                    String priceString = offer.getPrice().getTotal(); // Use total price as String
                    double priceUSD = Double.parseDouble(priceString); // Convert String to double

                    // Convert price to Indian Rupees
                    double priceINR = priceUSD * exchangeRate; // Convert to INR

                    // Format price to 2 decimal places
                    String formattedPrice = String.format("%.2f", priceINR);
                    flights.add(new Flight(flightNumber, departure, arrival, departureTime, arrivalTime, Double.parseDouble(formattedPrice)));
                }

                // Update the table on the UI thread
                Platform.runLater(() -> callback.onSuccess(FXCollections.observableArrayList(flights)));
                return; // Exit method if successful

            } catch (ResponseException e) {
            	// Handle 400 error specifically and retry
            	if ("400".equals(e.getCode())) {
            	    retryCount++;
            	    if (retryCount < maxRetries) {
            	        try {
            	            // Delay before retrying
            	            Thread.sleep(1000); // Wait for 1 seconds before retrying
            	        } catch (InterruptedException ex) {
            	            ex.printStackTrace();
            	        }
            	    } else {
            	        Platform.runLater(() -> {
            	            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to fetch flight data after multiple attempts: " + e.getMessage());
            	            alert.showAndWait();
            	        });
            	    }
            	}

            }
        }
    }

    public void getAirportSuggestionsWithDebounce(String query, AirportSuggestionCallback callback) {
        // Cancel any previous debounce task
        if (debounceFuture != null && !debounceFuture.isDone()) {
            debounceFuture.cancel(false);
        }

        // Schedule the task to be executed after a delay (300 ms)
        debounceFuture = scheduler.schedule(() -> getAirportSuggestions(query, callback), 400, TimeUnit.MILLISECONDS);
    }

    public void getAirportSuggestions(String query, AirportSuggestionCallback callback) {
        // Check cache first
        if (airportSuggestionsCache.containsKey(query)) {
            Platform.runLater(() -> callback.onSuccess(airportSuggestionsCache.get(query)));
            return;
        }

        try {
            Location[] locations = amadeus.referenceData.locations.get(
                    Params.with("keyword", query).and("subType", "AIRPORT,CITY"));

            List<String> airportList = new ArrayList<>();
            for (Location location : locations) {
                airportList.add(location.getName() + " (" + location.getIataCode() + ")");
            }

            // Cache the results for future use
            airportSuggestionsCache.put(query, airportList);

            Platform.runLater(() -> callback.onSuccess(airportList));
        } catch (ResponseException e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error fetching airport suggestions: " + e.getMessage());
                alert.showAndWait();
            });
        }
    }

    public interface AirportSuggestionCallback {
        void onSuccess(List<String> airportList);
    }
    
    
    // Callback interface for flight searches
    public interface FlightSearchCallback {
        void onSuccess(List<Flight> flights);
    }

    // Functional interface for flight search callback
    public interface FlightCallback {
        void onSuccess(javafx.collections.ObservableList<Flight> flights);
    }
}
