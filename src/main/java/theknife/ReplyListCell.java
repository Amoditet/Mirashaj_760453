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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class ReplyListCell extends ListCell<Reply> {
    private VBox content;
    private Text headerText;
    private Text originalReviewLabel;
    private Text originalReviewText;
    private Text starsText;
    private Text replyLabel;
    private Text replyText;
    private Button editButton;
    private Button deleteButton;

    public ReplyListCell() {
        super();

        headerText = new Text();
        headerText.setFont(Font.font("System", FontWeight.BOLD, 18));
        headerText.setStyle("-fx-fill: #FFD700;");

        originalReviewLabel = new Text("Original Review:");
        originalReviewLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        originalReviewLabel.setStyle("-fx-fill: #CCCCCC;");

        originalReviewText = new Text();
        originalReviewText.setWrappingWidth(800);
        originalReviewText.setStyle("-fx-fill: #CCCCCC; -fx-font-size: 14px;");

        starsText = new Text();
        starsText.setStyle("-fx-fill: #FFD700; -fx-font-size: 14px;");

        replyLabel = new Text("Your Reply:");
        replyLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        replyLabel.setStyle("-fx-fill: white;");

        replyText = new Text();
        replyText.setWrappingWidth(800);
        replyText.setStyle("-fx-fill: white; -fx-font-size: 14px;");

        editButton = new Button("Edit");
        editButton.getStyleClass().add("small-filter-button");
        editButton.setOnAction(event -> {
            event.consume();
            Reply reply = getItem();
            if (reply != null) {
                editReply(reply);
            }
        });

        deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("red-filter-button");
        deleteButton.setOnAction(event -> {
            event.consume();
            Reply reply = getItem();
            if (reply != null) {
                deleteReply(reply);
            }
        });

        HBox buttonsBox = new HBox(15);
        buttonsBox.getChildren().addAll(editButton, deleteButton);
        buttonsBox.setStyle("-fx-padding: 10 0 0 0; -fx-alignment: center-left;");

        content = new VBox();
        content.setSpacing(8);
        content.setStyle(
                "-fx-background-color: #444444; -fx-padding: 20; -fx-background-radius: 8; -fx-border-radius: 8;");
        content.getChildren().addAll(headerText, originalReviewLabel, originalReviewText, starsText, replyLabel,
                replyText, buttonsBox);
    }

    private void editReply(Reply reply) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/reply_edit.fxml"));
            Parent root = loader.load();

            ReplyEditController controller = loader.getController();
            controller.setReply(reply);

            Stage stage = new Stage();
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.setTitle("Edit Reply");
            stage.setScene(new Scene(root));
            stage.initOwner(getListView().getScene().getWindow());
            stage.showAndWait();

            RepliesController repliesController = (RepliesController) getListView().getUserData();
            if (repliesController != null) {
                repliesController.refreshReplies();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteReply(Reply reply) {
        UserSession.getInstance().deleteReply(reply);

        RepliesController repliesController = (RepliesController) getListView().getUserData();
        if (repliesController != null) {
            repliesController.refreshReplies();
        }
    }

    @Override
    protected void updateItem(Reply item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            setText(null);
        } else {
            // Find the original review
            Review originalReview = UserSession.getInstance().getReviews().stream()
                    .filter(review -> review.getUsername().equals(item.getReviewUsername()) &&
                            review.getRestaurantId() == item.getRestaurantId())
                    .findFirst()
                    .orElse(null);

            // Set header text
            if (originalReview != null) {
                headerText.setText(
                        "Reply to " + originalReview.getUsername() + " - " + originalReview.getRestaurantName());
            } else {
                headerText.setText(
                        "Reply to " + item.getReviewUsername() + " - Restaurant ID: " + item.getRestaurantId());
            }

            // Set original review content
            if (originalReview != null) {
                originalReviewLabel.setVisible(true);
                originalReviewText.setVisible(true);
                starsText.setVisible(true);

                originalReviewText.setText(originalReview.getText());
                starsText.setText("Rating: " + "★".repeat(originalReview.getRating()));
            } else {
                originalReviewLabel.setVisible(false);
                originalReviewText.setVisible(false);
                starsText.setVisible(false);
            }

            // Set reply content
            replyText.setText(item.getText());

            setGraphic(content);
        }
    }
}
