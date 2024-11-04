package flightbooking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.types.Binary;
import org.mindrot.jbcrypt.BCrypt;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import javafx.scene.image.Image;

public class UserRepository {
    private static final MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    private final MongoCollection<Document> userCollection;
    private final MongoCollection<Document> bookingCollection;

    public UserRepository() {
        MongoDatabase database = mongoClient.getDatabase("flight_booking");
        userCollection = database.getCollection("users");
        bookingCollection = database.getCollection("bookings");
    }

    /*public List<String> getPastBookings(String email) {
        List<String> bookingsList = new ArrayList<>();
        for (Document doc : bookingCollection.find(new Document("email", email))) {
            String bookingDetails = String.format("Flight: %s | Departure: %s -> %s | Departure Time: %s | Arrival Time: %s | Price: $%.2f",
                                                  doc.getString("flightNumber"),
                                                  doc.getString("departure"),
                                                  doc.getString("arrival"),
                                                  doc.getString("departureTime"),
                                                  doc.getString("arrivalTime"),
                                                  doc.getDouble("price"));
            bookingsList.add(bookingDetails);
        }
        return bookingsList;
    }*/

    public void registerUser(String name, String email, String password) {
        if (userExists(email)) {
            System.out.println("User with this email already exists.");
            return;
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        Document userDocument = new Document("name", name)
                .append("email", email)
                .append("password", hashedPassword);
        userCollection.insertOne(userDocument);
    }

    public boolean userExists(String email) {
        return userCollection.find(new Document("email", email)).first() != null;
    }

    public boolean validateLogin(String identifier, String password) {
        Document user = userCollection.find(
            new Document("$or", Arrays.asList(
                new Document("email", identifier),
                new Document("name", identifier)
            ))
        ).first();
        return user != null && BCrypt.checkpw(password, user.getString("password"));
    }

    public String getUsername(String identifier) {
        Document user = userCollection.find(
            new Document("$or", Arrays.asList(
                new Document("email", identifier),
                new Document("name", identifier)
            ))
        ).first();
        return user != null ? user.getString("name") : null;
    }

    public String getEmail(String identifier) {
        Document user = userCollection.find(
            new Document("$or", Arrays.asList(
                new Document("email", identifier),
                new Document("name", identifier)
            ))
        ).first();
        return user != null ? user.getString("email") : null;
    }

    public void saveBooking(Booking booking, int adultCount, int infantCount) {
        Document bookingDocument = new Document("username", booking.getUsername())
                .append("email", booking.getEmail())
                .append("flightNumber", booking.getFlight().getFlightNumber())
                .append("departure", booking.getFlight().getDeparture())
                .append("arrival", booking.getFlight().getArrival())
                .append("departureTime", booking.getFlight().getDepartureTime())
                .append("arrivalTime", booking.getFlight().getArrivalTime())
                .append("adultCount", adultCount)
                .append("infantCount", infantCount)
                .append("price", booking.getFlight().getPrice());

        bookingCollection.insertOne(bookingDocument);
    }

    public void saveProfileImage(String email, File imageFile) {
        try {
            byte[] imageBytes = fileToByteArray(imageFile);
            userCollection.updateOne(new Document("email", email),
                    new Document("$set", new Document("profileImage", new Binary(imageBytes))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Image getProfileImage(String email) {
        Document user = userCollection.find(new Document("email", email)).first();
        if (user != null && user.containsKey("profileImage")) {
            Binary binaryImage = user.get("profileImage", Binary.class);
            return new Image(new ByteArrayInputStream(binaryImage.getData()));
        }
        return null;
    }

    private byte[] fileToByteArray(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        }
    }
}
