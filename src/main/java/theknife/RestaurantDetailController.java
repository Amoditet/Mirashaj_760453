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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class RestaurantDetailController {

    private boolean isFavorite = false;
    private Image heartImage;
    private Image redHeartImage;

    @FXML
    private Text nameText;
    @FXML
    private ImageView arrowImageView;
    @FXML
    private ImageView heartImageView;
    @FXML
    private Button heartButton;
    @FXML
    private Text cuisineText;
    @FXML
    private Text ratingText;
    @FXML
    private Text reviewsText;
    @FXML
    private Text priceText;
    @FXML
    private Text distanceText;
    @FXML
    private Text addressText;
    @FXML
    private Text phoneNumberText;
    @FXML
    private Text awardsText;
    @FXML
    private javafx.scene.control.Label descriptionLabel;
    @FXML
    private javafx.scene.control.ScrollPane mainScrollPane;
    @FXML
    private Button closeButton;
    @FXML
    private Button backButton;

    @FXML
    private Button favoriteBtn;
    @FXML
    private Button reviewBtn;
    @FXML
    private Button bookBtn;
    @FXML
    private Button signInBtn;
    @FXML
    private Button RestaurantsBtn;
    @FXML
    private Button RepliesBtn;
    @FXML
    private javafx.scene.layout.HBox guestButtons;
    @FXML
    private javafx.scene.layout.HBox userButtons;
    @FXML
    private Button bookButton;
    @FXML
    private Button reviewButton;
    @FXML
    private javafx.scene.layout.VBox reviewsContainer;

    private Restaurant restaurant;

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        isFavorite = false;
        if (restaurant != null && UserSession.getInstance().getFavorites().stream()
                .anyMatch(fav -> fav.getName().trim().replaceAll("^\"|\"$", "")
                        .equalsIgnoreCase(restaurant.getName().trim().replaceAll("^\"|\"$", "")))) {
            isFavorite = true;
        }

        updateUI();
        updateHeartIcon();
        loadReviews();
    }

    private void loadReviews() {
        if (reviewsContainer == null || restaurant == null) {
            return;
        }
        reviewsContainer.getChildren().clear();

        // Ensure restaurants are loaded first, then reviews and replies
        UserSession.getInstance().loadAllRestaurants();
        UserSession.getInstance().loadReviewsFromCSV();
        UserSession.getInstance().loadRepliesFromCSV();

        java.util.List<Review> reviews = readReviewsFromCSV(restaurant.getName());
        if (reviews == null || reviews.isEmpty()) {
            reviewsContainer.setStyle("-fx-background-color: #3d3d3d; -fx-background: #3d3d3d;");
            javafx.scene.control.Label noReviewsLabel = new javafx.scene.control.Label(
                    "No reviews available for this restaurant.");
            noReviewsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-background-color: transparent;");
            reviewsContainer.getChildren().add(noReviewsLabel);
            ratingText.setText("Rating: 0.0");
            reviewsText.setText("Reviews: 0");
            return;
        }

        // Calculate average rating and total reviews
        double totalRating = 0;
        for (Review review : reviews) {
            totalRating += review.getRating();
        }
        double averageRating = totalRating / reviews.size();
        int totalReviews = reviews.size();

        // Update UI with calculated values
        ratingText.setText(String.format("Rating: %.1f", averageRating));
        reviewsText.setText("Reviews: " + totalReviews);

        // Limit to max 10 most recent reviews (assuming CSV order is oldest to newest)
        int start = Math.max(0, reviews.size() - 10);
        java.util.List<Review> recentReviews = reviews.subList(start, reviews.size());

        for (Review review : recentReviews) {
            javafx.scene.layout.HBox reviewBox = new javafx.scene.layout.HBox();
            reviewBox.setSpacing(10);
            reviewBox.setStyle("-fx-background-color: #444444; -fx-padding: 10; -fx-background-radius: 5;");
            reviewBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            // VBox for review texts
            javafx.scene.layout.VBox reviewTexts = new javafx.scene.layout.VBox();
            reviewTexts.setSpacing(5);

            // First line: name and stars
            javafx.scene.text.Text reviewerNameAndStars = new javafx.scene.text.Text(
                    review.getUsername() + " " + "★".repeat(review.getRating()));
            reviewerNameAndStars.setStyle("-fx-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

            // Below: review text
            javafx.scene.text.Text reviewText = new javafx.scene.text.Text(review.getText());
            reviewText.setStyle("-fx-fill: white; -fx-font-size: 14px;");

            reviewTexts.getChildren().addAll(reviewerNameAndStars, reviewText);

            // Reply button
            Button replyButton = new Button("Reply");
            replyButton.setStyle("-fx-background-color: #5a5a5a; -fx-text-fill: white; -fx-font-size: 14px;");
            replyButton.setOnAction(event -> {
                openReplyDialog(review);
            });

            // Set visibility of reply button only for owners of this specific restaurant
            UserSession session = theknife.UserSession.getInstance();
            boolean isRestaurantOwner = session.isRestaurantOwner(restaurant);
            replyButton.setVisible(isRestaurantOwner);

            // Add review texts and reply button to reviewBox
            reviewBox.getChildren().addAll(reviewTexts, replyButton);
            // Add spacing region to push reply button to the right
            javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
            javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            reviewBox.getChildren().add(1, spacer);

            reviewsContainer.getChildren().add(reviewBox);

            // Check if there's a reply to this review
            Reply reply = session.getReplyForReview(review.getUsername(), restaurant.getId());
            if (reply != null) {
                // Create reply box
                javafx.scene.layout.HBox replyBox = new javafx.scene.layout.HBox();
                replyBox.setSpacing(10);
                replyBox.setStyle(
                        "-fx-background-color: #555555; -fx-padding: 10; -fx-background-radius: 5; -fx-margin-left: 30;");
                replyBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                // VBox for reply content
                javafx.scene.layout.VBox replyContent = new javafx.scene.layout.VBox();
                replyContent.setSpacing(5);

                // Reply header
                javafx.scene.text.Text replyHeader = new javafx.scene.text.Text(
                        "Reply from " + reply.getOwnerUsername() + ":");
                replyHeader.setStyle("-fx-fill: #FFD700; -fx-font-weight: bold; -fx-font-size: 14px;");

                // Reply text
                javafx.scene.text.Text replyTextNode = new javafx.scene.text.Text(reply.getText());
                replyTextNode.setStyle("-fx-fill: white; -fx-font-size: 14px;");

                replyContent.getChildren().addAll(replyHeader, replyTextNode);
                replyBox.getChildren().add(replyContent);

                // Add margin to the left to indent the reply
                javafx.scene.layout.VBox.setMargin(replyBox, new javafx.geometry.Insets(5, 0, 0, 30));
                reviewsContainer.getChildren().add(replyBox);
            }
        }
    }

    private java.util.List<Review> readReviewsFromCSV(String restaurantName) {
        java.util.List<Review> reviews = new java.util.ArrayList<>();

        UserSession session = UserSession.getInstance();
        for (Review review : session.getReviews()) {
            String reviewRestaurantName = review.getRestaurantName();

            String cleanReviewName = reviewRestaurantName != null
                    ? reviewRestaurantName.trim().replaceAll("^\"|\"$", "")
                    : "";
            String cleanTargetName = restaurantName != null ? restaurantName.trim().replaceAll("^\"|\"$", "") : "";

            if (cleanReviewName.equalsIgnoreCase(cleanTargetName)) {
                reviews.add(review);
            }
        }

        return reviews;
    }

    @FXML
    private void handleFavorite() {
        if (restaurant != null) {
            isFavorite = !isFavorite;
            if (isFavorite) {
                UserSession.getInstance().addFavorite(restaurant);
            } else {
                UserSession.getInstance().removeFavorite(restaurant);
            }
            updateHeartIcon();
        }
    }

    private void updateHeartIcon() {
        if (heartImageView == null) {
            return;
        }
        if (isFavorite) {
            heartImageView.setImage(redHeartImage);
        } else {
            heartImageView.setImage(heartImage);
        }
        heartImageView.setVisible(true);
    }

    @FXML
    public void initialize() {
        // Ensure reviews container has correct background color
        if (reviewsContainer != null) {
            reviewsContainer.setStyle("-fx-background-color: #3d3d3d; -fx-background: #3d3d3d;");
        }

        // Ensure main scroll pane has correct background color
        if (mainScrollPane != null) {
            mainScrollPane.setStyle(
                    "-fx-background-color: #3d3d3d; -fx-background: #3d3d3d; -fx-control-inner-background: #3d3d3d;");
        }

        try {
            Image arrowImage = new Image(getClass().getResourceAsStream("/images/left-arrow.png"));
            arrowImageView.setImage(arrowImage);
            javafx.scene.effect.ColorAdjust whiteEffect = new javafx.scene.effect.ColorAdjust();
            whiteEffect.setBrightness(1.0);
            arrowImageView.setEffect(whiteEffect);

            arrowImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    javafx.stage.Stage stage = (javafx.stage.Stage) newScene.getWindow();
                    stage.setMaximized(true);
                    stage.setFullScreenExitHint("");
                    stage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
                    stage.setFullScreen(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            heartImage = new Image(getClass().getResourceAsStream("/images/heart.png"));
            redHeartImage = new Image(getClass().getResourceAsStream("/images/red-heart.png"));
            heartImageView.setImage(heartImage);

            heartImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    javafx.stage.Stage stage = (javafx.stage.Stage) newScene.getWindow();
                    stage.setMaximized(true);
                    stage.setFullScreenExitHint("");
                    stage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
                    stage.setFullScreen(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (descriptionLabel != null) {
            descriptionLabel.setBackground(javafx.scene.layout.Background.EMPTY);
            descriptionLabel.setMaxHeight(Double.MAX_VALUE);
            descriptionLabel.setPrefHeight(javafx.scene.control.Control.USE_COMPUTED_SIZE);
            descriptionLabel.setMinHeight(javafx.scene.control.Control.USE_PREF_SIZE);
        }

        if (bookBtn != null) {
            bookBtn.managedProperty().bind(bookBtn.visibleProperty());
            bookBtn.setOnAction(event -> openBookingsPage());
        }
        if (favoriteBtn != null) {
            favoriteBtn.managedProperty().bind(favoriteBtn.visibleProperty());
            favoriteBtn.setOnAction(event -> openFavoritesPage());
        }
        if (signInBtn != null) {
            signInBtn.managedProperty().bind(signInBtn.visibleProperty());
            signInBtn.setOnAction(event -> openLoginPage());
        }
        if (RestaurantsBtn != null) {
            RestaurantsBtn.managedProperty().bind(RestaurantsBtn.visibleProperty());
            RestaurantsBtn.setOnAction(event -> openRestaurantsPage());
        }
        if (RepliesBtn != null) {
            RepliesBtn.managedProperty().bind(RepliesBtn.visibleProperty());
            RepliesBtn.setOnAction(event -> openRepliesPage());
        }
        if (reviewBtn != null) {
            reviewBtn.managedProperty().bind(reviewBtn.visibleProperty());
        }
        if (guestButtons != null) {
            guestButtons.managedProperty().bind(guestButtons.visibleProperty());
        }
        if (userButtons != null) {
            userButtons.managedProperty().bind(userButtons.visibleProperty());
        }

        updateButtonVisibility();

        reviewBtn.setOnAction(event -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/view/reviews.fxml"));
                javafx.scene.Parent root = loader.load();

                theknife.ReviewsController controller = loader.getController();
                controller.setRestaurant(this.restaurant);
                controller.refreshReviews();

                javafx.stage.Stage stage = (javafx.stage.Stage) reviewBtn.getScene().getWindow();
                javafx.scene.Scene scene = reviewBtn.getScene();
                scene.setRoot(root);
                stage.setTitle("Reviews");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Call this in your initialize() or after user state is known
    private void updateButtonVisibility(boolean isLoggedIn) {
        bookButton.setVisible(isLoggedIn);
        reviewButton.setVisible(isLoggedIn);
    }

    public void updateButtonVisibility() {
        theknife.UserSession userSession = theknife.UserSession.getInstance();
        boolean isClient = "client".equalsIgnoreCase(userSession.getRole())
                || "cliente".equalsIgnoreCase(userSession.getRole());
        boolean isOwner = "owner".equalsIgnoreCase(userSession.getRole())
                || "ristoratore".equalsIgnoreCase(userSession.getRole());

        if (userSession.isNotLoggedIn()) {
            if (guestButtons != null) {
                guestButtons.setVisible(true);
            }
            if (userButtons != null) {
                userButtons.setVisible(false);
            }
            favoriteBtn.setVisible(false);
            bookBtn.setVisible(false);
            reviewBtn.setVisible(false);
            if (heartButton != null) {
                heartButton.setVisible(false);
            }
            if (RestaurantsBtn != null) {
                RestaurantsBtn.setVisible(false);
            }
            if (RepliesBtn != null) {
                RepliesBtn.setVisible(false);
            }
            if (bookBtn != null) {
                bookBtn.setVisible(false);
            }
            if (favoriteBtn != null) {
                favoriteBtn.setVisible(false);
            }
            if (signInBtn != null) {
                signInBtn.setVisible(true);
            }
            if (bookButton != null)
                bookButton.setVisible(false);
            if (reviewButton != null)
                reviewButton.setVisible(false);
        } else if (isClient) {
            if (guestButtons != null) {
                guestButtons.setVisible(false);
            }
            if (userButtons != null) {
                userButtons.setVisible(true);
            }
            favoriteBtn.setVisible(true);
            bookBtn.setVisible(true);
            reviewBtn.setVisible(true);
            if (heartButton != null) {
                heartButton.setVisible(true);
            }
            if (RestaurantsBtn != null) {
                RestaurantsBtn.setVisible(false);
            }
            if (RepliesBtn != null) {
                RepliesBtn.setVisible(false);
            }
            if (bookBtn != null) {
                bookBtn.setVisible(true);
            }
            if (favoriteBtn != null) {
                favoriteBtn.setVisible(true);
            }
            if (signInBtn != null) {
                signInBtn.setVisible(false);
            }
            if (bookButton != null)
                bookButton.setVisible(true);
            if (reviewButton != null)
                reviewButton.setVisible(true);
        } else if (isOwner) {
            if (guestButtons != null) {
                guestButtons.setVisible(false);
            }
            if (userButtons != null) {
                userButtons.setVisible(true);
            }
            favoriteBtn.setVisible(false);
            bookBtn.setVisible(false);
            reviewBtn.setVisible(false);
            if (heartButton != null) {
                heartButton.setVisible(false);
            }
            if (RestaurantsBtn != null) {
                RestaurantsBtn.setVisible(true);
            }
            if (RepliesBtn != null) {
                RepliesBtn.setVisible(true);
            }
            if (bookBtn != null) {
                bookBtn.setVisible(false);
            }
            if (favoriteBtn != null) {
                favoriteBtn.setVisible(false);
            }
            if (signInBtn != null) {
                signInBtn.setVisible(false);
            }
            if (bookButton != null)
                bookButton.setVisible(false);
            if (reviewButton != null)
                reviewButton.setVisible(false);
        } else {
            if (guestButtons != null) {
                guestButtons.setVisible(false);
            }
            if (userButtons != null) {
                userButtons.setVisible(false);
            }
            favoriteBtn.setVisible(false);
            bookBtn.setVisible(false);
            reviewBtn.setVisible(false);
            if (bookButton != null)
                bookButton.setVisible(false);
            if (reviewButton != null)
                reviewButton.setVisible(false);
        }
    }

    private void updateUI() {
        if (restaurant == null)
            return;

        nameText.setText(restaurant.getName());
        cuisineText.setText("Cuisine: " + restaurant.getCuisine());
        // Remove setting rating and reviews here to avoid overwriting calculated values
        // ratingText.setText(String.format("Rating: %.1f", restaurant.getRating()));
        // reviewsText.setText("Reviews: " + restaurant.getReviews());
        priceText.setText("Price: " + restaurant.getPrice());
        distanceText.setText("Location: " + restaurant.getLocation());
        addressText.setText("Address: " + restaurant.getAddress());
        phoneNumberText.setText("Phone: " + restaurant.getPhoneNumber());
        descriptionLabel.setText(restaurant.getDescription());
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
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
            javafx.scene.Parent root = loader.load();

            javafx.stage.Stage stage = (javafx.stage.Stage) closeButton.getScene().getWindow();
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
    private void handleBook() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/view/bookings_add.fxml"));
            javafx.scene.Parent root = loader.load();

            BookingsAddController controller = loader.getController();
            if (restaurant != null) {
                controller.setRestaurantName(restaurant.getName());
            }

            // Set callback to refresh if needed (though this page doesn't show bookings
            // list)
            controller.setOnBookingChanged(() -> {
                // No UI refresh needed on this page since it doesn't show bookings
                // The booking will be visible when user navigates to bookings page
            });

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.setTitle("Book Now");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initOwner(bookBtn.getScene().getWindow());
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLeaveReview() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/review_add.fxml"));
            javafx.scene.Parent root = loader.load();

            ReviewAddController controller = loader.getController();
            controller.setRestaurantName(restaurant != null ? restaurant.getName() : null);

            // Set callback to refresh the reviews list when a review is added
            controller.setOnReviewAdded(() -> {
                updateReviewsList(); // Refresh the reviews display
            });

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.setTitle("Leave a Review");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initOwner(reviewBtn.getScene().getWindow());
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openLoginPage() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/login.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) signInBtn.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setMaximized(true);
            stage.setFullScreen(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFavoritesPage() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/favorites.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) favoriteBtn.getScene().getWindow();
            javafx.scene.Scene scene = favoriteBtn.getScene();
            scene.setRoot(root);
            stage.setTitle("Favorites");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openBookingsPage() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/bookings.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) bookBtn.getScene().getWindow();
            javafx.scene.Scene scene = bookBtn.getScene();
            scene.setRoot(root);
            stage.setTitle("Bookings");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openRestaurantsPage() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/view/restaurants.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) RestaurantsBtn.getScene().getWindow();
            javafx.scene.Scene scene = RestaurantsBtn.getScene();
            scene.setRoot(root);
            stage.setTitle("My Restaurants");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void openReplyDialog(Review review) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/reply_add.fxml"));
            javafx.scene.Parent root = loader.load();

            ReplyAddController controller = loader.getController();
            controller.setReviewAndRestaurant(review, restaurant);

            // Set callback to refresh reviews when reply is added
            controller.setOnReplyAdded(() -> loadReviews());

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.setTitle("Reply to Review");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initOwner(closeButton.getScene().getWindow());
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openRepliesPage() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/view/replies.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) RepliesBtn.getScene().getWindow();
            javafx.scene.Scene scene = RepliesBtn.getScene();
            scene.setRoot(root);
            stage.setTitle("My Replies");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateReviewsList() {
        loadReviews();
    }
}
