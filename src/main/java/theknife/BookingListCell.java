/**
 * 
 * Erik Mirashaj Matricola: 760453 Sede: VA Email: emirashaj@studenti.uninsubria.it
 * Igor Gorchynskyi Matricola: 757184 Sede: VA
 * Lorenzo Mujeci Matricola: 757597 Sede: VA
 * Matteo Nika Matricola: 762540 Sede: VA
 * 
 */

package theknife;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class BookingListCell extends ListCell<Bookings> {

    private HBox content;
    private Text bookingText;
    private Button editButton;
    private Button deleteButton;

    public BookingListCell() {
        super();
        bookingText = new Text();
        bookingText.getStyleClass().add("list-cell");
        bookingText.setStyle("-fx-padding: 5 10 5 10; -fx-alignment: center-left;");
        editButton = new Button("Edit");
        deleteButton = new Button("Delete");

        editButton.getStyleClass().add("small-filter-button");
        deleteButton.getStyleClass().add("red-filter-button");

        HBox buttonsBox = new HBox(5, editButton, deleteButton);
        content = new HBox(10, bookingText, buttonsBox);
        content.setPadding(new Insets(5));
        HBox.setHgrow(bookingText, Priority.ALWAYS);

        editButton.setOnAction(event -> {
            Bookings booking = getItem();
            if (booking != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/bookings_add.fxml"));
                    Parent root = loader.load();

                    BookingsAddController controller = loader.getController();
                    controller.setRestaurantName(booking.getRestaurantName());
                    controller.setEditingBooking(booking);

                    // Set callback to refresh the list when booking is updated
                    controller.setOnBookingChanged(() -> {
                        // Find the parent BookingsController and refresh the list
                        javafx.scene.Node node = getListView();
                        while (node != null && !(node instanceof javafx.scene.Parent)) {
                            node = node.getParent();
                        }
                        if (node != null) {
                            javafx.scene.Parent parent = (javafx.scene.Parent) node;
                            // Try to find BookingsController in the scene
                            try {
                                javafx.stage.Stage stage = (javafx.stage.Stage) parent.getScene().getWindow();
                                Object userData = stage.getUserData();
                                if (userData instanceof BookingsController) {
                                    ((BookingsController) userData).refreshBookings();
                                } else {
                                    // Alternative: refresh the ListView directly
                                    getListView().getItems().clear();
                                    getListView().getItems().addAll(UserSession.getInstance().getBookings());
                                }
                            } catch (Exception e) {
                                // Fallback: refresh the ListView directly
                                getListView().getItems().clear();
                                getListView().getItems().addAll(UserSession.getInstance().getBookings());
                            }
                        }
                    });

                    Stage stage = new Stage();
                    stage.setTitle("Edit Booking");
                    stage.setScene(new Scene(root));
                    stage.initOwner(getListView().getScene().getWindow());
                    stage.showAndWait();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        deleteButton.setOnAction(event -> {
            Bookings booking = getItem();
            if (booking != null) {
                UserSession.getInstance().deleteBooking(booking);
                getListView().getItems().remove(booking);
                getListView().refresh();
            }
        });
    }

    @Override
    protected void updateItem(Bookings booking, boolean empty) {
        super.updateItem(booking, empty);
        if (empty || booking == null) {
            setText(null);
            setGraphic(null);
        } else {
            String restaurantName = booking.getRestaurantName();
            if (restaurantName != null) {
                restaurantName = restaurantName.replace("\"", "");
            }
            String dateStr = booking.getDate();
            String formattedDate = dateStr;
            try {
                java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
                formattedDate = date.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            } catch (Exception e) {
            }
            bookingText.setText(restaurantName + " - " + formattedDate + " " + booking.getTime());
            setGraphic(content);
        }
    }
}
