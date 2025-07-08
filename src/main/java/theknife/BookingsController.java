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
import javafx.scene.control.ListView;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class BookingsController {
    @FXML
    private ListView<Bookings> bookingsListView;
    @FXML
    private HBox guestButtons;
    @FXML
    private HBox userButtons;
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
    private Button exitBtn;
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
        setupBookingsListView();

        try {
            Image arrowImage = new Image(getClass().getResourceAsStream("/images/left-arrow.png"));
            arrowImageView.setImage(arrowImage);
            ColorAdjust whiteEffect = new ColorAdjust();
            whiteEffect.setBrightness(1.0);
            arrowImageView.setEffect(whiteEffect);

        } catch (Exception e) {
            e.printStackTrace();
        }

        favoriteBtn.setOnAction(event -> openFavoritesPage());

        reviewBtn.setOnAction(event -> openReviewsPage());
    }

    private void setupNavigationButtons() {
        // Set up managed property bindings
        if (signInBtn != null) {
            signInBtn.managedProperty().bind(signInBtn.visibleProperty());
        }
        if (favoriteBtn != null) {
            favoriteBtn.managedProperty().bind(favoriteBtn.visibleProperty());
            // Setup existing action or keep as is
        }
        if (bookBtn != null) {
            bookBtn.managedProperty().bind(bookBtn.visibleProperty());
            // Current page, so make it appear active
            bookBtn.setStyle("-fx-background-color: #444444;");
        }
        if (reviewBtn != null) {
            reviewBtn.managedProperty().bind(reviewBtn.visibleProperty());
            // Setup existing action or keep as is
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
            Stage stage = (Stage) bookingsListView.getScene().getWindow();
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

    private void openFavoritesPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/favorites.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) bookingsListView.getScene().getWindow();
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

    private void openReviewsPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/reviews.fxml"));
            Parent root = loader.load();

            ReviewsController controller = loader.getController();
            controller.refreshReviews();

            Stage stage = (Stage) bookingsListView.getScene().getWindow();
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

    private void setupBookingsListView() {
        bookingsListView.setCellFactory(param -> new BookingListCell());
        loadBookings();
    }

    private void loadBookings() {
        bookingsListView.getItems().clear();
        bookingsListView.getItems().addAll(UserSession.getInstance().getBookings());
    }

    public void refreshBookings() {
        loadBookings();
    }

    @FXML
    private void handleGoBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
            Parent root = loader.load();

            refreshBookings();

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
    private void handleExitAction() {
        javafx.application.Platform.exit();
        System.exit(0);
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

    public void updateButtonVisibility() {
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
}
