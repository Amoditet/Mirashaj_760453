/**
 * 
 * Erik Mirashaj Matricola: 760453 Sede: VA Email: emirashaj@studenti.uninsubria.it
 * Igor Gorchynskyi Matricola: 757184 Sede: VA
 * Lorenzo Mujeci Matricola: 757597 Sede: VA
 * Matteo Nika Matricola: 762540 Sede: VA
 * 
 */

package theknife;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookingsAddController {

    private String restaurantName;
    private Bookings editingBooking;
    private Runnable onBookingChanged; // Callback for when booking is successfully added/edited

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public void setEditingBooking(Bookings booking) {
        this.editingBooking = booking;
        if (booking != null) {
            datePicker.setValue(java.time.LocalDate.parse(booking.getDate()));
            timeComboBox.setValue(booking.getTime());
        }
    }

    public void setOnBookingChanged(Runnable callback) {
        this.onBookingChanged = callback;
    }

    public Bookings getUpdatedBooking() {
        if (editingBooking != null) {
            String dateStr = datePicker.getValue().toString();
            String timeStr = timeComboBox.getValue();
            return new Bookings(editingBooking.getRestaurantName(), dateStr, timeStr);
        }
        return null;
    }

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> timeComboBox;

    @FXML
    private Button closeButton;

    @FXML
    private Button submitButton;

    @FXML
    public void initialize() {
        datePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        datePicker.getEditor().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                LocalDate date = datePicker.getValue();
                if (date != null) {
                    LocalDate now = LocalDate.now();
                    if (date.getYear() != now.getYear()) {
                        datePicker.setValue(LocalDate.of(now.getYear(), date.getMonth(), date.getDayOfMonth()));
                    }
                }
            }
        });

        List<String> times = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime time = LocalTime.of(12, 0);
        LocalTime endTime = LocalTime.of(15, 30);
        int count = 0;
        while (!time.isAfter(endTime) && count < 11) {
            times.add(time.format(formatter));
            time = time.plusMinutes(15);
            count++;
        }

        time = LocalTime.of(18, 0);
        endTime = LocalTime.of(23, 45);
        count = 0;
        while (!time.isAfter(endTime) && count < 24) {
            times.add(time.format(formatter));
            time = time.plusMinutes(15);
            count++;
        }
        times.add("00:00");

        timeComboBox.setItems(FXCollections.observableArrayList(times));

        timeComboBox.requestFocus();
    }

    @FXML
    private void handleSubmit() {
        LocalDate selectedDate = datePicker.getValue();
        String selectedTime = timeComboBox.getValue();

        if (selectedDate == null || selectedTime == null) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a booking date and time.");
            alert.showAndWait();
            return;
        }

        String dateStr = selectedDate.toString(); // ISO format yyyy-MM-dd
        Bookings booking = new Bookings(restaurantName, dateStr, selectedTime);

        if (editingBooking != null) {
            theknife.UserSession.getInstance().updateBooking(editingBooking, booking);
        } else {
            theknife.UserSession.getInstance().addBooking(booking);
        }

        // Notify parent that a booking has been added/edited
        if (onBookingChanged != null) {
            onBookingChanged.run();
        }

        handleClose();
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
