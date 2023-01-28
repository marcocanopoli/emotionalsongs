package client_gui;

import client.ClientApp;
import client.ClientContext;
import common.Playlist;
import common.Song;
import common.User;
import common.interfaces.PlaylistDAO;
import common.interfaces.SongDAO;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewPlaylistController {

    @FXML
    private Button createPlaylistBtn;
    @FXML
    private Button addAuthorSongsBtn;
    @FXML
    private Button addAlbumSongsBtn;
    @FXML
    private Label playlistSongsCount;
    @FXML
    private Label playlistDuration;
    @FXML
    private TextField newPlaylistName;
    @FXML
    private TextField authorPrompt;
    @FXML
    private TextField albumPrompt;
    @FXML
    private ListView<String> authorsList;
    @FXML
    private ListView<String> albumsList;
    @FXML
    private TableView<Song> songsTable;
    @FXML
    private TableView<Song> newPlaylistSongsTable;
    private final ObservableList<String> searchedAuthors = FXCollections.observableArrayList();
    private final ObservableList<String> searchedAlbums = FXCollections.observableArrayList();
    private final ObservableList<Song> playlistSongs = FXCollections.observableArrayList();
    private ClientContext context;
    private SongDAO songDAO;
    private PlaylistDAO playlistDAO;


    public void initialize() {
        context = ClientContext.getInstance();
        songDAO = ClientApp.getSongDAO();
        playlistDAO = ClientApp.getPlaylistDAO();

        ObservableList<Song> songs = context.getSearchedSongs();

        Property<ObservableList<Song>> searchedSongsProperty = new SimpleObjectProperty<>(songs);
        Property<ObservableList<String>> searchedAuthorsProperty = new SimpleObjectProperty<>(searchedAuthors);
        Property<ObservableList<String>> searchedAlbumsProperty = new SimpleObjectProperty<>(searchedAlbums);
        Property<ObservableList<Song>> playlistSongsProperty = new SimpleObjectProperty<>(playlistSongs);

        songsTable.itemsProperty().bind(searchedSongsProperty);
        authorsList.itemsProperty().bind(searchedAuthorsProperty);
        albumsList.itemsProperty().bind(searchedAlbumsProperty);
        newPlaylistSongsTable.itemsProperty().bind(playlistSongsProperty);

        playlistSongsCount.textProperty().bind(Bindings.size(playlistSongs).asString());

        createPlaylistBtn.disableProperty().bind(
                Bindings.size(playlistSongs).greaterThan(0).not()
                        .or(Bindings.isEmpty(newPlaylistName.textProperty())));

        authorsList.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) ->
                addAuthorSongsBtn.setDisable(nv == null));

        albumsList.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) ->
                addAlbumSongsBtn.setDisable(nv == null));

        newPlaylistName.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= 256 ? change : null));

        addSongAddToPlaylistBtn();
        addRemoveSongBtn();
    }

    @FXML
    private void createNewPlaylist(ClientContext context) throws RemoteException {
        User user = context.getUser();

        Playlist newPlaylist = playlistDAO.createNewPlaylist(user.getId(), newPlaylistName.getText(), playlistSongs);

        if (newPlaylist != null) {
            context.addUserPlaylist(newPlaylist);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Errore");
            alert.setHeaderText(null);
            alert.setContentText("La playlist '" + newPlaylistName.getText() + "' esiste già!");

            alert.showAndWait();

        }

    }

    private void addSongAddToPlaylistBtn() {

        TableColumn<Song, Void> addSongColumn = new TableColumn<>("Aggiungi");
        Callback<TableColumn<Song, Void>, TableCell<Song, Void>> cellFactory = param -> new TableCell<>() {
            final HBox btnBox = new HBox();

            final Button addBtn = new Button("Aggiungi");

            {
                addBtn.setOnAction(event1 -> {
                    Song song = songsTable.getItems().get(getIndex());
                    playlistSongs.add(song);
                });

                btnBox.getChildren().add(addBtn);
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


        addSongColumn.setMinWidth(100);
        addSongColumn.setCellFactory(cellFactory);
        songsTable.getColumns().add(addSongColumn);
    }

    private void addRemoveSongBtn() {

        newPlaylistSongsTable.getColumns().remove(newPlaylistSongsTable.getColumns().size() - 1);

        TableColumn<Song, Void> removeSongColumn = new TableColumn<>("Rimuovi");
        Callback<TableColumn<Song, Void>, TableCell<Song, Void>> cellFactory = param -> new TableCell<>() {
            final HBox btnBox = new HBox();

            final Button addBtn = new Button("Rimuovi");

            {
                addBtn.setOnAction(event1 -> {
                    Song song = newPlaylistSongsTable.getItems().get(getIndex());
                    playlistSongs.remove(song);
                });

                btnBox.getChildren().add(addBtn);
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


        removeSongColumn.setMinWidth(100);
        removeSongColumn.setCellFactory(cellFactory);
        newPlaylistSongsTable.getColumns().add(removeSongColumn);
    }

    @FXML
    private void searchAuthors() throws RemoteException {
        String author = authorPrompt.getText().trim();

        List<String> results = songDAO.getAuthors(author);
        searchedAuthors.setAll(results);

        if (!results.isEmpty()) authorPrompt.clear();
    }

    @FXML
    private void searchAlbums() throws RemoteException {
        String album = albumPrompt.getText().trim();

        HashMap<String, String> results = songDAO.getAlbums(album);
        List<Map.Entry<String, String>> list = new ArrayList<>(results.entrySet());
        searchedAlbums.setAll(String.valueOf(list));

        if (!results.isEmpty()) albumPrompt.clear();
    }

    @FXML
    private void addAuthorSongs() throws RemoteException {
        String author = authorsList.getSelectionModel().getSelectedItem();
        List<Song> results = songDAO.searchByAuthorYear(author, null);

        if (!results.isEmpty()) {
            playlistSongs.addAll(results);
            authorPrompt.clear();
        }

    }

    @FXML
    private void addAlbumSongs() throws RemoteException {
        String album = albumsList.getSelectionModel().getSelectedItem();
        List<Song> results = songDAO.searchByAlbum(album);

        if (!results.isEmpty()) {
            playlistSongs.addAll(results);
            albumPrompt.clear();
        }
    }

}