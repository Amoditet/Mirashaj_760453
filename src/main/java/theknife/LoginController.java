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
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.input.KeyCombination;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import java.nio.file.*;
import javafx.application.Platform;

import javafx.scene.control.Hyperlink;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileInputStream;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    @FXML
    private Hyperlink registerLink;

    @FXML
    private ImageView arrowImageView;

    @FXML
    private void initialize() {
        try {
            Image arrowImage = new Image(getClass().getResourceAsStream("/images/left-arrow.png"));
            arrowImageView.setImage(arrowImage);
            ColorAdjust whiteEffect = new ColorAdjust();
            whiteEffect.setBrightness(1.0);
            arrowImageView.setEffect(whiteEffect);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Scene sceneLocal = usernameField.getScene();
        if (sceneLocal != null) {
            sceneLocal.setFill(null);
        }

        registerLink.setOnAction(_ -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/register.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) registerLink.getScene().getWindow();
                javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
                Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.setFullScreenExitHint("");
                stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
                stage.setFullScreen(true);
                stage.show();
                stage.setFullScreenExitHint("");
                stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            } catch (Exception e) {
                if (errorLabel != null) {
                    errorLabel.setText("Error navigating to the registration page: " + e.getMessage());
                    errorLabel.setVisible(true);
                }
            }
        });
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            return;
        }

        username = username.trim();
        password = password.trim();

        // First try to read from external file if it exists
        Path externalFile = Paths.get("data/utenti.csv");
        if (Files.exists(externalFile)) {
            try (BufferedReader br = Files.newBufferedReader(externalFile)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 6 && parts[2].trim().equals(username) && parts[3].trim().equals(password)) {
                        String location = parts[4].trim();
                        String role = parts[5].trim();
                        UserSession.getInstance().setUser(username, location, role);
                        navigateToDashboard(location);
                        return;
                    }
                }
            } catch (IOException e) {
                // Continue to check resource file
            }
        }

        // Then try to read from resource file
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getResourceStream("data/utenti.csv")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[2].trim().equals(username) && parts[3].trim().equals(password)) {
                    String location = parts[4].trim();
                    String role = parts[5].trim();
                    UserSession.getInstance().setUser(username, location, role);
                    navigateToDashboard(location);
                    return;
                }
            }
            errorLabel.setText("Incorrect username or password.");
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Data access error.");
        }
    }

    private void navigateToDashboard(String location) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
            Parent root = loader.load();
            DashboardController controller = loader.getController();
            controller.setLocation(location);
            controller.updateButtonVisibility();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setFullScreen(true);
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error navigating to dashboard.");
        }
    }

    @FXML
    private void handleGoHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setFullScreenExitHint("");
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            stage.setFullScreen(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleExitAction() {
        Platform.exit();
        System.exit(0);
    }

    private static InputStream getResourceStream(String path) throws IOException {
        InputStream stream = LoginController.class.getClassLoader().getResourceAsStream(path);
        if (stream != null) {
            return stream;
        }
        java.io.File file = new java.io.File(path);
        if (file.exists()) {
            return new FileInputStream(file);
        }
        throw new IOException("Resource not found: " + path);
    }

}
