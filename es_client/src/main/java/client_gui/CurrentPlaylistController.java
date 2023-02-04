package client_gui;

import client.ClientApp;
import client.ClientContext;
import client_gui.components.SongsTableController;
import common.NodeHelpers;
import common.Playlist;
import common.Song;
import common.StringHelpers;
import common.interfaces.PlaylistDAO;
import exceptions.DAOException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.rmi.RemoteException;

/**
 * Controller per FXML della vista di dettaglio di una playlist
 * Permette di aggiungere tag emozionali per ogni canzone,
 * visualizzare il dettaglio dei tag o eliminare la playlist
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 */
public class CurrentPlaylistController {

    @FXML
    public Label playlistName;
    @FXML
    public Label playlistSongs;
    @FXML
    public Label playlistDuration;
    @FXML
    public Button deletePlaylistBtn;
    @FXML
    private TableView<Song> playlistSongsTable;
    @FXML
    private SongsTableController playlistSongsTableController;
    private final PlaylistDAO playlistDAO = ClientApp.getPlaylistDAO();
    private final ClientContext context = ClientContext.getInstance();
    private Playlist currentPlaylist = context.getCurrentPlaylist();
    private final ObservableList<Song> currentPlaylistSongs = FXCollections.observableArrayList();

    /**
     * Metodo di inizializzazione chiamato alla creazione della vista.
     * Contiene il listener per in ascolto di cambiamenti alla playlist,
     * i bindings ai componenti UI e l'inizializzazione delle colonne opzionali
     * della tabella per la specifica vista
     */
    public void initialize() {

        Property<ObservableList<Song>> playlistSongsProperty = new SimpleObjectProperty<>(currentPlaylistSongs);

        playlistSongs.textProperty().bind(Bindings.size(currentPlaylistSongs).asString());
        playlistSongsTable.itemsProperty().bind(playlistSongsProperty);

        context.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals("playlist")) {

                currentPlaylist = (Playlist) e.getNewValue();

                if (currentPlaylist != null) {
                    initCurrentPlaylist();
                } else {
                    playlistName.setText("");
                    playlistDuration.setText("0");
                }
            }
        });

        playlistSongsTableController.addEmotionAddBtn();
        playlistSongsTableController.addRemoveSongBtn(currentPlaylistSongs, true);
    }

    /**
     * Recupera le canzoni della playlist corrente e ne calcola la durata totale
     */
    private void initCurrentPlaylist() {
        try {
            currentPlaylistSongs.setAll(playlistDAO.getPlaylistSongs(currentPlaylist.getId()));
            playlistName.setText(currentPlaylist.getName());
            playlistDuration.setText(StringHelpers.getSongsListDurationString(currentPlaylistSongs));

        } catch (RemoteException e) {
            throw new DAOException(e);
        }
    }

    /**
     * Elimina la playlist corrente
     */
    @FXML
    private void deletePlaylist() {
        String msg = "Sei sicuro di voler eliminare la playlist '" + currentPlaylist.getName() + "' ?";

        boolean res = NodeHelpers.createAlert(
                Alert.AlertType.CONFIRMATION, "Conferma eliminazione playlist", null, msg, true);

        if (res) {
            try {
                int deleted = playlistDAO.deletePlaylist(currentPlaylist.getId());

                if (deleted > 0) {
                    context.removeUserPlaylist(currentPlaylist);
                    context.setCurrentPlaylist(null);
                }

            } catch (RemoteException e) {
                throw new DAOException(e);
            }
        }
    }
}

