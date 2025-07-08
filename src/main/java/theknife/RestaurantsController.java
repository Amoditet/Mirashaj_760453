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
import javafx.scene.control.ListView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.util.Optional;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import theknife.UserSession;
import theknife.Restaurant;
import theknife.RestaurantListCell;

import javafx.scene.layout.HBox;

public class RestaurantsController {
    @FXML
    private ListView<Restaurant> restaurantListView;
    @FXML
    private Button addRestaurantBtn;
    @FXML
    private Button exitBtn;

    @FXML
    private ImageView arrowImageView;

    @FXML
    private Button aboutBtn;
    @FXML
    private Button bookBtn;
    @FXML
    private Button favoriteBtn;
    @FXML
    private Button restaurantsBtn;
    @FXML
    private Button repliesBtn;
    @FXML
    private Button reviewBtn;
    @FXML
    private Button signInBtn;

    @FXML
    public void initialize() {
        restaurantListView.setCellFactory(param -> new theknife.RestaurantListCell());

        updateButtonVisibility();

        signInBtn.managedProperty().bind(signInBtn.visibleProperty());
        addRestaurantBtn.managedProperty().bind(addRestaurantBtn.visibleProperty());
        restaurantsBtn.managedProperty().bind(restaurantsBtn.visibleProperty());
        repliesBtn.managedProperty().bind(repliesBtn.visibleProperty());
        favoriteBtn.managedProperty().bind(favoriteBtn.visibleProperty());
        bookBtn.managedProperty().bind(bookBtn.visibleProperty());
        reviewBtn.managedProperty().bind(reviewBtn.visibleProperty());

        restaurantsBtn.setStyle("-fx-background-color: #444444;");

        repliesBtn.setOnAction(event -> openRepliesPage());

        loadRestaurants();

        javafx.application.Platform.runLater(() -> {
            if (restaurantListView.getScene() != null && restaurantListView.getScene().getWindow() instanceof Stage) {
                Stage stage = (Stage) restaurantListView.getScene().getWindow();
                stage.setUserData(this);
            }
        });

        try {
            Image arrowImage = new Image(getClass().getResourceAsStream("/images/left-arrow.png"));
            arrowImageView.setImage(arrowImage);
            ColorAdjust whiteEffect = new ColorAdjust();
            whiteEffect.setBrightness(1.0);
            arrowImageView.setEffect(whiteEffect);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadRestaurants() {
        UserSession session = UserSession.getInstance();
        session.loadAllRestaurants();

        List<Restaurant> restaurants;
        if ("owner".equalsIgnoreCase(session.getRole()) || "ristoratore".equalsIgnoreCase(session.getRole())) {
            restaurants = session.getOwnedRestaurants();
        } else {
            restaurants = session.getAllRestaurants();
        }

        javafx.collections.ObservableList<Restaurant> observableList = javafx.collections.FXCollections
                .observableArrayList(restaurants);
        restaurantListView.setItems(observableList);
    }

    @FXML
    private void handleAddRestaurant() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/restaurants_parameters.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Restaurant");
            stage.setScene(new Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.setMaximized(true);
            stage.showAndWait();

            loadRestaurants();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateButtonVisibility() {
        theknife.UserSession userSession = theknife.UserSession.getInstance();

        if (userSession.isNotLoggedIn()) {
            signInBtn.setVisible(true);
            favoriteBtn.setVisible(false);
            bookBtn.setVisible(false);
            reviewBtn.setVisible(false);
            restaurantsBtn.setVisible(false);
            repliesBtn.setVisible(false);
            addRestaurantBtn.setVisible(false);

        } else {
            String role = userSession.getRole();
            if ("owner".equalsIgnoreCase(role) || "ristoratore".equalsIgnoreCase(role)) {
                signInBtn.setVisible(false);
                favoriteBtn.setVisible(false);
                bookBtn.setVisible(false);
                reviewBtn.setVisible(false);
                restaurantsBtn.setVisible(true);
                repliesBtn.setVisible(true);
                addRestaurantBtn.setVisible(true);

            } else if ("client".equalsIgnoreCase(role) || "cliente".equalsIgnoreCase(role)) {
                signInBtn.setVisible(false);
                favoriteBtn.setVisible(false);
                bookBtn.setVisible(false);
                reviewBtn.setVisible(false);
                restaurantsBtn.setVisible(true);
                repliesBtn.setVisible(false);
                addRestaurantBtn.setVisible(false);
            }
        }
    }

    @FXML
    private void handleExitAction() {
        javafx.application.Platform.exit();
        System.exit(0);
    }

    @FXML
    private void handleGoHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/dashboard.fxml"));
            Stage stage = (Stage) restaurantListView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Dashboard");
            stage.setMaximized(true);
            stage.setFullScreen(true);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAbout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/about.fxml"));
            Stage newStage = new Stage();
            Scene scene = new Scene(root);
            newStage.setScene(scene);
            newStage.setTitle("About");
            newStage.sizeToScene();
            newStage.centerOnScreen();
            newStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openRepliesPage() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/view/replies.fxml"));
            javafx.scene.Parent root = loader.load();
            Stage stage = (Stage) repliesBtn.getScene().getWindow();
            javafx.scene.Scene scene = repliesBtn.getScene();
            scene.setRoot(root);
            stage.setTitle("My Replies");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
