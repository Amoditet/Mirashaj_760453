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
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.util.List;

public class RepliesController {

    @FXML
    private ImageView arrowImageView;
    @FXML
    private Button closeButton;
    @FXML
    private ListView<Reply> repliesListView;
    @FXML
    private javafx.scene.layout.HBox ownerButtons;
    @FXML
    private Button restaurantsBtn;
    @FXML
    private Button repliesBtn;

    @FXML
    public void initialize() {
        // Set up ListView
        repliesListView.setCellFactory(param -> new ReplyListCell());
        repliesListView.setUserData(this);

        try {
            Image arrowImage = new Image(getClass().getResourceAsStream("/images/left-arrow.png"));
            arrowImageView.setImage(arrowImage);
            javafx.scene.effect.ColorAdjust whiteEffect = new javafx.scene.effect.ColorAdjust();
            whiteEffect.setBrightness(1.0);
            arrowImageView.setEffect(whiteEffect);

            arrowImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    Stage stage = (Stage) newScene.getWindow();
                    stage.setMaximized(true);
                    stage.setFullScreenExitHint("");
                    stage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
                    stage.setFullScreen(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadReplies();

        // Set up navigation buttons
        setupNavigationButtons();
        updateButtonVisibility();
    }

    private void setupNavigationButtons() {
        if (ownerButtons != null) {
            ownerButtons.managedProperty().bind(ownerButtons.visibleProperty());
        }
        if (restaurantsBtn != null) {
            restaurantsBtn.managedProperty().bind(restaurantsBtn.visibleProperty());
            restaurantsBtn.setOnAction(event -> openRestaurantsPage());
        }
        if (repliesBtn != null) {
            repliesBtn.managedProperty().bind(repliesBtn.visibleProperty());
            // Current page, so make it appear active but non-clickable
            repliesBtn.setStyle("-fx-background-color: #444444;");
        }
    }

    private void updateButtonVisibility() {
        UserSession userSession = UserSession.getInstance();
        boolean isOwner = "owner".equalsIgnoreCase(userSession.getRole()) ||
                "ristoratore".equalsIgnoreCase(userSession.getRole());

        if (ownerButtons != null) {
            ownerButtons.setVisible(isOwner);
        }
        if (restaurantsBtn != null) {
            restaurantsBtn.setVisible(isOwner);
        }
        if (repliesBtn != null) {
            repliesBtn.setVisible(isOwner);
        }
    }

    private void loadReplies() {
        if (repliesListView == null) {
            return;
        }
        repliesListView.getItems().clear();

        // Ensure replies are loaded from CSV
        UserSession.getInstance().loadRepliesFromCSV();
        UserSession.getInstance().loadReviewsFromCSV();

        UserSession session = UserSession.getInstance();
        String currentOwnerUsername = session.getUsername();

        List<Reply> ownerReplies = session.getReplies().stream()
                .filter(reply -> currentOwnerUsername.equals(reply.getOwnerUsername()))
                .toList();

        javafx.collections.ObservableList<Reply> observableReplies = javafx.collections.FXCollections
                .observableArrayList(ownerReplies);
        repliesListView.setItems(observableReplies);
    }

    public void refreshReplies() {
        loadReplies();
    }

    @FXML
    private void handleClose() {
        try {
            javafx.application.Platform.exit();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/view/dashboard.fxml"));
            javafx.scene.Parent root = loader.load();

            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Dashboard");
            stage.setMaximized(true);
            stage.setFullScreenExitHint("");
            stage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
            stage.setFullScreen(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAbout() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/about.fxml"));
            javafx.scene.Parent root = loader.load();
            Stage stage = new Stage();
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.setTitle("About");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initOwner(closeButton.getScene().getWindow());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openRestaurantsPage() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/view/restaurants.fxml"));
            javafx.scene.Parent root = loader.load();
            Stage stage = (Stage) restaurantsBtn.getScene().getWindow();
            javafx.scene.Scene scene = restaurantsBtn.getScene();
            scene.setRoot(root);
            stage.setTitle("My Restaurants");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
