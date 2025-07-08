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
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ReplyAddController {

    @FXML
    private Label reviewLabel;
    @FXML
    private TextArea replyTextArea;
    @FXML
    private Button submitButton;
    @FXML
    private Button closeButton;

    private Review review;
    private Restaurant restaurant;
    private Runnable onReplyAdded; // Callback for when reply is successfully added

    public void setReviewAndRestaurant(Review review, Restaurant restaurant) {
        this.review = review;
        this.restaurant = restaurant;
        updateUI();
    }

    public void setOnReplyAdded(Runnable callback) {
        this.onReplyAdded = callback;
    }

    private void updateUI() {
        if (review != null) {
            reviewLabel.setText("Replying to review by " + review.getUsername() + ": \"" + review.getText() + "\"");
        }
    }

    @FXML
    private void handleSubmit() {
        if (review == null || restaurant == null) {
            return;
        }

        String replyText = replyTextArea.getText().trim();
        if (replyText.isEmpty()) {
            return;
        }

        UserSession session = UserSession.getInstance();
        Reply reply = new Reply(session.getUsername(), review.getUsername(), restaurant.getId(), replyText);
        session.addReply(reply);

        if (onReplyAdded != null) {
            onReplyAdded.run(); // Notify parent that a reply has been added
        }

        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
