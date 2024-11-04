package flightbooking;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FlightController {

     User loggedInUser;
    private TextField originInput;
    private TextField destinationInput;
    private DatePicker dateInput;
    private TableView<Flight> flightTable;
    private List<Booking> bookings;
    private ListView<String> originSuggestions;
    private ListView<String> destinationSuggestions;
    private ComboBox<Integer> adultCountComboBox;
    private ComboBox<Integer> infantCountComboBox;

    public FlightController() {
        this.flightTable = FlightTableView.createTable();
        this.bookings = new ArrayList<>();

        // Set up MongoDB connection
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("flight_booking");
        database.getCollection("bookings");
    }

    public void handleLogin(String username, String email) {
        this.loggedInUser = new User(username, email);
    }

    public AnchorPane createLayout() {
        AnchorPane layout = new AnchorPane();
        layout.setPadding(new Insets(10));

        // Origin input and suggestions
        Label originLabel = new Label("Origin:");
        originInput = new TextField();
        originSuggestions = new ListView<>();
        originSuggestions.setVisible(false);

        // Destination input and suggestions
        Label destinationLabel = new Label("Destination:");
        destinationInput = new TextField();
        destinationSuggestions = new ListView<>();
        destinationSuggestions.setVisible(false);

        // Date picker
        Label dateLabel = new Label("Date:");
        dateInput = new DatePicker();
        
        Label adultCountLabel = new Label("Adults:");
        adultCountComboBox = new ComboBox<>();
        for (int i = 0; i <= 10; i++) {
            adultCountComboBox.getItems().add(i);
        }
        adultCountComboBox.setValue(1); // Default to 1 adult

        Label infantCountLabel = new Label("Infants (<3):");
        infantCountComboBox = new ComboBox<>();
        for (int i = 0; i <= 5; i++) {
            infantCountComboBox.getItems().add(i);
        }
        infantCountComboBox.setValue(0); // Default to 0 infants

        // Buttons
        Button searchButton = new Button("Search Flights");
        Button bookButton = new Button("Book Flight");

        AnchorPane.setTopAnchor(originLabel, 30.0);
        AnchorPane.setLeftAnchor(originLabel, 275.0);
        AnchorPane.setTopAnchor(originInput, 30.0);
        AnchorPane.setLeftAnchor(originInput, 375.0);
        AnchorPane.setRightAnchor(originInput, 275.0);
        originInput.setPrefWidth(200);

        // Position originSuggestions to overlap originInput
        AnchorPane.setTopAnchor(originSuggestions, 60.0);
        AnchorPane.setLeftAnchor(originSuggestions, 375.0);
        AnchorPane.setRightAnchor(originSuggestions, 275.0);
        
        originSuggestions.setPrefWidth(200);
        originSuggestions.setPrefHeight(100);

        AnchorPane.setTopAnchor(destinationLabel, 80.0);
        AnchorPane.setLeftAnchor(destinationLabel, 275.0);
        AnchorPane.setTopAnchor(destinationInput, 80.0);
        AnchorPane.setLeftAnchor(destinationInput, 375.0);
        AnchorPane.setRightAnchor(destinationInput, 275.0);
        originInput.setPrefWidth(200);

        // Position destinationSuggestions to overlap destinationInput
        AnchorPane.setTopAnchor(destinationSuggestions, 110.0);
        AnchorPane.setLeftAnchor(destinationSuggestions, 375.0);
        AnchorPane.setRightAnchor(destinationSuggestions, 275.0);
        
        destinationSuggestions.setPrefWidth(200);
        destinationSuggestions.setPrefHeight(100);

        AnchorPane.setTopAnchor(dateLabel, 130.0);
        AnchorPane.setLeftAnchor(dateLabel, 275.0);
        AnchorPane.setTopAnchor(dateInput, 130.0);
        AnchorPane.setLeftAnchor(dateInput, 375.0);
        AnchorPane.setRightAnchor(dateInput, 275.0);
        dateInput.setPrefWidth(200);
        
        AnchorPane.setTopAnchor(adultCountLabel, 180.0);
        AnchorPane.setLeftAnchor(adultCountLabel, 275.0);
        
        AnchorPane.setTopAnchor(adultCountComboBox, 210.0);
        AnchorPane.setLeftAnchor(adultCountComboBox, 275.0);
        adultCountComboBox.setPrefWidth(130.0);
        
        AnchorPane.setTopAnchor(infantCountLabel, 180.0);
        AnchorPane.setRightAnchor(infantCountLabel, 318.0);
        
        AnchorPane.setTopAnchor(infantCountComboBox, 210.0);
        AnchorPane.setRightAnchor(infantCountComboBox, 275.0);
        infantCountComboBox.setPrefWidth(130.0);


        AnchorPane.setTopAnchor(searchButton, 260.0);
        AnchorPane.setLeftAnchor(searchButton, 275.0);

        AnchorPane.setTopAnchor(flightTable, 310.0);
        AnchorPane.setLeftAnchor(flightTable, 50.0);
        AnchorPane.setRightAnchor(flightTable, 50.0);
        AnchorPane.setBottomAnchor(flightTable, 80.0);

        AnchorPane.setBottomAnchor(bookButton, 30.0);
        AnchorPane.setLeftAnchor(bookButton, 390.0);

        // Add all elements to layout
        layout.getChildren().addAll(
                originLabel, originInput, originSuggestions,
                destinationLabel, destinationInput, destinationSuggestions,
                dateLabel, dateInput, searchButton, flightTable, bookButton,
                adultCountLabel, adultCountComboBox, infantCountLabel, infantCountComboBox
                
        );

        // Event handling for autocomplete
        originInput.setOnKeyReleased(event -> showSuggestions(originInput.getText(), originSuggestions));
        destinationInput.setOnKeyReleased(event -> showSuggestions(destinationInput.getText(), destinationSuggestions));

        // Hide suggestions when clicking outside the text field
        layout.setOnMouseClicked(e -> {
            originSuggestions.setVisible(false);
            destinationSuggestions.setVisible(false);
        });

        // Handle flight search button click
        searchButton.setOnAction(e -> searchFlights());

        // Handle booking button click
        bookButton.setOnAction(e -> bookFlight());

        return layout;
    }

    private void showSuggestions(String query, ListView<String> suggestions) {
        if (query.isEmpty()) {
            suggestions.setVisible(false);
            return;
        }

        FlightService flightService = new FlightService();
        flightService.getAirportSuggestions(query, airportList -> {
            if (airportList.isEmpty()) {
                suggestions.setVisible(false);
            } else {
                suggestions.getItems().setAll(airportList);
                suggestions.setVisible(true);
                suggestions.toFront();  // Bring suggestions to the front
                suggestions.setOnMouseClicked(e -> {
                    String selectedAirport = suggestions.getSelectionModel().getSelectedItem();
                    if (selectedAirport != null) {
                        if (suggestions == originSuggestions) {
                            originInput.setText(selectedAirport);
                        } else {
                            destinationInput.setText(selectedAirport);
                        }
                        suggestions.setVisible(false);  // Hide suggestions after selection
                    }
                });
            }
        });
    }

    private void searchFlights() {
        String origin = originInput.getText();
        String destination = destinationInput.getText();
        String date = dateInput.getValue() != null ? dateInput.getValue().toString() : "";

        if (origin.isEmpty() || destination.isEmpty() || date.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill all fields!");
            alert.showAndWait();
            return;
        }

        FlightService flightService = new FlightService();
        flightService.searchFlights(origin, destination, date, flights -> {
            flightTable.getItems().setAll(flights);
        });
    }

    private void bookFlight() {
        // Log the current state of loggedInUser
        System.out.println("Current loggedInUser: " + loggedInUser);

        // Check if the user is logged in
        if (loggedInUser == null || loggedInUser.getEmail() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please log in to book a flight.");
            alert.showAndWait();
            return; // Exit the method if not logged in or email is null
        }

        Flight selectedFlight = flightTable.getSelectionModel().getSelectedItem();
        if (selectedFlight != null) {
            System.out.println("Selected flight: " + selectedFlight); // Log the selected flight
            showBookingConfirmation(selectedFlight);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a flight to book.");
            alert.showAndWait();
        }
    }

    // Show a booking confirmation window before confirming the flight booking
    private void showBookingConfirmation(Flight selectedFlight) {
        Stage confirmationStage = new Stage();
        confirmationStage.setTitle("Confirm Your Booking");

        // Create a main container with padding
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.getStyleClass().add("main-container");

        // Flight details section
        Label originLabel = new Label(selectedFlight.getDeparture());
        Label destinationLabel = new Label(selectedFlight.getArrival());
        Label flightNumberLabel = new Label("Flight: " + selectedFlight.getFlightNumber());
        
        // Parse the LocalDateTime strings from the selectedFlight object
        LocalDateTime departureDateTime = LocalDateTime.parse(selectedFlight.getDepartureTime());
        LocalDateTime arrivalDateTime = LocalDateTime.parse(selectedFlight.getArrivalTime());

        // Format the LocalDateTime to extract the date and time parts
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        String departureDate = departureDateTime.format(dateFormatter);
        String departureTime = departureDateTime.format(timeFormatter);
        String arrivalDate = arrivalDateTime.format(dateFormatter);
        String arrivalTime = arrivalDateTime.format(timeFormatter);

        // Create the labels with the formatted date and time
        Label departureDateLabel = new Label("Departure Date: " + departureDate);
        Label departureTimeLabel = new Label("Departure Time: " + departureTime);
        Label arrivalDateLabel = new Label("Arrival Date: " + arrivalDate);
        Label arrivalTimeLabel = new Label("Arrival Time: " + arrivalTime);
        
        int adultCount = adultCountComboBox.getValue();
        int infantCount = infantCountComboBox.getValue();
        double basePrice = selectedFlight.getPrice();
        double finalPrice = (adultCount * basePrice) + (infantCount * 0); // Assuming infants are free
        Label priceLabel = new Label("Price: ₹" + finalPrice);
        selectedFlight.price=finalPrice;
        selectedFlight.adultPassenger=adultCount;
        selectedFlight.infantPassenger=infantCount;

        originLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #000000;");
        destinationLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #000000;");
        flightNumberLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000;");
        departureDateLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000;");
        departureTimeLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000;");
        arrivalDateLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000;");
        arrivalTimeLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000;");
        priceLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000;");

        // Passenger details section
        Label passengerLabel = new Label("Passengers: " + adultCount + " Adult and " + infantCount + " Infants");
        Label emailLabel = new Label("Email: " + loggedInUser.getEmail());

        passengerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #000000;");
        emailLabel.setStyle("-fx-font-size: 16px;-fx-text-fill: #000000;");

        // Layout for flight details
        HBox flightDetailsBox = new HBox(10);
        flightDetailsBox.getChildren().addAll(originLabel, new Label("→"), destinationLabel);
        flightDetailsBox.setAlignment(Pos.CENTER);

        // Layout for additional details
        GridPane detailsGrid = new GridPane();
        detailsGrid.setVgap(10);
        detailsGrid.setHgap(20);
        detailsGrid.setAlignment(Pos.CENTER);
        detailsGrid.add(flightNumberLabel, 0, 0);
        detailsGrid.add(departureDateLabel, 0, 1);
        detailsGrid.add(departureTimeLabel, 0, 2);
        detailsGrid.add(arrivalDateLabel, 0, 3);
        detailsGrid.add(arrivalTimeLabel, 0, 4);
        detailsGrid.add(priceLabel, 0, 5);
        detailsGrid.add(passengerLabel, 0, 6);
        detailsGrid.add(emailLabel, 0, 7);

        // Barcode simulation
        Label barcodeLabel = new Label("|||||||||||||||||||||||||||||||||||||||||||");
        barcodeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #000000;");
        barcodeLabel.setAlignment(Pos.CENTER);

        // Buttons
        Button confirmButton = new Button("Confirm Booking");
        Button cancelButton = new Button("Cancel");
        confirmButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        cancelButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold;");

        // Button layout
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(confirmButton, cancelButton);

        // Add all elements to the main container
        mainContainer.getChildren().addAll(flightDetailsBox, detailsGrid, barcodeLabel, buttonBox);

        // Set event handlers for buttons
        confirmButton.setOnAction(e -> {
            String bookingID = generateBookingID();
            Booking booking = new Booking(
                loggedInUser.getUsername(),
                loggedInUser.getEmail(),
                selectedFlight,
                bookingID,
                selectedFlight.getDeparture()
                
            );
            bookings.add(booking);

            // Open the payment portal
            showPaymentForm(booking,adultCount,infantCount);

            confirmationStage.close();
        });

        cancelButton.setOnAction(e -> confirmationStage.close());

        // Create and set the scene
        Scene confirmationScene = new Scene(mainContainer, 335, 600);
        String css = new File("C:\\Users\\swani\\eclipse-workspace\\FlightBooking\\src\\flightbooking\\styles.css").toURI().toString();
        confirmationScene.getStylesheets().add(css);
        confirmationStage.setScene(confirmationScene);
        confirmationStage.show();
    }
    
    private void showPaymentForm(Booking booking, int adultCount, int infantCount) {
        Stage paymentStage = new Stage();
        paymentStage.setTitle("Payment Portal");

        // Main layout for the payment form
        AnchorPane paymentForm = new AnchorPane();
        paymentForm.setPadding(new Insets(20));
        paymentForm.getStyleClass().add("payment-container");

        // Title label
        Label titleLabel = new Label("Enter Payment Details");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        AnchorPane.setTopAnchor(titleLabel, 0.0);
        AnchorPane.setLeftAnchor(titleLabel, 34.0);

        // Input fields for payment
        TextField cardNumberField = new TextField();
        cardNumberField.setPromptText("Card Number");
        AnchorPane.setTopAnchor(cardNumberField, 40.0);
        AnchorPane.setLeftAnchor(cardNumberField, 50.0);
        AnchorPane.setRightAnchor(cardNumberField, 50.0);

        TextField cardholderNameField = new TextField();
        cardholderNameField.setPromptText("Cardholder Name");
        AnchorPane.setTopAnchor(cardholderNameField, 80.0);
        AnchorPane.setLeftAnchor(cardholderNameField, 50.0);
        AnchorPane.setRightAnchor(cardholderNameField, 50.0);

        TextField expiryDateField = new TextField();
        expiryDateField.setPromptText("MM/YY");
        AnchorPane.setTopAnchor(expiryDateField, 120.0);
        AnchorPane.setLeftAnchor(expiryDateField, 50.0);
        AnchorPane.setRightAnchor(expiryDateField, 50.0);

        TextField cvvField = new TextField();
        cvvField.setPromptText("CVV");
        AnchorPane.setTopAnchor(cvvField, 160.0);
        AnchorPane.setLeftAnchor(cvvField, 50.0);
        AnchorPane.setRightAnchor(cvvField, 50.0);

        // Confirm and Cancel buttons
        Button confirmPaymentButton = new Button("Pay Now");
        confirmPaymentButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        AnchorPane.setTopAnchor(confirmPaymentButton, 230.0);
        AnchorPane.setLeftAnchor(confirmPaymentButton, 50.0);

        Button cancelPaymentButton = new Button("Cancel");
        cancelPaymentButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        AnchorPane.setTopAnchor(cancelPaymentButton, 230.0);
        AnchorPane.setRightAnchor(cancelPaymentButton, 50.0);

        // Adding elements to layout
        paymentForm.getChildren().addAll(
            titleLabel, cardNumberField, cardholderNameField,
            expiryDateField, cvvField, confirmPaymentButton, cancelPaymentButton
        );

        // Set up event handling for the buttons
        confirmPaymentButton.setOnAction(e -> {
            if (validatePaymentForm(cardNumberField, expiryDateField, cvvField)) {
                // Simulate processing
                Alert processingAlert = new Alert(Alert.AlertType.INFORMATION, "Processing Payment...");
                processingAlert.show();

                // After a short delay to simulate processing
                new Thread(() -> {
                    try {
                        Thread.sleep(2000); // Simulate processing delay
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    // Close the processing alert on the FX application thread
                    Platform.runLater(() -> processingAlert.close());

                    // Payment successful
                    Platform.runLater(() -> {
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Payment Successful! Your booking is confirmed.");
                        successAlert.showAndWait();

                        // Save booking to MongoDB or perform additional actions here
                        UserRepository userRepository = new UserRepository();
                        userRepository.saveBooking(booking, adultCount, infantCount);

                        paymentStage.close();
                    });
                }).start();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid payment details. Please try again.");
                alert.showAndWait();
            }
        });

        cancelPaymentButton.setOnAction(e -> paymentStage.close());

        // Set the scene and display the stage
        Scene paymentScene = new Scene(paymentForm, 300, 465);
        String css = new File("C:\\Users\\swani\\eclipse-workspace\\FlightBooking\\src\\flightbooking\\styles.css").toURI().toString();
        paymentScene.getStylesheets().add(css);
        paymentStage.setScene(paymentScene);
        paymentStage.show();
    }


    private boolean validatePaymentForm(TextField cardNumberField, TextField expiryDateField, TextField cvvField) {
        // Perform basic validation of payment details
        String cardNumber = cardNumberField.getText();
        String expiryDate = expiryDateField.getText();
        String cvv = cvvField.getText();

        return cardNumber.matches("\\d{16}") &&  // Check if card number is 16 digits
               expiryDate.matches("(0[1-9]|1[0-2])/\\d{2}") && // Check if date is in MM/YY format
               cvv.matches("\\d{3}"); // Check if CVV is 3 digits
    }



    private String generateBookingID() {
        return "BK" + System.currentTimeMillis(); 
    }
}