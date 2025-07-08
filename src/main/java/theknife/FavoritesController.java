/**
 * 
 * Erik Mirashaj Matricola: 760453 Sede: VA Email: emirashaj@studenti.uninsubria.it
 * Igor Gorchynskyi Matricola: 757184 Sede: VA
 * Lorenzo Mujeci Matricola: 757597 Sede: VA
 * Matteo Nika Matricola: 762540 Sede: VA
 * 
 */

package theknife;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.util.Callback;
import javafx.scene.input.MouseEvent;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class FavoritesController {
    @FXML
    private BorderPane rootPane;
    @FXML
    private ListView<Restaurant> favoritesListView;
    @FXML
    private HBox guestButtons;
    @FXML
    private HBox userButtons;
    @FXML
    private Button signInBtn;
    @FXML
    private Button bookBtn;
    @FXML
    private Button favoriteBtn;
    @FXML
    private Button reviewBtn;
    @FXML
    private Button exitBtn;
    @FXML
    private Button restaurantsBtn;
    @FXML
    private Button backButton;
    @FXML
    private Button aboutBtn;

    @FXML
    private ImageView arrowImageView;

    @FXML
    public void initialize() {
        // Setup navigation buttons
        setupNavigationButtons();
        updateButtonVisibility();
        loadFavorites();

        favoritesListView.setCellFactory(new Callback<ListView<Restaurant>, ListCell<Restaurant>>() {
            @Override
            public ListCell<Restaurant> call(ListView<Restaurant> listView) {
                return new FavoritesListCell();
            }
        });

        favoritesListView.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                Restaurant selected = favoritesListView.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    return;
                }
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/restaurant_detail.fxml"));
                    Parent root = loader.load();

                    theknife.RestaurantDetailController controller = loader.getController();
                    controller.setRestaurant(selected);

                    Scene scene = favoritesListView.getScene();
                    scene.setRoot(root);

                    Stage stage = (Stage) scene.getWindow();
                    stage.setTitle(selected.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

        bookBtn.setOnAction(event -> openBookingsPage());

        reviewBtn.setOnAction(event -> openReviewsPage());
    }

    private void setupNavigationButtons() {
        // Set up managed property bindings
        if (signInBtn != null) {
            signInBtn.managedProperty().bind(signInBtn.visibleProperty());
        }
        if (favoriteBtn != null) {
            favoriteBtn.managedProperty().bind(favoriteBtn.visibleProperty());
            // Current page, so make it appear active
            favoriteBtn.setStyle("-fx-background-color: #444444;");
        }
        if (bookBtn != null) {
            bookBtn.managedProperty().bind(bookBtn.visibleProperty());
            // Setup existing action or add navigation
        }
        if (reviewBtn != null) {
            reviewBtn.managedProperty().bind(reviewBtn.visibleProperty());
            // Setup existing action or add navigation
        }
        if (restaurantsBtn != null) {
            restaurantsBtn.managedProperty().bind(restaurantsBtn.visibleProperty());
            restaurantsBtn.setOnAction(event -> openRestaurantsPage());
        }
    }

    private void openRestaurantsPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/restaurants.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) favoritesListView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Restaurants");
            stage.setMaximized(true);
            stage.setFullScreenExitHint("");
            stage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
            stage.setFullScreen(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openBookingsPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/bookings.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) favoritesListView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Bookings");
            stage.setMaximized(true);
            stage.setFullScreenExitHint("");
            stage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
            stage.setFullScreen(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openReviewsPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/reviews.fxml"));
            Parent root = loader.load();

            ReviewsController controller = loader.getController();
            controller.refreshReviews();

            Stage stage = (Stage) favoritesListView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Reviews");
            stage.setMaximized(true);
            stage.setFullScreenExitHint("");
            stage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
            stage.setFullScreen(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshFavorites() {
        loadFavorites();
    }

    private void loadFavorites() {
        favoritesListView.getItems().clear();
        java.util.List<Restaurant> favorites = UserSession.getInstance().getFavorites();
        if (favorites.isEmpty()) {
            favoritesListView.getItems().add(null);
            favoritesListView.setDisable(true);
        } else {
            favoritesListView.setDisable(false);
            java.util.Set<String> added = new java.util.HashSet<>();
            for (Restaurant r : favorites) {
                String name = r.getName() != null ? r.getName().toLowerCase() : "";
                if (added.add(name)) {
                    favoritesListView.getItems().add(r);
                }
            }
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

        } else {
            String role = userSession.getRole();
            if ("owner".equalsIgnoreCase(role) || "ristoratore".equalsIgnoreCase(role)) {
                signInBtn.setVisible(false);
                favoriteBtn.setVisible(false);
                bookBtn.setVisible(false);
                reviewBtn.setVisible(false);
                restaurantsBtn.setVisible(true);

            } else if ("client".equalsIgnoreCase(role) || "cliente".equalsIgnoreCase(role)) {
                signInBtn.setVisible(false);
                favoriteBtn.setVisible(true);
                bookBtn.setVisible(true);
                reviewBtn.setVisible(true);
                restaurantsBtn.setVisible(false);
            }
        }
    }

    @FXML
    private void handleExitAction() {
        javafx.application.Platform.exit();
        System.exit(0);
    }

    @FXML
    private void handleGoBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
            Parent root = loader.load();

            Scene scene = arrowImageView.getScene();
            scene.setRoot(root);

            Stage stage = (Stage) scene.getWindow();
            stage.setTitle("Dashboard");
            stage.setMaximized(true);
            stage.setFullScreenExitHint("");
            stage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
            stage.setFullScreen(true);
        } catch (Exception e) {
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
}
