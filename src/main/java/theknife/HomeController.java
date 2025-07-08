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
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;

public class HomeController {

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private Button loginBtn;
    @FXML
    private Button registerBtn;
    @FXML
    private Button ospiteBtn;

    @FXML
    private ImageView knifeImage;

    @FXML
    private BorderPane rootPane;

    @FXML
    private Button exitBtn;

    @FXML
    public void initialize() {
        if (exitBtn != null) {
            exitBtn.setOnAction(event -> handleExitAction());
        }

        exitBtn.setDisable(false);
        exitBtn.setFocusTraversable(true);
        exitBtn.setOnAction(event -> handleExitAction());
        exitBtn.setOnMouseClicked(event -> handleExitAction());
        exitBtn.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            System.exit(0);
        });

    };

    @FXML
    private void handleLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setFullScreenExitHint("");
            stage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/register.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) registerBtn.getScene().getWindow();
            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExitAction() {
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleOspite() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/dashboard.fxml"));
            Stage stage = (Stage) ospiteBtn.getScene().getWindow();
            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setFullScreen(true);
            stage.setAlwaysOnTop(true);
            stage.show();
            stage.setAlwaysOnTop(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setFullScreen(false);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
