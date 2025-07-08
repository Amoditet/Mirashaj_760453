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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import theknife.UserSession;
import theknife.RestaurantDetailController;

public class RestaurantListCell extends ListCell<Restaurant> {
    private HBox content;
    private Text nameText;
    private Button editButton;
    private Button deleteButton;

    private Restaurant lastClickedRestaurant = null;
    private long lastClickTime = 0;

    public RestaurantListCell() {
        super();
        nameText = new Text();
        nameText.getStyleClass().add("list-cell");
        nameText.setStyle("-fx-padding: 0 0 0 0; -fx-alignment: center-left;");

        editButton = new Button("Edit");
        deleteButton = new Button("Delete");

        editButton.getStyleClass().add("small-filter-button");
        deleteButton.getStyleClass().add("red-filter-button");

        HBox buttonsBox = new HBox(5, editButton, deleteButton);
        content = new HBox(10, nameText, buttonsBox);
        HBox.setHgrow(nameText, Priority.ALWAYS);

        editButton.setOnAction(event -> {
            Restaurant r = getItem();
            if (r != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/restaurants_parameters.fxml"));
                    Parent root = loader.load();

                    RestaurantsParametersController controller = loader.getController();
                    controller.prefillFields(r);
                    controller.setEditingRestaurant(r);

                    Stage stage = new Stage();
                    stage.setTitle("Edit Restaurant");
                    stage.setScene(new Scene(root));
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initOwner(getListView().getScene().getWindow());
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.showAndWait();

                    if (getListView().getScene() != null && getListView().getScene().getWindow() instanceof Stage) {
                        Stage parentStage = (Stage) getListView().getScene().getWindow();
                        Object userData = parentStage.getUserData();
                        if (userData instanceof RestaurantsController) {
                            ((RestaurantsController) userData).loadRestaurants();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        deleteButton.setOnAction(event -> {
            event.consume();
            Restaurant r = getItem();
            if (r != null) {
                removeRestaurantFromCSV(r);
                UserSession.getInstance().getOwnedRestaurants().remove(r);
                javafx.application.Platform.runLater(() -> {
                    getListView().getItems().remove(r);
                    getListView().refresh();
                });
            }
        });

        this.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> {
            if (evt.getTarget() instanceof Button)
                return;
            Restaurant selected = getItem();
            if (selected != null && !isEmpty()) {
                long currentTime = System.currentTimeMillis();
                if (selected.equals(lastClickedRestaurant) && (currentTime - lastClickTime) < 500) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/restaurant_detail.fxml"));
                        Parent root = loader.load();

                        RestaurantDetailController controller = loader.getController();
                        controller.setRestaurant(selected);

                        Scene scene = getListView().getScene();
                        scene.setRoot(root);

                        Stage stage = (Stage) scene.getWindow();
                        stage.setTitle(selected.getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                lastClickedRestaurant = selected;
                lastClickTime = currentTime;
            }
        });
    }

    private void removeRestaurantFromCSV(Restaurant r) {
        String workingDir = System.getProperty("user.dir");
        java.io.File restaurantsFile = new java.io.File(workingDir, "data/restaurants_list.csv");

        if (!restaurantsFile.exists()) {
            return; // Se il file non esiste, non c'è niente da rimuovere
        }

        java.util.List<String> lines = new java.util.ArrayList<>();

        // Leggi tutte le righe tranne quella del ristorante da eliminare
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(restaurantsFile))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    lines.add(line); // Mantieni l'header
                    first = false;
                    continue;
                }
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (parts.length < 1) {
                    lines.add(line);
                    continue;
                }

                try {
                    int lineId = Integer.parseInt(parts[0].trim());
                    if (lineId == r.getId()) {
                        continue; // Salta questa riga (elimina il ristorante)
                    }
                } catch (NumberFormatException e) {
                    // Se non è un numero valido, mantieni la riga
                }
                lines.add(line);
            }
        } catch (Exception e) {
            return;
        }

        // Riscrivi il file senza il ristorante eliminato
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(restaurantsFile))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void updateItem(Restaurant item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            setText(null);
        } else {
            nameText.setText(item.getName().replaceAll("^\"|\"$", ""));

            editButton.setVisible(true);
            editButton.setManaged(true);
            deleteButton.setVisible(true);
            deleteButton.setManaged(true);

            setGraphic(content);
        }
    }
}
