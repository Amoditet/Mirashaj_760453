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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ReviewsController {

    @FXML
    private ImageView arrowImageView;

    @FXML
    private ListView<Review> reviewListView;

    @FXML
    private Button closeButton;

    private Restaurant restaurant;
    private Review editingReview;

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @FXML
    private Button signInBtn;
    @FXML
    private Button favoriteBtn;
    @FXML
    private Button bookBtn;
    @FXML
    private Button reviewBtn;
    @FXML
    private Button restaurantsBtn;

    @FXML
    public void initialize() {
        UserSession.getInstance().loadReviewsFromCSV();

        refresh();

        reviewListView.setCellFactory(param -> new ReviewListCell());
        reviewListView.setUserData(this);

        // Setup navigation buttons
        setupNavigationButtons();
        updateButtonVisibility();

        try {
            Image arrowImage = new Image(getClass().getResourceAsStream("/images/left-arrow.png"));
            arrowImageView.setImage(arrowImage);
            javafx.scene.effect.ColorAdjust whiteEffect = new javafx.scene.effect.ColorAdjust();
            whiteEffect.setBrightness(1.0);
            arrowImageView.setEffect(whiteEffect);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupNavigationButtons() {
        // Set up managed property bindings
        if (signInBtn != null) {
            signInBtn.managedProperty().bind(signInBtn.visibleProperty());
        }
        if (favoriteBtn != null) {
            favoriteBtn.managedProperty().bind(favoriteBtn.visibleProperty());
            favoriteBtn.setOnAction(event -> openFavoritesPage());
        }
        if (bookBtn != null) {
            bookBtn.managedProperty().bind(bookBtn.visibleProperty());
            bookBtn.setOnAction(event -> openBookingsPage());
        }
        if (reviewBtn != null) {
            reviewBtn.managedProperty().bind(reviewBtn.visibleProperty());
            // Current page, so make it appear active
            reviewBtn.setStyle("-fx-background-color: #444444;");
        }
        if (restaurantsBtn != null) {
            restaurantsBtn.managedProperty().bind(restaurantsBtn.visibleProperty());
            restaurantsBtn.setOnAction(event -> openRestaurantsPage());
        }
    }

    public void refresh() {
        UserSession.getInstance().loadAllRestaurants();
        UserSession.getInstance().loadReviewsFromCSV(); // Ensure reviews are loaded from CSV
        loadReviews();
        updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        theknife.UserSession userSession = theknife.UserSession.getInstance();

        if (userSession.isNotLoggedIn()) {
            if (signInBtn != null)
                signInBtn.setVisible(true);
            if (favoriteBtn != null)
                favoriteBtn.setVisible(false);
            if (bookBtn != null)
                bookBtn.setVisible(false);
            if (reviewBtn != null)
                reviewBtn.setVisible(false);
            if (restaurantsBtn != null)
                restaurantsBtn.setVisible(false);
        } else {
            String role = userSession.getRole();
            if ("owner".equalsIgnoreCase(role) || "ristoratore".equalsIgnoreCase(role)) {
                if (signInBtn != null)
                    signInBtn.setVisible(false);
                if (favoriteBtn != null)
                    favoriteBtn.setVisible(false);
                if (bookBtn != null)
                    bookBtn.setVisible(false);
                if (reviewBtn != null)
                    reviewBtn.setVisible(false);
                if (restaurantsBtn != null)
                    restaurantsBtn.setVisible(true);
            } else if ("client".equalsIgnoreCase(role) || "cliente".equalsIgnoreCase(role)) {
                if (signInBtn != null)
                    signInBtn.setVisible(false);
                if (favoriteBtn != null)
                    favoriteBtn.setVisible(true);
                if (bookBtn != null)
                    bookBtn.setVisible(true);
                if (reviewBtn != null)
                    reviewBtn.setVisible(true);
                if (restaurantsBtn != null)
                    restaurantsBtn.setVisible(false);
            }
        }
    }

    public void refreshReviews() {
        // Reload reviews from CSV to ensure data is up-to-date
        UserSession.getInstance().loadReviewsFromCSV();
        loadReviews();
    }

    public void refreshRestaurantList() {
        UserSession.getInstance().loadAllRestaurants();
    }

    private void loadReviews() {
        reviewListView.getItems().clear();
        UserSession session = UserSession.getInstance();
        String currentUser = session.getUsername();
        String role = session.getRole();
        java.util.List<Review> allReviews = session.getReviews();
        java.util.List<Review> filteredReviews = new java.util.ArrayList<>();

        if ("owner".equalsIgnoreCase(role) || "ristoratore".equalsIgnoreCase(role)) {
            java.util.List<Restaurant> allRestaurants = session.getAllRestaurants();

            for (Review r : allReviews) {
                boolean matchFound = false;

                for (Restaurant restaurant : allRestaurants) {
                    if (r.getRestaurantId() == restaurant.getId()) {
                        matchFound = true;
                        break;
                    }

                    if (!matchFound && r.getRestaurantName() != null && restaurant.getName() != null) {
                        String reviewRestaurantName = r.getRestaurantName().trim().replaceAll("^\"|\"$", "");
                        String restaurantName = restaurant.getName().trim().replaceAll("^\"|\"$", "");
                        if (reviewRestaurantName.equalsIgnoreCase(restaurantName)) {
                            matchFound = true;
                            break;
                        }
                    }
                }

                if (matchFound) {
                    filteredReviews.add(r);
                }
            }
        } else {
            for (Review r : allReviews) {
                if (r.getUsername().equals(currentUser)) {
                    filteredReviews.add(r);
                }
            }
        }

        javafx.collections.ObservableList<Review> observableReviews = javafx.collections.FXCollections
                .observableArrayList(filteredReviews);
        reviewListView.setItems(observableReviews);
    }

    @FXML
    private void handleExitAction() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleAbout() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/about.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.setTitle("About");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initOwner(closeButton.getScene().getWindow());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoBack() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
            javafx.scene.Parent root = loader.load();

            javafx.scene.Scene scene = reviewListView.getScene();
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

    private void openFavoritesPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/favorites.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) reviewListView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Favorites");
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
            Stage stage = (Stage) reviewListView.getScene().getWindow();
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

    private void openRestaurantsPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/restaurants.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) reviewListView.getScene().getWindow();
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

    @FXML
    private void handleFavorites() {
        openFavoritesPage();
    }

    @FXML
    private void handleBookings() {
        openBookingsPage();
    }
}
