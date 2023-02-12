package emotionalsongs.client.gui;

import emotionalsongs.client.ClientApp;
import emotionalsongs.client.ClientContext;
import emotionalsongs.client.gui.components.SongsTableController;
import emotionalsongs.common.NodeHelpers;
import emotionalsongs.common.Song;
import emotionalsongs.common.User;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

import java.beans.PropertyChangeEvent;

/**
 * Controller per FXML dela vista di ricerca canzoni.
 * Permette la ricerca di canzoni per titolo, autore o autore/anno
 * e azioni di visualizzazione e aggiunta ad una playlist per ogni canzone
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 */
public class SearchViewController {
    @FXML
    public GridPane searchSongsPane;
    @FXML
    private Button newSongBtn;
    @FXML
    private TableView<Song> searchSongsTable;
    @FXML
    private SongsTableController searchSongsTableController;

    /**
     * Metodo di inizializzazione chiamato alla creazione della vista.
     * Setta la tabella in cui inserire le canzoni ricercate e effettua il binding
     * della lista di canzoni salvate nel <code>context</code> per la persistenza
     * all'interno dell'applicazione.
     * Implementa listener del cambio utente per mostrare o nascondere la funzionalità di aggiunta ad una playlist
     *
     * @see ClientContext
     */
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

        context.addPropertyChangeListener(e -> {
            if (e.getNewValue() != null &&
                    (e.getPropertyName().equals("newPlaylist") ||
                            e.getPropertyName().equals("deletePlaylist"))) {
                searchSongsTable.getColumns().remove(1);
                searchSongsTableController.addPlaylistDropdown();
            }
        });


    }

    /**
     * Listener del cambio utente, aggiunge o rimuove la funzionalità di aggiunta
     * della singola canzone ad una playlist
     *
     * @param e l'evento triggerato dal <code>context</code>
     * @see java.beans.PropertyChangeListener
     * @see ClientContext
     */
    private void userChangeListener(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("user")) {

            User newUser = (User) e.getNewValue();

            if (newUser != null) {
                searchSongsTableController.addPlaylistDropdown();
                newSongBtn.setDisable(false);
            } else {
                searchSongsTable.getColumns().remove(1);
                newSongBtn.setDisable(true);
            }
        }
    }

    /**
     * Apre la vista di creazione di una nuova canzone
     */
    public void newSong() {

        NodeHelpers.createStage(
                ClientApp.getWindow(), ClientApp.newSongURL, "Aggiunta canzone", true);
    }
}
