package flightbooking;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class FlightTableView {

    // Creates the table and its columns
    @SuppressWarnings("unchecked")
    public static TableView<Flight> createTable() {
        TableView<Flight> table = new TableView<>();
        table.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        // Define columns
        TableColumn<Flight, String> flightNumberCol = new TableColumn<>("Flight Number");
        flightNumberCol.setCellValueFactory(new PropertyValueFactory<>("flightNumber"));
        flightNumberCol.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-text-fill: rgba(255,255,255);");

        TableColumn<Flight, String> departureCol = new TableColumn<>("Departure");
        departureCol.setCellValueFactory(new PropertyValueFactory<>("departure"));
        departureCol.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-text-fill: rgba(255,255,255);");

        TableColumn<Flight, String> arrivalCol = new TableColumn<>("Arrival");
        arrivalCol.setCellValueFactory(new PropertyValueFactory<>("arrival"));
        arrivalCol.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-text-fill: rgba(255,255,255);");
        
        TableColumn<Flight, String> departureTimeCol = new TableColumn<>("Departure Time");
        departureTimeCol.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        departureTimeCol.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-text-fill: rgba(255,255,255);");

        TableColumn<Flight, String> arrivalTimeCol = new TableColumn<>("Arrival Time");
        arrivalTimeCol.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        arrivalTimeCol.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-text-fill: rgba(255,255,255);");

        TableColumn<Flight, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-text-fill: rgba(255,255,255);");
        
     // Format price column to display two decimal places
        priceCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("₹%.2f", price));  // Format price in INR
                }
            }
        });

        // Add all columns to the table
        table.getColumns().addAll(flightNumberCol, departureCol, arrivalCol, departureTimeCol, arrivalTimeCol, priceCol);

        // Evenly distribute column widths
        double numColumns = table.getColumns().size();
        table.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double columnWidth = newWidth.doubleValue() / numColumns;
            table.getColumns().forEach(col -> col.setPrefWidth(columnWidth));
        });

        return table;
    }
}
