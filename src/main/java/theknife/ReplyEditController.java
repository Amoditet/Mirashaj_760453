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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ReplyEditController {

    @FXML
    private Button closeButton;
    @FXML
    private Label reviewInfoLabel;
    @FXML
    private Label originalReviewLabel;
    @FXML
    private TextArea replyTextArea;
    @FXML
    private Button saveButton;

    private Reply originalReply;

    public void setReply(Reply reply) {
        this.originalReply = reply;

        // Load the original review information
        UserSession session = UserSession.getInstance();
        Review originalReview = session.getReviews().stream()
                .filter(review -> review.getUsername().equals(reply.getReviewUsername()) &&
                        review.getRestaurantId() == reply.getRestaurantId())
                .findFirst()
                .orElse(null);

        if (originalReview != null) {
            reviewInfoLabel.setText(
                    "Replying to: " + originalReview.getUsername() + " - " + originalReview.getRestaurantName());
            originalReviewLabel
                    .setText("\"" + originalReview.getText() + "\" - " + "★".repeat(originalReview.getRating()));
        } else {
            reviewInfoLabel.setText("Replying to: " + reply.getReviewUsername());
            originalReviewLabel.setText("Original review not found");
        }

        // Set the current reply text
        replyTextArea.setText(reply.getText());
    }

    @FXML
    private void handleSave() {
        String newReplyText = replyTextArea.getText().trim();

        if (newReplyText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Empty Reply");
            alert.setContentText("Please enter a reply before saving.");
            alert.showAndWait();
            return;
        }

        // Create new reply with updated text
        Reply updatedReply = new Reply(
                originalReply.getOwnerUsername(),
                originalReply.getReviewUsername(),
                originalReply.getRestaurantId(),
                newReplyText);

        // Update the reply in UserSession
        UserSession.getInstance().updateReply(originalReply, updatedReply);

        // Close the dialog
        handleClose();
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
