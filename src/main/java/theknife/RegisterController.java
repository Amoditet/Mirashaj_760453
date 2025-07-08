/**
 * 
 * Erik Mirashaj Matricola: 760453 Sede: VA Email: emirashaj@studenti.uninsubria.it
 * Igor Gorchynskyi Matricola: 757184 Sede: VA
 * Lorenzo Mujeci Matricola: 757597 Sede: VA
 * Matteo Nika Matricola: 762540 Sede: VA
 * 
 */

package theknife;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.effect.ColorAdjust;
import javafx.application.Platform;
import java.io.*;
import java.nio.file.*;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.Arrays;

public class RegisterController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField locationField;
    @FXML
    private CheckBox clientCheckBox;
    @FXML
    private CheckBox ownerCheckBox;
    @FXML
    private Label errorLabel;
    @FXML
    private Hyperlink loginLink;
    @FXML
    private ImageView arrowImageView;

    @FXML
    public void initialize() {
        try {
            Image arrowImage = new Image(getClass().getResourceAsStream("/images/left-arrow.png"));
            arrowImageView.setImage(arrowImage);
            ColorAdjust whiteEffect = new ColorAdjust();
            whiteEffect.setBrightness(1.0);
            arrowImageView.setEffect(whiteEffect);
        } catch (Exception e) {
        }

        if (clientCheckBox != null && ownerCheckBox != null) {
            clientCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    ownerCheckBox.setSelected(false);
                }
            });
            ownerCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    clientCheckBox.setSelected(false);
                }
            });
        } else {
        }

        if (loginLink != null) {
            loginLink.setOnAction(event -> handleNavigateToLogin());
        } else {
        }

        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }
    }

    @FXML
    private void handleRegister() {
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String location = locationField.getText().trim();

        name = capitalizeFirstLetter(name);
        surname = capitalizeFirstLetter(surname);
        location = capitalizeFirstLetter(location);

        String role = null;
        if (clientCheckBox.isSelected()) {
            role = "client";
        } else if (ownerCheckBox.isSelected()) {
            role = "owner";
        }

        if (name.isEmpty() || surname.isEmpty() || username.isEmpty() ||
                password.isEmpty() || location.isEmpty() || role == null) {
            showError("Please fill in all required fields.");
            return;
        }

        if (!isOnlyLetters(name) || !isOnlyLetters(surname) || !isOnlyLetters(location)) {
            showError("Name, surname, and location must contain only letters.");
            return;
        }

        try {
            if (isUsernameExists(username)) {
                showError("Username already exists. Please choose a different one.");
                return;
            }

            String newUser = String.join(",", name, surname, username, password, location, role);
            saveUserToFile(newUser);
            showError("Registration successful! Please use the login screen.");
            navigateToDashboard(username, location, role);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error during registration: " + e.getMessage());
        }
    }

    private boolean isUsernameExists(String username) {
        // First check external file if it exists
        Path externalFile = Paths.get("data/utenti.csv");
        if (Files.exists(externalFile)) {
            try (BufferedReader br = Files.newBufferedReader(externalFile)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3 && parts[2].trim().equals(username)) {
                        return true;
                    }
                }
            } catch (IOException e) {
                // Continue to check resource file
            }
        }

        // Then check resource file
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getResourceStream("data/utenti.csv")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[2].trim().equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    private void saveUserToFile(String newUser) throws IOException {
        // For JAR distribution, we need to write to an external file
        Path dataDir = Paths.get("data");
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }

        Path usersFile = dataDir.resolve("utenti.csv");

        // If the file doesn't exist, copy from resources first
        if (!Files.exists(usersFile)) {
            try (InputStream resourceStream = getResourceStream("data/utenti.csv");
                    OutputStream fileStream = Files.newOutputStream(usersFile, StandardOpenOption.CREATE)) {
                resourceStream.transferTo(fileStream);
            } catch (IOException e) {
                // If resource doesn't exist, create empty file with header
                Files.write(usersFile, Arrays.asList("Name,Surname,Username,Password,Location,Role"));
            }
        }

        // Append the new user
        String newUserWithNewline = newUser;
        Files.write(usersFile, Arrays.asList(newUserWithNewline),
                StandardOpenOption.APPEND);
    }

    private static InputStream getResourceStream(String path) throws IOException {
        InputStream stream = RegisterController.class.getClassLoader().getResourceAsStream(path);
        if (stream != null) {
            return stream;
        }
        java.io.File file = new java.io.File(path);
        if (file.exists()) {
            return new FileInputStream(file);
        }
        throw new IOException("Resource not found: " + path);
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    private boolean isOnlyLetters(String input) {
        return input != null && input.matches("[a-zA-Z]+");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void handleNavigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginLink.getScene().getWindow();

            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setFullScreenExitHint("");
            stage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
            stage.setFullScreen(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error navigating to login page.");
        }
    }

    private void navigateToDashboard(String username, String location, String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
            Parent root = loader.load();

            UserSession.getInstance().setUser(username, location, role);

            DashboardController controller = loader.getController();
            if (controller != null) {
                controller.setLocation(location);
                controller.updateButtonVisibility();
            }

            Stage stage = (Stage) nameField.getScene().getWindow();
            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setFullScreen(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error navigating to dashboard.");
        }
    }

    @FXML
    private void handleGoHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nameField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setFullScreen(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error navigating to home page.");
        }
    }

    @FXML
    private void handleExitAction() {
        Platform.exit();
        System.exit(0);
    }
}
