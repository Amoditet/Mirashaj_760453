/**
 * 
 * Erik Mirashaj Matricola: 760453 Sede: VA Email: emirashaj@studenti.uninsubria.it
 * Igor Gorchynskyi Matricola: 757184 Sede: VA
 * Lorenzo Mujeci Matricola: 757597 Sede: VA
 * Matteo Nika Matricola: 762540 Sede: VA
 * 
 */

package theknife;

import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import theknife.UserSession;
import theknife.Review;
import theknife.ReviewsController;

public class ReviewListCell extends ListCell<Review> {
    private VBox content;
    private Text restaurantNameText;
    private HBox starsBox;
    private Text reviewText;
    private Button deleteButton;

    private Image filledStar;
    private Image emptyStar;

    private Review lastClickedReview = null;
    private long lastClickTime = 0;

    public ReviewListCell() {
        super();

        restaurantNameText = new Text();
        restaurantNameText.setFont(Font.font("System", FontWeight.BOLD, 22));
        restaurantNameText.setStyle("-fx-padding: 0 0 10 0;");

        starsBox = new HBox(4);
        filledStar = new Image(getClass().getResourceAsStream("/images/yellow-star.png"));
        emptyStar = new Image(getClass().getResourceAsStream("/images/star.png"));

        reviewText = new Text();
        reviewText.setWrappingWidth(400);
        reviewText.setStyle("-fx-padding: 10 0 0 0;");

        deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("red-filter-button");

        HBox buttonsBox = new HBox(deleteButton);
        buttonsBox.setStyle("-fx-padding: 5 0 0 0;");

        content = new VBox(restaurantNameText, starsBox, reviewText, buttonsBox);
        content.setSpacing(2);

        deleteButton.setOnAction(event -> {
            event.consume();
            Review review = getItem();
            if (review != null) {
                // Remove from UserSession and save to CSV
                UserSession.getInstance().removeReview(review);

                // Remove from the current list view immediately
                getListView().getItems().remove(review);
                getListView().refresh();
            }
        });

        this.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> {
            if (evt.getTarget() instanceof Button)
                return;
            Review selected = getItem();
            if (selected != null && !isEmpty()) {
                long currentTime = System.currentTimeMillis();
                if (selected.equals(lastClickedReview) && (currentTime - lastClickTime) < 500) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/restaurant_detail.fxml"));
                        Parent root = loader.load();

                        RestaurantDetailController controller = loader.getController();
                        controller.setRestaurant(findRestaurantForReview(selected));

                        Scene scene = getListView().getScene();
                        scene.setRoot(root);

                        Stage stage = (Stage) scene.getWindow();
                        stage.setTitle(
                                findRestaurantForReview(selected) != null ? findRestaurantForReview(selected).getName()
                                        : "Restaurant Detail");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                lastClickedReview = selected;
                lastClickTime = currentTime;
            }
        });
    }

    private void updateStars(int rating) {
        starsBox.getChildren().clear();
        for (int i = 1; i <= 5; i++) {
            ImageView starView = new ImageView(i <= rating ? filledStar : emptyStar);
            starView.setFitWidth(32);
            starView.setFitHeight(32);
            starsBox.getChildren().add(starView);
        }
    }

    private Restaurant findRestaurantForReview(Review review) {
        if (review == null) {
            return null;
        }

        int reviewRestaurantId = review.getRestaurantId();
        if (reviewRestaurantId != -1) {
            for (Restaurant r : UserSession.getInstance().getAllRestaurants()) {
                if (r.getId() == reviewRestaurantId) {
                    return r;
                }
            }
        }

        String reviewRestaurantName = review.getRestaurantName();
        if (reviewRestaurantName != null && !reviewRestaurantName.equals("Unknown Restaurant")) {
            String cleanReviewName = reviewRestaurantName.trim().replaceAll("^\"|\"$", "");
            for (Restaurant r : UserSession.getInstance().getAllRestaurants()) {
                if (r.getName() != null) {
                    String cleanRestaurantName = r.getName().trim().replaceAll("^\"|\"$", "");
                    if (cleanRestaurantName.equalsIgnoreCase(cleanReviewName)) {
                        return r;
                    }
                }
            }
        }

        return null;
    }

    @Override
    protected void updateItem(Review item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            setText(null);
        } else {
            Restaurant restaurant = findRestaurantForReview(item);
            restaurantNameText.setText(restaurant != null ? restaurant.getName() : "Unknown Restaurant");
            updateStars(item.getRating());
            reviewText.setText(item.getText());
            setGraphic(content);
        }
    }
}
