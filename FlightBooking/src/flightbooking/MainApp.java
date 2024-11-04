package flightbooking;

import java.io.File;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainApp extends Application {

    private String username;
    private String email;
    private BorderPane mainLayout;
    private Scene mainScene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Udaan");

        // Initialize the main layout container
        mainLayout = new BorderPane();

        // Load the background image from the URL
        Image backgroundImage = new Image("https://wallpapercave.com/wp/wp6493826.jpg");
        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(
                        BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true // Set to cover the entire layout
                )
        );
        mainLayout.setBackground(new Background(bgImage));

        // Create the main scene
        mainScene = new Scene(mainLayout, 1000, 600);

        String css = new File("C:\\Users\\swani\\eclipse-workspace\\FlightBooking\\src\\flightbooking\\styles.css").toURI().toString();
        mainScene.getStylesheets().add(css);

        // Set the initial page to the flight booking page
        showFlightBookingPage(primaryStage);

        // Show the stage
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }


    // Creates the login page layout
    private VBox createLoginLayout(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Title Label for the login page
        Label titleLabel = new Label("LOGIN");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: rgba(255, 255, 255, 1);");

        // Username/Email and Password fields
        Label identifierLabel = new Label("Email:");
        TextField identifierInput = new TextField();
        identifierLabel.getStyleClass().add("Label");
        identifierInput.setPromptText("Enter Email");

        Label passwordLabel = new Label("Password:");
        passwordLabel.getStyleClass().add("Label");
        PasswordField passwordInput = new PasswordField();  // For password input
        passwordInput.setPromptText("Enter your password");
        
        Button backButton = new Button("Back");

        Button loginButton = new Button("Login");
        loginButton.setAlignment(Pos.CENTER);
        loginButton.getStyleClass().add("Button");
        Hyperlink registerLink = new Hyperlink("Don't have an account? Register here!");
        backButton.setOnAction(e -> showFlightBookingPage((Stage) mainLayout.getScene().getWindow()));

        // Add components to the layout
        grid.add(identifierLabel, 0, 0);
        grid.add(identifierInput, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordInput, 1, 1);
        grid.add(loginButton, 1, 2);
        grid.add(backButton, 1, 4);
        grid.add(registerLink, 1, 3);  // Add the register link

        // Center the GridPane
        grid.setAlignment(Pos.CENTER);

        // Create a translucent VBox to wrap the grid
        VBox translucentBox = new VBox(10);  // Add spacing between title and grid
        translucentBox.setPadding(new Insets(20));
        translucentBox.setAlignment(Pos.CENTER);
        translucentBox.getChildren().addAll(titleLabel, grid);  // Add title label at the top

        // Set the size of the VBox
        translucentBox.setMaxWidth(400);
        translucentBox.setMaxHeight(400);

        // Apply translucent background color using inline CSS or you can define it in an external stylesheet
        translucentBox.setStyle(
            "-fx-background-color: rgba(0,0,0, 0.5);" +  // White background with 50% opacity
            "-fx-background-radius: 10;" +  // Rounded corners
            "-fx-border-radius: 10;"        // Same rounded corners for the border
        );

        // Handle login button click
        loginButton.setOnAction(e -> {
            String email = identifierInput.getText(); // Now using "email" for clarity
            String password = passwordInput.getText();

            UserRepository userRepository = new UserRepository(); // Initialize repository to check credentials
            if (email.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a valid email and password!");
                alert.showAndWait();
            } else if (!isValidEmail(email)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a valid email format!");
                alert.showAndWait();
            } else if (userRepository.validateLogin(email, password)) {
                // Successful login
                this.email = email;  // Directly assign email if valid
                this.username = userRepository.getUsername(email); // Assuming you have a method to retrieve the username based on email

                // Switch to the flight booking page
                showFlightBookingPage(primaryStage);
            } else {
                // Invalid login credentials
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid credentials! Please try again.");
                alert.showAndWait();
            }
        });

        // Utility method to validate email format
        
        // Handle register link click
        registerLink.setOnAction(e -> showRegistrationPage(primaryStage));

        return translucentBox; // Return the VBox instead of the GridPane
    }
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }


    // Show registration page
    private void showRegistrationPage(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Title Label for the registration page
        Label titleLabel = new Label("CREATE NEW ACCOUNT");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: rgba(255, 255, 255, 1);");

        Label nameLabel = new Label("Name:");
        nameLabel.getStyleClass().add("Label");
        TextField nameInput = new TextField();
        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("Label");
        TextField emailInput = new TextField();
        Label passwordLabel = new Label("Password:");
        passwordLabel.getStyleClass().add("Label");
        PasswordField passwordInput = new PasswordField();

        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("Button");
        registerButton.setAlignment(Pos.CENTER);
        Hyperlink loginLink = new Hyperlink("Already have an account? Login here!");

        grid.add(nameLabel, 0, 0);
        grid.add(nameInput, 1, 0);
        grid.add(emailLabel, 0, 1);
        grid.add(emailInput, 1, 1);
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordInput, 1, 2);
        grid.add(registerButton, 1, 3);
        grid.add(loginLink, 1, 4); // Add the login link to the grid

        // Center the GridPane
        grid.setAlignment(Pos.CENTER);

        // Create a translucent VBox to wrap the grid
        VBox translucentBox = new VBox(10);  // Add spacing between title and grid
        translucentBox.setPadding(new Insets(20));
        translucentBox.setAlignment(Pos.CENTER);
        translucentBox.getChildren().addAll(titleLabel, grid);  // Add title label at the top
        
        // Set the size of the VBox
        translucentBox.setMaxWidth(400);
        translucentBox.setMaxHeight(400);

        // Apply translucent background color using inline CSS or you can define it in an external stylesheet
        translucentBox.setStyle(
            "-fx-background-color: rgba(0, 0, 0, 0.5);" +  // White background with 80% opacity
            "-fx-background-radius: 10;" +  // Rounded corners
            "-fx-border-radius: 10;"        // Same rounded corners for the border
        );

        // Handle register button click
        registerButton.setOnAction(e -> {
            String name = nameInput.getText();
            String email = emailInput.getText();
            String password = passwordInput.getText();

            UserRepository userRepository = new UserRepository();
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || !email.contains("@")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a valid name, email, and password!");
                alert.showAndWait();
            } else if (userRepository.userExists(email)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Email already exists!");
                alert.showAndWait();
            } else {
                userRepository.registerUser(name, email, password);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Registration successful! You can now log in.");
                alert.showAndWait();
                mainLayout.setCenter(createLoginLayout(primaryStage));  // Switch back to login layout
            }
        });

        // Handle login link click
        loginLink.setOnAction(e -> {
            mainLayout.setCenter(createLoginLayout(primaryStage));  // Switch back to login layout
        });

        // Set the translucent VBox as the center of the main layout
        mainLayout.setCenter(translucentBox);
    }


    // Show the flight booking page
    private void showFlightBookingPage(Stage primaryStage) {
        FlightController flightController = new FlightController();
        flightController.handleLogin(this.username, this.email);

        VBox sideNav = new VBox(10);

        // Load the logo image
        Image logoImage = new Image("file:C:\\Users\\swani\\Downloads\\Udaan.png"); // Use the appropriate path to your logo file
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitWidth(100); 
        logoImageView.setPreserveRatio(true); 
        logoImageView.setSmooth(true); 
        logoImageView.setStyle("-fx-padding: 10;"); 

        // Create a circular clip for the logo
        Circle clip = new Circle(50); 
        clip.setCenterX(50); 
        clip.setCenterY(50); 
        logoImageView.setClip(clip); // Set the circular clip on the image view

        // Create existing buttons
        Button profileButton = new Button("User Profile");
        profileButton.getStyleClass().add("button");

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("button");
        loginButton.setOnAction(e -> mainLayout.setCenter(createLoginLayout(primaryStage)));
        loginButton.setVisible(true);
        if(flightController.loggedInUser.getEmail()!=null) {
        	loginButton.setVisible(false);
        }

        // Create a spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS); // Allow the spacer to grow and take available space

        // Create logout button
        Button logoutButton = new Button("Sign Out");
        logoutButton.setVisible(false);
        if(flightController.loggedInUser.getEmail()!=null) {
        	logoutButton.setVisible(true);
        }

        // Center the logo and buttons in the sidebar
        sideNav.setAlignment(Pos.TOP_CENTER); // Align items at the top center
        sideNav.getChildren().addAll(logoImageView, profileButton, loginButton, spacer, logoutButton);
        sideNav.setPadding(new Insets(10));
        sideNav.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        profileButton.setOnAction(e -> showUserProfile());
        logoutButton.setOnAction(e -> logout(primaryStage));

        BorderPane flightBookingLayout = new BorderPane();
        flightBookingLayout.setLeft(sideNav);
        flightBookingLayout.setCenter(flightController.createLayout());

        mainLayout.setCenter(flightBookingLayout);
    }

    private void showUserProfile() {
        // Create a new layout for the user profile
        GridPane profileLayout = new GridPane();
        profileLayout.setPadding(new Insets(20, 20, 20, 20));
        profileLayout.setVgap(15);
        profileLayout.setHgap(15);
        profileLayout.setAlignment(Pos.CENTER); // Center the content

        // Display user details with enhanced styling
        Label profileLabel = new Label("USER PROFILE");
        profileLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");

        // Create HBoxes for name and email
        HBox nameBox = new HBox(10); // Spacing between the labels and values
        nameBox.setAlignment(Pos.CENTER);
        Label nameLabel = new Label("Name:");
        nameLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFFFFF;");
        Label nameValue = new Label(this.username);
        
        nameBox.getChildren().addAll(nameLabel, nameValue);

        HBox emailBox = new HBox(10); // Spacing between the labels and values
        emailBox.setAlignment(Pos.CENTER);
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFFFFF;");
        Label emailValue = new Label(this.email);
        
        emailBox.getChildren().addAll(emailLabel, emailValue);

        // Profile picture section with circular clip and border
        ImageView profileImageView = new ImageView();
        profileImageView.setFitWidth(100);
        profileImageView.setFitHeight(100);
        profileImageView.setPreserveRatio(false);

        Circle clip = new Circle(50);
        clip.setCenterX(50);
        clip.setCenterY(50);
        profileImageView.setClip(clip);

        Circle border = new Circle(50);
        border.setStroke(Color.WHITE);
        border.setStrokeWidth(2);
        border.setFill(Color.TRANSPARENT);

        StackPane profileImageStack = new StackPane();
        profileImageStack.getChildren().addAll(profileImageView, border);

        // Upload profile picture button
        Button uploadImageButton = new Button("Upload Profile Photo");

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        uploadImageButton.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(mainLayout.getScene().getWindow());
            if (selectedFile != null) {
                try {
                    // Load and display the image
                    Image profileImage = new Image(selectedFile.toURI().toString());
                    profileImageView.setImage(profileImage);

                    // Save the profile image to MongoDB
                    UserRepository userRepository = new UserRepository();
                    userRepository.saveProfileImage(this.email, selectedFile); // Save image with user's email

                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Profile photo uploaded successfully!");
                    alert.showAndWait();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load the image!");
                    alert.showAndWait();
                }
            }
        });

        // Back button
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF; -fx-background-color: #6c757d;");
        backButton.setOnAction(e -> showFlightBookingPage((Stage) mainLayout.getScene().getWindow()));

        // Translucent VBox as a background container
        VBox translucentBox = new VBox(15);
        translucentBox.setPadding(new Insets(25));
        translucentBox.setAlignment(Pos.CENTER);
        translucentBox.getChildren().addAll(profileLabel, profileImageStack, nameBox, emailBox, uploadImageButton, backButton);
        translucentBox.setMaxWidth(400);
        translucentBox.setMaxHeight(550);
        translucentBox.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.5);" +  // White background with 80% opacity
                "-fx-background-radius: 10;" +  // Rounded corners
                "-fx-border-radius: 10;"        // Same rounded corners for the border
        );

        // Set the translucent VBox as the center of the main layout
        mainLayout.setCenter(translucentBox);

        // Load existing profile image from MongoDB if available
        UserRepository userRepository = new UserRepository();
        Image existingImage = userRepository.getProfileImage(this.email);
        if (existingImage != null) {
            profileImageView.setImage(existingImage);
        }
    }


    private void logout(Stage primaryStage) {
        // Create a confirmation alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Signout Confirmation");
        alert.setHeaderText("Are you sure you want to sign out?");
        alert.setContentText("Click OK to sign out, or Cancel to stay logged in.");

        // Set button types for the alert
        ButtonType okButton = new ButtonType("OK");
        ButtonType cancelButton = new ButtonType("Cancel");
        alert.getButtonTypes().setAll(okButton, cancelButton);

        // Show the alert and wait for user response
        alert.showAndWait().ifPresent(response -> {
            if (response == okButton) {
                // User confirmed logout
                this.username = null;
                this.email = null;
                mainLayout.setCenter(createLoginLayout(primaryStage));
            }
            // If the user clicks Cancel, do nothing and stay logged in
        });
    }

}
