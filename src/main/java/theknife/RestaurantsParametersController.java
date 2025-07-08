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
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.File;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import javafx.event.ActionEvent;

public class RestaurantsParametersController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField cuisineField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField descriptionField;

    @FXML
    private Button addButton;
    @FXML
    private Button cancelButton;

    private Restaurant editingRestaurant;

    @FXML
    private void initialize() {
        addButton.setOnAction(event -> handleAdd());
        cancelButton.setOnAction(event -> handleCancel());
    }

    public void prefillFields(Restaurant r) {
        if (r == null)
            return;
        this.editingRestaurant = r;
        nameField.setText(clean(r.getName()));
        addressField.setText(clean(r.getAddress()));
        locationField.setText(clean(r.getLocation()));
        priceField.setText(clean(r.getPrice()));
        cuisineField.setText(clean(r.getCuisine()));
        phoneNumberField.setText(clean(r.getPhoneNumber()));
        descriptionField.setText(clean(r.getDescription()));
    }

    public void setEditingRestaurant(Restaurant r) {
        this.editingRestaurant = r;
    }

    private String clean(String s) {
        if (s == null)
            return "";
        return s.replaceAll("^\\\"|\\\"$", "");
    }

    private int getNextRestaurantId() {
        int maxId = 0;
        String workingDir = System.getProperty("user.dir");
        File restaurantsFile = new File(workingDir, "data/restaurants_list.csv");

        if (!restaurantsFile.exists()) {
            return 1;
        }

        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(restaurantsFile))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (parts.length > 0) {
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        if (id > maxId)
                            maxId = id;
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (IOException ignored) {
        }
        return maxId + 1;
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String location = locationField.getText().trim();
        String price = priceField.getText().trim();
        String cuisine = cuisineField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();
        String description = descriptionField.getText().trim();

        if (name.isEmpty() || address.isEmpty() || location.isEmpty() || price.isEmpty() ||
                cuisine.isEmpty() || phoneNumber.isEmpty() || description.isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all required fields.");
            alert.showAndWait();
            return;
        }

        String username = UserSession.getInstance().getUsername();
        int restaurantId;

        if (editingRestaurant != null) {
            restaurantId = editingRestaurant.getId();
        } else {
            restaurantId = getNextRestaurantId();
        }

        String csvLine = String.format(
                "%d,%s,%s,%s,%s,%s,%s,,,%s,,,,,,%s",
                restaurantId, username, name, address, location, price, cuisine, phoneNumber, description);

        try {
            String workingDir = System.getProperty("user.dir");
            File externalFile = new File(workingDir + "/data/restaurants_list.csv");

            File dataDir = new File(workingDir + "/data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            if (!externalFile.exists()) {
                try (java.io.FileWriter fw = new java.io.FileWriter(externalFile)) {
                    fw.write(
                            "restaurantId,Username,Name,Address,Location,Price,Cuisine,Longitude,Latitude,PhoneNumber,Url,WebsiteUrl,Award,GreenStar,FacilitiesAndServices,Description\n");
                }
            }

            if (editingRestaurant != null) {
                java.util.List<String> lines = new java.util.ArrayList<>();
                try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(externalFile))) {
                    String line;
                    boolean first = true;
                    while ((line = br.readLine()) != null) {
                        if (first) {
                            lines.add(line);
                            first = false;
                            continue;
                        }
                        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                        if (parts.length < 1) {
                            lines.add(line);
                            continue;
                        }
                        try {
                            int existingId = Integer.parseInt(parts[0].trim());
                            if (existingId == editingRestaurant.getId()) {
                                continue;
                            }
                        } catch (NumberFormatException e) {
                            lines.add(line);
                            continue;
                        }
                        lines.add(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                lines.add(csvLine);

                try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(externalFile))) {
                    for (String l : lines) {
                        bw.write(l);
                        bw.newLine();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                UserSession.getInstance().getOwnedRestaurants().remove(editingRestaurant);
            } else {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(externalFile, true))) {
                    writer.write(csvLine);
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }

            Restaurant r = new Restaurant(restaurantId, name, address, 0.0, 0, location, cuisine, price, "",
                    phoneNumber, "", "", "", description, UserSession.getInstance().getUsername());
            UserSession.getInstance().addOwnedRestaurant(r);

            UserSession.getInstance().addRestaurant(r);

            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to save restaurant data: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
