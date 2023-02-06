package emotionalsongs.client.gui;

import emotionalsongs.client.ClientApp;
import emotionalsongs.client.ClientContext;
import emotionalsongs.client.gui.components.SongsTableController;
import emotionalsongs.common.*;
import emotionalsongs.common.interfaces.PlaylistDAO;
import emotionalsongs.common.interfaces.SongDAO;
import emotionalsongs.exceptions.RMIStubException;
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

/**
 * Controller per FXML della vista di creazione di una nuova playlist.
 * Mostra una lista delle canzoni inserite, un prompt per il nome e
 * diversi metodi di ricerca per l'aggiunta di canzoni alla playlist.
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 */

public class NewPlaylistController {

    @FXML
    private ScrollPane leftScrollPane;
    @FXML
    private Button createPlaylistBtn;
    @FXML
    private Button addAuthorSongsBtn;
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

    /**
     * Metodo di inizializzazione chiamato alla creazione della vista.
     * Effettua il binding dei vari componenti UI ai rispettivi oggetti, rendendo la vista reattiva
     * ai cambiamenti.
     * Aggiunge listener per la formattazione e validazione degli input
     */
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

    /**
     * Crea una nuova playlist nominata secondo il prompt e aggiunge i riferimenti alle canzoni selezionate.
     * Aggiorna il context per mantenere la reattività
     */
    @FXML
    public void createNewPlaylist() {
        User user = context.getUser();
        List<Integer> songIds = new ArrayList<>();

        for (Song song : newPlaylistSongs) {
            songIds.add(song.id);
        }

        try {
            Playlist newPlaylist = playlistDAO.createNewPlaylist(user.getId(), newPlaylistName.getText(), songIds);

            if (newPlaylist != null) {
                context.addUserPlaylist(newPlaylist);
                newPlaylistName.clear();
                newPlaylistSongs.clear();
            } else {
                String msg = "La playlist '" + newPlaylistName.getText() + "' esiste già!";
                NodeHelpers.createAlert(null, Alert.AlertType.WARNING, "Attenzione!", null, msg, false);
            }
        } catch (RemoteException e) {
            throw new RMIStubException(e);
        }


    }

    /**
     * Inizializza la tabella dei risultati di ricerca album aggiungendo
     * la colonna con il bottone di aggiunta album alla playlist
     */
    private void initAlbumsTable() {

        TableColumn<Song, Void> addAlbumCol = new TableColumn<>("Aggiungi");
        Callback<TableColumn<Song, Void>, TableCell<Song, Void>> cellFactory = param -> new TableCell<>() {
            final HBox btnBox = new HBox();

            final Button viewBtn = new Button("Aggiungi album");

            {
                viewBtn.setOnAction(event1 -> {
                    Song song = albumsTable.getItems().get(getIndex());
                    addAlbumSongs(song.getAuthor(), song.getAlbum());
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

    /**
     * Ricerca tutti gli autori contenenti nel nome la stringa inserita
     * e li aggiunge alla lista di autori
     */
    @FXML
    private void searchAuthors() {
        String author = authorPrompt.getText().trim();
        try {
            List<String> results = songDAO.getAuthors(author);
            searchedAuthors.setAll(results);
            if (!results.isEmpty()) authorPrompt.clear();
        } catch (RemoteException e) {
            throw new RMIStubException(e);
        }

    }

    /**
     * Ricerca tutti gli album contenenti nel titolo la stringa inserita
     * e li aggiunge alla lista di album
     */
    @FXML
    private void searchAlbums() {
        String album = albumPrompt.getText().trim();
        try {
            List<Song> results = songDAO.getAlbums(album);
            searchedAlbums.addAll(results);
            if (!results.isEmpty()) albumPrompt.clear();
        } catch (RemoteException e) {
            throw new RMIStubException(e);
        }
    }

    /**
     * Aggiunge alla nuova playlist tutte le canzoni di un autore
     */
    @FXML
    private void addAuthorSongs() {
        String author = authorsList.getSelectionModel().getSelectedItem();
        try {
            List<Song> results = songDAO.getSongsByAuthorYear(author, null);

            if (!results.isEmpty()) {
                context.addNewPlaylistSongs(results);
                authorPrompt.clear();
            }
        } catch (RemoteException e) {
            throw new RMIStubException(e);
        }

    }

    /**
     * Aggiunge alla nuova playlist le tutte le canzoni di un determinato album di un autore
     *
     * @param author l'autore dell'album
     * @param album  il titolo dell'album
     */
    @FXML
    private void addAlbumSongs(String author, String album) {
        try {
            List<Song> results = songDAO.getSongsByAuthorAlbum(author, album);

            if (!results.isEmpty()) {
                context.addNewPlaylistSongs(results);
                albumPrompt.clear();
            }
        } catch (RemoteException e) {
            throw new RMIStubException(e);
        }
    }


}