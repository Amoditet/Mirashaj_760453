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
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class FavoritesListCell extends ListCell<Restaurant> {
    private HBox hbox;
    private Text text;
    private Button removeButton;

    public FavoritesListCell() {
        super();
        text = new Text();
        removeButton = new Button("Remove");
        removeButton.getStyleClass().add("red-filter-button");

        hbox = new HBox(10, text, removeButton);
        hbox.setStyle("-fx-padding: 10 10 10 10;");

        removeButton.setOnAction(event -> {
            Restaurant item = getItem();
            if (item != null) {
                UserSession.getInstance().removeFavorite(item);
                getListView().getItems().remove(item);
            }
        });
    }

    @Override
    protected void updateItem(Restaurant item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            text.setText(item.getName().replaceAll("^\"|\"$", ""));
            if (UserSession.getInstance().getFavorites().contains(item)) {
                getStyleClass().add("list-cell");
                setGraphic(hbox);
            }
        }
    }
}
