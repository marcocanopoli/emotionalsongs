package client_gui;

import client.ClientApp;
import client.ClientContext;
import client_gui.components.SongsTableController;
import common.*;
import common.interfaces.PlaylistDAO;
import common.interfaces.SongDAO;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class NewPlaylistController {

    @FXML
    private ScrollPane leftScrollPane;
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
    private TableView<Song> albumsTable;
    @FXML
    private TableView<Song> byTitleSongsTable;
    @FXML
    private TableView<Song> newPlaylistSongsTable;
    private final ClientContext context = ClientContext.getInstance();
    private final ObservableList<String> searchedAuthors = FXCollections.observableArrayList();
    private final ObservableList<Song> searchedAlbums = FXCollections.observableArrayList();
    private final ObservableList<Song> newPlaylistSongs = context.getNewPlaylistSongs();
    private final ObservableList<Song> searchedSongs = context.getSearchedSongs();
    private final SongDAO songDAO = ClientApp.getSongDAO();
    private final PlaylistDAO playlistDAO = ClientApp.getPlaylistDAO();
    @FXML
    private SongsTableController byTitleSongsTableController;
    @FXML
    private SongsTableController newPlaylistSongsTableController;


    public void initialize() {

        Property<ObservableList<Song>> searchedSongsProperty = new SimpleObjectProperty<>(searchedSongs);
        Property<ObservableList<Song>> searchedAlbumsProperty = new SimpleObjectProperty<>(searchedAlbums);
        Property<ObservableList<String>> searchedAuthorsProperty = new SimpleObjectProperty<>(searchedAuthors);
        Property<ObservableList<Song>> playlistSongsProperty = new SimpleObjectProperty<>(newPlaylistSongs);

        byTitleSongsTable.itemsProperty().bind(searchedSongsProperty);
        authorsList.itemsProperty().bind(searchedAuthorsProperty);
        albumsTable.itemsProperty().bind(searchedAlbumsProperty);
        newPlaylistSongsTable.itemsProperty().bind(playlistSongsProperty);

        playlistSongsCount.textProperty().bind(Bindings.size(newPlaylistSongs).asString());

        newPlaylistSongsTable.visibleProperty().bind(Bindings.size(newPlaylistSongs).isNotEqualTo(0));
        leftScrollPane.fitToWidthProperty().bind(Bindings.size(newPlaylistSongs).isEqualTo(0));

        createPlaylistBtn.disableProperty().bind(
                Bindings.size(newPlaylistSongs).greaterThan(0).not()
                        .or(Bindings.isEmpty(newPlaylistName.textProperty())));

        authorsList.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) ->
                addAuthorSongsBtn.setDisable(nv == null));

        newPlaylistName.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= 256 ? change : null));


        newPlaylistSongs.addListener((ListChangeListener<Song>) change -> {
            String durationString = "0";

            while (change.next()) {
                ObservableList<Song> songsList = FXCollections.observableArrayList(change.getList());
                durationString = StringHelpers.getSongsListDurationString(songsList);
            }
            playlistDuration.setText(durationString);
        });

        byTitleSongsTableController.addSongAddToPlaylistBtn();
        newPlaylistSongsTableController.addRemoveSongBtn(newPlaylistSongs, false);

        initAlbumsTable();
    }

    @FXML
    public void createNewPlaylist() throws RemoteException {
        User user = context.getUser();
        List<Integer> songIds = new ArrayList<>();

        for (Song song : newPlaylistSongs) {
            songIds.add(song.id);
        }

        Playlist newPlaylist = playlistDAO.createNewPlaylist(user.getId(), newPlaylistName.getText(), songIds);

        if (newPlaylist != null) {
            context.addUserPlaylist(newPlaylist);
            newPlaylistName.clear();
            newPlaylistSongs.clear();
        } else {
            String msg = "La playlist '" + newPlaylistName.getText() + "' esiste già!";
            NodeHelpers.createAlert(Alert.AlertType.WARNING, "Attenzione!", null, msg, false);
        }

    }

    private void initAlbumsTable() {

        TableColumn<Song, Void> addAlbumCol = new TableColumn<>("Aggiungi");
        Callback<TableColumn<Song, Void>, TableCell<Song, Void>> cellFactory = param -> new TableCell<>() {
            final HBox btnBox = new HBox();

            final Button viewBtn = new Button("Aggiungi album");

            {
                viewBtn.setOnAction(event1 -> {
                    Song song = albumsTable.getItems().get(getIndex());
                    try {
                        addAlbumSongs(song.getAuthor(), song.getAlbum());
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });

                btnBox.getChildren().add(viewBtn);
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


        addAlbumCol.setMinWidth(150);
        addAlbumCol.setEditable(false);
        addAlbumCol.setCellFactory(cellFactory);
        albumsTable.getColumns().add(0, addAlbumCol);
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
        List<Song> results = songDAO.getAlbums(album);
        searchedAlbums.addAll(results);
        if (!results.isEmpty()) albumPrompt.clear();
    }

    @FXML
    private void addAuthorSongs() throws RemoteException {
        String author = authorsList.getSelectionModel().getSelectedItem();
        List<Song> results = songDAO.getSongsByAuthorYear(author, null);

        if (!results.isEmpty()) {
            context.addNewPlaylistSongs(results);
            authorPrompt.clear();
        }

    }

    @FXML
    private void addAlbumSongs(String author, String album) throws RemoteException {
        List<Song> results = songDAO.getSongsByAuthorAlbum(author, album);

        if (!results.isEmpty()) {
            context.addNewPlaylistSongs(results);
            albumPrompt.clear();
        }
    }


}