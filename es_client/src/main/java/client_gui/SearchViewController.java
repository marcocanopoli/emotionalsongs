package client_gui;

import client.ClientApp;
import client.ClientContext;
import common.Playlist;
import common.Song;
import common.User;
import common.interfaces.PlaylistDAO;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.util.List;

public class SearchViewController {
    @FXML
    public GridPane searchSongsPane;
    @FXML
    private TableView<Song> searchSongsTable;
    private ClientContext context;
    private PlaylistDAO playlistDAO;

    public void initialize() {
        playlistDAO = ClientApp.getPlaylistDAO();
        context = ClientContext.getInstance();

        User user = context.getUser();
        context.addPropertyChangeListener(this::userChangeListener);

        ObservableList<Song> songs = context.getSearchedSongs();
        Property<ObservableList<Song>> searchedSongsProperty = new SimpleObjectProperty<>(songs);

        searchSongsTable.itemsProperty().bind(searchedSongsProperty);

        if (user != null) {
            addPlaylistDropdown();
        }

    }

    private void userChangeListener(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("user")) {

            User newUser = (User) e.getNewValue();

            if (newUser != null) {
                addPlaylistDropdown();
            } else {
                int tableSize = searchSongsTable.getColumns().size();
                searchSongsTable.getColumns().remove(tableSize - 1);
            }
        }
    }

    private void addPlaylistDropdown() {

        ObservableList<Playlist> userPlaylists = context.getUserPlaylists();

        Callback<TableColumn<Song, Void>, TableCell<Song, Void>> cellFactory = param ->
                new TableCell<>() {
                    final HBox btnBox = new HBox();
                    private final MenuButton playlistChoice = new MenuButton("Aggiungi a:");


                    {
                        for (Playlist p : userPlaylists) {
                            MenuItem item = new MenuItem(p.getName());
                            item.setOnAction(event -> {
                                Song song = getTableView().getItems().get(getIndex());

                                try {
                                    int[] rows = playlistDAO.addSongsToPlaylist(p.getId(), List.of(song.id));
                                    if (rows.length > 0) {
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("Conferma");
                                        alert.setHeaderText(null);
                                        alert.setContentText("'" + song.getTitle() + "' è stata aggiunta alla playlist '" + p.getName() + "'!");

                                        alert.showAndWait();
                                    } else {
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("Info");
                                        alert.setHeaderText(null);
                                        alert.setContentText("'" + song.getTitle() + "' è già presente in '" + p.getName() + "'!");

                                        alert.showAndWait();
                                    }
                                } catch (RemoteException e) {
                                    throw new RuntimeException(e);
                                }

                            });
                            playlistChoice.getItems().add(item);
                        }

                        btnBox.getChildren().add(playlistChoice);
                        btnBox.setAlignment(Pos.CENTER);
                    }


                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnBox);
                        }
                    }
                };

        TableColumn<Song, Void> playlistColumn = new TableColumn<>("Playlist");
        playlistColumn.setMinWidth(130);
        playlistColumn.setCellFactory(cellFactory);

        searchSongsTable.getColumns().add(playlistColumn);
    }


}
