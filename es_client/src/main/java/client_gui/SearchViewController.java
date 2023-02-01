package client_gui;

import client.ClientContext;
import client_gui.components.SongsTableController;
import common.Song;
import common.User;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

import java.beans.PropertyChangeEvent;

public class SearchViewController {
    @FXML
    public GridPane searchSongsPane;
    @FXML
    private TableView<Song> searchSongsTable;
    @FXML
    private SongsTableController searchSongsTableController;

    public void initialize() {
        ClientContext context = ClientContext.getInstance();
        User user = context.getUser();
        context.addPropertyChangeListener(this::userChangeListener);

        ObservableList<Song> songs = context.getSearchedSongs();
        Property<ObservableList<Song>> searchedSongsProperty = new SimpleObjectProperty<>(songs);

        searchSongsTable.itemsProperty().bind(searchedSongsProperty);

        if (user != null) {
            searchSongsTableController.addPlaylistDropdown();
        }

    }

    private void userChangeListener(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("user")) {

            User newUser = (User) e.getNewValue();

            if (newUser != null) {
                searchSongsTableController.addPlaylistDropdown();
            } else {
                searchSongsTable.getColumns().remove(1);
            }
        }
    }


}
