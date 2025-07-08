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
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

public class ReviewAddController {

    private String restaurantName;
    private Runnable onReviewAdded; // Callback for when review is successfully added

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public void setOnReviewAdded(Runnable callback) {
        this.onReviewAdded = callback;
    }

    @FXML
    private ImageView star1;
    @FXML
    private ImageView star2;
    @FXML
    private ImageView star3;
    @FXML
    private ImageView star4;
    @FXML
    private ImageView star5;

    @FXML
    private TextArea reviewTextArea;

    @FXML
    private Button submitButton;

    @FXML
    private Button closeButton;

    private int rating = 0;
    private Image emptyStar;
    private Image filledStar;

    @FXML
    public void initialize() {
        closeButton.setOnAction(event -> handleExitAction());

        emptyStar = new Image(getClass().getResourceAsStream("/images/star.png"));
        filledStar = new Image(getClass().getResourceAsStream("/images/yellow-star.png"));
        setRating(0);
    }

    public void setReview(Review review) {
        if (review != null) {
            reviewTextArea.setText(review.getText());
            setRating(review.getRating());
        } else {
            reviewTextArea.clear();
            setRating(0);
        }
    }

    public void setRestaurant(Restaurant restaurant) {
        if (restaurant != null) {
            setRestaurantName(restaurant.getName());
        }
    }

    @FXML
    private void handleStarClick(MouseEvent event) {
        ImageView clickedStar = (ImageView) event.getSource();
        int starNumber = 0;
        if (clickedStar == star1)
            starNumber = 1;
        else if (clickedStar == star2)
            starNumber = 2;
        else if (clickedStar == star3)
            starNumber = 3;
        else if (clickedStar == star4)
            starNumber = 4;
        else if (clickedStar == star5)
            starNumber = 5;

        setRating(starNumber);
    }

    private void setRating(int rating) {
        this.rating = rating;
        star1.setImage(rating >= 1 ? filledStar : emptyStar);
        star2.setImage(rating >= 2 ? filledStar : emptyStar);
        star3.setImage(rating >= 3 ? filledStar : emptyStar);
        star4.setImage(rating >= 4 ? filledStar : emptyStar);
        star5.setImage(rating >= 5 ? filledStar : emptyStar);
    }

    @FXML
    private void handleSubmit() {
        String reviewText = reviewTextArea.getText();

        UserSession session = UserSession.getInstance();
        int restaurantId = -1;
        if (restaurantName != null) {
            restaurantId = session.getRestaurantIdByName(restaurantName);
        }
        session.addOrUpdateReview(restaurantId,
                new Review(session.getUsername(), restaurantName, restaurantId, rating, reviewText));

        // Notify parent that a review has been added
        if (onReviewAdded != null) {
            onReviewAdded.run();
        }

        handleExitAction();
    }

    @FXML
    private void handleExitAction() {
        if (closeButton != null && closeButton.getScene() != null) {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        }
    }
}
