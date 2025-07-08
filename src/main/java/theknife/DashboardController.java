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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class DashboardController {

    @FXML
    private Text locationText;
    @FXML
    private Text currentPageText;
    @FXML
    private Button exitBtn;
    @FXML
    private BorderPane rootPane;

    @FXML
    private HBox guestButtons;
    @FXML
    private HBox userButtons;

    @FXML
    private Button signInBtn;
    @FXML
    private Button favoriteBtn;
    @FXML
    private Button reviewBtn;
    @FXML
    private Button bookBtn;
    @FXML
    private Button restaurantsBtn;
    @FXML
    private Button repliesBtn;
    @FXML
    private Button aboutBtn;

    @FXML
    private TextField searchInput;

    @FXML
    private GridPane restaurantGrid;

    private List<Restaurant> allRestaurants = new ArrayList<>();

    private PauseTransition searchPause;

    private static final int PAGE_SIZE = 20;
    private int currentPage = 1;

    @FXML
    private Button loadMoreBtn;
    @FXML
    private Button loadLessBtn;

    @FXML
    private void handleExitAction() {
        Platform.exit();
        System.exit(0);
    }

    private void filterAndRenderRestaurants() {
        String searchTerm = searchInput.getText() != null ? searchInput.getText().toLowerCase().trim() : "";
        String locationTerm = locationText.getText() != null ? locationText.getText().toLowerCase() : "";

        List<Restaurant> filtered = allRestaurants.stream()
                .filter(r -> {
                    boolean searchMatch = searchTerm.isEmpty() || matchesSearchTerm(r, searchTerm);
                    return searchMatch;
                })
                .filter(r -> {
                    boolean locationMatch = true;
                    if (!locationTerm.isEmpty()) {
                        String cityPart = locationTerm;
                        if (locationTerm.contains(",")) {
                            cityPart = locationTerm.split(",")[0].trim().toLowerCase();
                        } else {
                            cityPart = locationTerm.trim().toLowerCase();
                        }
                        String restaurantLocation = r.getLocation() != null ? r.getLocation().toLowerCase().trim() : "";
                        locationMatch = restaurantLocation.contains(cityPart);
                    }
                    return locationMatch;
                })
                .collect(Collectors.toList());

        int fromIndex = (currentPage - 1) * PAGE_SIZE;
        int toIndex = Math.min(currentPage * PAGE_SIZE, filtered.size());
        List<Restaurant> paginatedList = filtered.subList(fromIndex, toIndex);

        renderRestaurants(paginatedList);

        if (toIndex < filtered.size()) {
            loadMoreBtn.setVisible(true);
        } else {
            loadMoreBtn.setVisible(false);
        }
    }

    private void resetPagination() {
        currentPage = 1;
    }

    @FXML
    public void initialize() {
        theknife.UserSession userSession = theknife.UserSession.getInstance();

        if (userSession.isNotLoggedIn()) {
            locationText.setText("Rome");
            loadRestaurants();
            resetPagination();
            filterAndRenderRestaurants();
        } else if (userSession.getLocation() != null && !userSession.getLocation().isEmpty()) {
            locationText.setText(userSession.getLocation());
            loadRestaurants();
            resetPagination();
            filterAndRenderRestaurants();
        } else {
            locationText.setText("");
        }

        Platform.runLater(() -> rootPane.requestFocus());

        searchPause = new PauseTransition(Duration.millis(300));
        searchInput.textProperty().addListener((obs, oldText, newText) -> {
            searchPause.stop();
            searchPause.setOnFinished(event -> {
                resetPagination();
                filterAndRenderRestaurants();
            });
            searchPause.playFromStart();
        });

        signInBtn.setOnAction(event -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/login.fxml"));
                javafx.scene.Parent root = loader.load();
                javafx.stage.Stage stage = (javafx.stage.Stage) signInBtn.getScene().getWindow();
                javafx.scene.Scene scene = new javafx.scene.Scene(root);
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.setFullScreen(true);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        favoriteBtn.setOnAction(event -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/view/favorites.fxml"));
                javafx.scene.Parent root = loader.load();

                theknife.FavoritesController controller = loader.getController();
                controller.refreshFavorites();

                javafx.stage.Stage stage = (javafx.stage.Stage) favoriteBtn.getScene().getWindow();
                javafx.scene.Scene scene = favoriteBtn.getScene();
                scene.setRoot(root);
                stage.setTitle("Favorites");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        bookBtn.setOnAction(event -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/view/bookings.fxml"));
                javafx.scene.Parent root = loader.load();
                javafx.stage.Stage stage = (javafx.stage.Stage) bookBtn.getScene().getWindow();
                javafx.scene.Scene scene = bookBtn.getScene();
                scene.setRoot(root);
                stage.setTitle("Bookings");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        reviewBtn.setOnAction(event -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/view/reviews.fxml"));
                javafx.scene.Parent root = loader.load();

                theknife.ReviewsController controller = loader.getController();
                controller.refreshReviews();

                javafx.stage.Stage stage = (javafx.stage.Stage) reviewBtn.getScene().getWindow();
                javafx.scene.Scene scene = reviewBtn.getScene();
                scene.setRoot(root);
                stage.setTitle("Reviews");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        guestButtons.managedProperty().bind(guestButtons.visibleProperty());
        userButtons.managedProperty().bind(userButtons.visibleProperty());

        signInBtn.managedProperty().bind(signInBtn.visibleProperty());
        bookBtn.managedProperty().bind(bookBtn.visibleProperty());
        favoriteBtn.managedProperty().bind(favoriteBtn.visibleProperty());
        reviewBtn.managedProperty().bind(reviewBtn.visibleProperty());
        restaurantsBtn = (javafx.scene.control.Button) rootPane.lookup("#restaurantsBtn");
        if (restaurantsBtn != null) {
            restaurantsBtn.managedProperty().bind(restaurantsBtn.visibleProperty());
            restaurantsBtn.setOnAction(event -> openRestaurantsPage());
        } else {
            restaurantsBtn = new javafx.scene.control.Button();
        }

        if (repliesBtn != null) {
            repliesBtn.managedProperty().bind(repliesBtn.visibleProperty());
            repliesBtn.setOnAction(event -> openRepliesPage());
        }

        updateButtonVisibility();

        currentPageText.setText("1");

        loadMoreBtn.setOnAction(event -> {
            currentPage++;
            currentPageText.setText(String.valueOf(currentPage));
            filterAndRenderRestaurants();
            updateLoadLessButtonVisibility();
        });

        loadLessBtn.setOnAction(event -> {
            if (currentPage > 1) {
                currentPage--;
                currentPageText.setText(String.valueOf(currentPage));
                filterAndRenderRestaurants();
                updateLoadLessButtonVisibility();
            }
        });

        updateLoadLessButtonVisibility();
    }

    private void updateLoadLessButtonVisibility() {
        loadLessBtn.setVisible(currentPage > 1);
    }

    public void updateButtonVisibility() {
        theknife.UserSession userSession = theknife.UserSession.getInstance();

        if (userSession.isNotLoggedIn()) {
            signInBtn.setVisible(true);
            favoriteBtn.setVisible(false);
            bookBtn.setVisible(false);
            reviewBtn.setVisible(false);
            restaurantsBtn.setVisible(false);
            if (repliesBtn != null) {
                repliesBtn.setVisible(false);
            }

        } else {
            String role = userSession.getRole();
            if ("owner".equalsIgnoreCase(role) || "ristoratore".equalsIgnoreCase(role)) {
                signInBtn.setVisible(false);
                favoriteBtn.setVisible(false);
                bookBtn.setVisible(false);
                reviewBtn.setVisible(false);
                restaurantsBtn.setVisible(true);
                if (repliesBtn != null) {
                    repliesBtn.setVisible(true);
                }

            } else if ("client".equalsIgnoreCase(role) || "cliente".equalsIgnoreCase(role)) {
                signInBtn.setVisible(false);
                favoriteBtn.setVisible(true);
                bookBtn.setVisible(true);
                reviewBtn.setVisible(true);
                restaurantsBtn.setVisible(false);
                if (repliesBtn != null) {
                    repliesBtn.setVisible(false);
                }
            }
        }
    }

    private void loadRestaurants() {
        allRestaurants.clear();

        UserSession.getInstance().loadAllRestaurants();
        UserSession.getInstance().loadReviewsFromCSV();

        String workingDir = System.getProperty("user.dir");
        java.nio.file.Path michelinCsvPath = java.nio.file.Paths.get(workingDir, "data", "michelin_my_maps.csv");
        java.nio.file.Path restaurantsListCsvPath = java.nio.file.Paths.get(workingDir, "data", "restaurants_list.csv");

        try (java.io.BufferedReader br = java.nio.file.Files.newBufferedReader(michelinCsvPath)) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (fields.length < 14) {
                    continue;
                }

                try {
                    int id = 50000 + allRestaurants.size();
                    String name = fields[0].trim().replaceAll("^\"|\"$", "");
                    String address = fields[1].trim().replaceAll("^\"|\"$", "");
                    String location = fields[2].trim().replaceAll("^\"|\"$", "");
                    if (location.contains(",")) {
                        location = location.split(",")[0].trim().toLowerCase();
                    } else {
                        location = location.trim().toLowerCase();
                    }
                    String price = fields[3].trim().replaceAll("^\"|\"$", "");
                    String cuisine = fields[4].trim().replaceAll("^\"|\"$", "");
                    double rating = 0.0;
                    int reviews = 0;
                    String phoneNumber = fields[7].trim().replaceAll("^\"|\"$", "");
                    String Url = fields[8].trim().replaceAll("^\"|\"$", "");
                    String award = fields[10].trim().replaceAll("^\"|\"$", "");
                    String greenStar = fields[11].trim().replaceAll("^\"|\"$", "");
                    String facilities = fields[12].trim().replaceAll("^\"|\"$", "");
                    String description = fields.length > 13 ? fields[13].trim().replaceAll("^\"|\"$", "") : "";

                    Restaurant restaurant = new Restaurant(id, name, address, rating, reviews, location, cuisine, price,
                            Url, phoneNumber, award, greenStar, facilities, description, null);
                    allRestaurants.add(restaurant);
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (java.io.BufferedReader br = java.nio.file.Files.newBufferedReader(restaurantsListCsvPath)) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (fields.length < 7) {
                    continue;
                }

                try {
                    int id = Integer.parseInt(fields[0].trim());
                    String username = fields[1].trim().replaceAll("^\"|\"$", "");
                    String name = fields[2].trim();
                    String address = fields[3].trim();
                    String location = fields[4].trim();
                    if (location.contains(",")) {
                        location = location.split(",")[0].trim().toLowerCase();
                    } else {
                        location = location.trim().toLowerCase();
                    }
                    String price = fields[5].trim();
                    String cuisine = fields[6].trim();
                    String phoneNumber = fields.length > 9 ? fields[9].trim() : "";
                    String award = fields.length > 12 ? fields[12].trim() : "";
                    String greenStar = fields.length > 13 ? fields[13].trim() : "";
                    String facilities = fields.length > 14 ? fields[14].trim() : "";
                    String description = fields.length > 15 ? fields[15].trim() : "";

                    Restaurant restaurant = new Restaurant(id, name, address, 0.0, 0, location, cuisine, price,
                            "", phoneNumber, award, greenStar, facilities, description, username);
                    allRestaurants.add(restaurant);
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderRestaurants(List<Restaurant> restaurants) {
        restaurantGrid.getChildren().clear();
        int col = 0;
        int row = 0;

        if (restaurants.isEmpty()) {
            Text noResultsText = new Text("No restaurants found matching your criteria.");
            noResultsText.setStyle("-fx-fill: #dddddd; -fx-font-size: 16px;");
            restaurantGrid.add(noResultsText, 0, 0);
            GridPane.setColumnSpan(noResultsText, 4);
            return;
        }

        for (Restaurant r : restaurants) {
            VBox card = createRestaurantCard(r);
            restaurantGrid.add(card, col, row);
            col++;
            if (col >= 4) {
                col = 0;
                row++;
            }
        }
    }

    private double calculateAverageRating(Restaurant restaurant) {
        List<Review> reviews = UserSession.getInstance().getReviews();
        List<Review> restaurantReviews = new ArrayList<>();

        for (Review review : reviews) {
            if (review.getRestaurantId() == restaurant.getId() ||
                    (review.getRestaurantName() != null && restaurant.getName() != null &&
                            review.getRestaurantName().trim().replaceAll("^\"|\"$", "")
                                    .equalsIgnoreCase(restaurant.getName().trim().replaceAll("^\"|\"$", "")))) {
                restaurantReviews.add(review);
            }
        }

        if (restaurantReviews.isEmpty()) {
            return 0.0;
        }

        double totalRating = 0;
        for (Review review : restaurantReviews) {
            totalRating += review.getRating();
        }

        return totalRating / restaurantReviews.size();
    }

    private int getReviewCount(Restaurant restaurant) {
        List<Review> reviews = UserSession.getInstance().getReviews();
        int count = 0;

        for (Review review : reviews) {
            if (review.getRestaurantId() == restaurant.getId() ||
                    (review.getRestaurantName() != null && restaurant.getName() != null &&
                            review.getRestaurantName().trim().replaceAll("^\"|\"$", "")
                                    .equalsIgnoreCase(restaurant.getName().trim().replaceAll("^\"|\"$", "")))) {
                count++;
            }
        }

        return count;
    }

    private VBox createRestaurantCard(Restaurant r) {
        VBox card = new VBox();
        card.getStyleClass().add("restaurant-card");
        card.setSpacing(5);
        card.setPrefHeight(220);
        card.setMinHeight(220);
        card.setMaxHeight(220);
        card.setPrefWidth(220);
        card.setStyle(
                "-fx-background-color: #333333; -fx-border-color: #444444; -fx-border-width: 1px; -fx-background-radius: 8px; -fx-border-radius: 8px; -fx-padding: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10,0,0,2);");

        Text title = new Text(r.getName());
        title.getStyleClass().add("card-title");
        title.setStyle("-fx-fill: #eeeeee; -fx-font-weight: bold; -fx-font-size: 16px;");
        title.setWrappingWidth(200);

        Text cuisine = new Text("Cuisine: " + r.getCuisine());
        cuisine.getStyleClass().add("card-cuisine");
        cuisine.setStyle("-fx-fill: #bbbbbb; -fx-font-size: 13px;");
        cuisine.setWrappingWidth(200);

        double actualRating = calculateAverageRating(r);
        int reviewCount = getReviewCount(r);
        Text rating = new Text(String.format("Rating: %.1f (%d reviews)", actualRating, reviewCount));
        rating.getStyleClass().add("card-rating");
        rating.setStyle("-fx-fill: #bbbbbb; -fx-font-size: 13px;");

        Text price = new Text("Price: " + r.getPrice());
        price.getStyleClass().add("card-price");
        price.setStyle("-fx-fill: #bbbbbb; -fx-font-size: 13px;");

        card.getChildren().addAll(title, cuisine, rating, price);

        card.setOnMouseClicked(event -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/view/restaurant_detail.fxml"));
                javafx.scene.Parent root = loader.load();

                theknife.RestaurantDetailController controller = loader.getController();
                controller.setRestaurant(r);

                javafx.scene.Scene scene = card.getScene();
                scene.setRoot(root);

                javafx.stage.Stage stage = (javafx.stage.Stage) scene.getWindow();
                stage.setTitle(r.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return card;
    }

    public void setLocation(String location) {
        if (location != null && !location.isEmpty()) {
            locationText.setText(location);
            resetPagination();
            loadRestaurants();
            filterAndRenderRestaurants();
        }
    }

    public void setCurrentPage(String currentPage) {
        currentPageText.setText(currentPage);
        loadRestaurants();
        filterAndRenderRestaurants();
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

    @FXML
    private void handleLocationTextClick() {
        String currentLocation = locationText.getText();
        String cityOnly = currentLocation.contains(",") ? currentLocation.split(",")[0].trim() : currentLocation.trim();

        javafx.scene.control.TextField editField = new javafx.scene.control.TextField(cityOnly);
        editField.setPrefWidth(locationText.getBoundsInLocal().getWidth() + 20);

        javafx.scene.Parent parent = locationText.getParent();
        if (parent instanceof javafx.scene.layout.Pane) {
            javafx.scene.layout.Pane pane = (javafx.scene.layout.Pane) parent;
            int index = pane.getChildren().indexOf(locationText);
            pane.getChildren().remove(locationText);
            pane.getChildren().add(index, editField);
            editField.requestFocus();
            editField.selectAll();

            editField.setOnAction(event -> commitLocationEdit(editField, pane, index));
            editField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    commitLocationEdit(editField, pane, index);
                }
            });
        }
    }

    private void commitLocationEdit(javafx.scene.control.TextField editField, javafx.scene.layout.Pane pane,
            int index) {
        String newLocation = editField.getText();
        if (newLocation != null && !newLocation.trim().isEmpty()) {
            String trimmedLocation = newLocation.trim();
            setLocation(trimmedLocation);

            UserSession userSession = UserSession.getInstance();
            if (userSession.isLoggedIn()) {
                userSession.updateUserLocation(trimmedLocation);
            }
        }
        pane.getChildren().remove(editField);
        if (!pane.getChildren().contains(locationText)) {
            pane.getChildren().add(index, locationText);
        }
    }

    private void openRestaurantsPage() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/view/restaurants.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) rootPane.getScene().getWindow();
            javafx.scene.Scene scene = rootPane.getScene();
            scene.setRoot(root);
            stage.setTitle("My Restaurants");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openRepliesPage() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/view/replies.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) rootPane.getScene().getWindow();
            javafx.scene.Scene scene = rootPane.getScene();
            scene.setRoot(root);
            stage.setTitle("My Replies");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean matchesSearchTerm(Restaurant restaurant, String searchTerm) {
        if (restaurant.getName() != null) {
            String restaurantName = restaurant.getName().toLowerCase();
            if (containsCompleteWord(restaurantName, searchTerm)) {
                return true;
            }
        }

        if (restaurant.getCuisine() != null) {
            String cuisine = restaurant.getCuisine().toLowerCase();
            if (containsCompleteWord(cuisine, searchTerm)) {
                return true;
            }
        }

        return false;
    }

    private boolean containsCompleteWord(String text, String word) {
        if (text == null || word == null || word.isEmpty()) {
            return false;
        }

        String regex = "\\b" + java.util.regex.Pattern.quote(word) + "\\b";
        return java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.CASE_INSENSITIVE).matcher(text).find();
    }
}
