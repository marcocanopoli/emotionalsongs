package emotionalsongs.client.gui.components;

import emotionalsongs.client.ClientApp;
import emotionalsongs.client.ClientContext;
import emotionalsongs.common.NodeHelpers;
import emotionalsongs.common.Playlist;
import emotionalsongs.common.Song;
import emotionalsongs.common.interfaces.PlaylistDAO;
import emotionalsongs.exceptions.RMIStubException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Controller per FXML della tabella che mostra le canzoni ricercate.
 * In qunato componente riutilizzabile, contiene metodi per aggiungere
 * colonne addizionali alla tabella a seconda del caso d'uso
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 */
public class SongsTableController {
    @FXML
    private TableView<Song> songsTable;
    private final ClientContext context = ClientContext.getInstance();
    private final ObservableList<Song> newPlaylistSongs = context.getNewPlaylistSongs();
    private final PlaylistDAO playlistDAO = ClientApp.getPlaylistDAO();

    /**
     * Metodo di inizializzazione chiamato alla creazione della vista.
     * Contiene il listener per l'aggiornamento delle colonne all'aggiunta o rimozione di una playlist
     */
    public void initialize() {

        addEmotionsInfoBtn();
    }

    /**
     * Aggiunge ad ogni riga il bottone per la visualizzazione della singola canzone
     * con il prospetto dei tag emozionali
     */
    private void addEmotionsInfoBtn() {
        TableColumn<Song, Void> emotionColumn = new TableColumn<>("Dettagli");
        Callback<TableColumn<Song, Void>, TableCell<Song, Void>> cellFactory = param -> new TableCell<>() {
            final HBox btnBox = new HBox();

            final Button viewBtn = new Button("Vedi emozioni");

            {
                viewBtn.setOnAction(event1 -> {
                    Song song = songsTable.getItems().get(getIndex());
                    context.setCurrentSong(song);
                    NodeHelpers.createStage(ClientApp.getWindow(), ClientApp.songInfoURL, "Info canzone", true);
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


        emotionColumn.setMinWidth(120);
        emotionColumn.setCellFactory(cellFactory);
        songsTable.getColumns().add(0, emotionColumn);
    }

    /**
     * Aggiunge ad ogni riga il bottone per aggiungere tag emozionali alla canzone
     */
    public void addEmotionAddBtn() {

        Callback<TableColumn<Song, Void>, TableCell<Song, Void>> cellFactory = param ->
                new TableCell<>() {

                    final HBox btnBox = new HBox();
                    private final Button emotionsAddBtn = new Button("Inserisci emozioni");

                    {
                        emotionsAddBtn.setOnAction(event1 -> {
                            Song song = getTableView().getItems().get(getIndex());
                            context.setCurrentSong(song);
                            NodeHelpers.createStage(ClientApp.getWindow(), ClientApp.ratingURL, "Inserisci emozioni", true);
                        });

                        btnBox.getChildren().add(emotionsAddBtn);
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


        TableColumn<Song, Void> emotionsAddColumn = new TableColumn<>("Inserisci");
        emotionsAddColumn.setMinWidth(150);
        emotionsAddColumn.setCellFactory(cellFactory);
        songsTable.getColumns().add(0, emotionsAddColumn);
    }

    /**
     * Aggiunge ad ogni riga un tasto per l'aggiunta di una canzone ad una playlist
     * in fase di creazione
     */
    public void addSongAddToPlaylistBtn() {

        TableColumn<Song, Void> addSongColumn = new TableColumn<>("Aggiungi");
        Callback<TableColumn<Song, Void>, TableCell<Song, Void>> cellFactory = param -> new TableCell<>() {
            final HBox btnBox = new HBox();

            final Button addBtn = new Button("Aggiungi");

            {
                addBtn.setOnAction(event1 -> {
                    Song song = songsTable.getItems().get(getIndex());
                    newPlaylistSongs.add(song);
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
        songsTable.getColumns().add(0, addSongColumn);
    }

    /**
     * Aggiunge ad ogni riga un tasto per la rimozione della canzone dalla playlist
     * in fase di ceazione con eliminazione opzionale per playlist già esistenti
     *
     * @param songList   la lista di canzoni della playlist da cui rimuovere
     * @param deleteSong flag per la cancellazione della canzone dalla playlist
     */
    public void addRemoveSongBtn(ObservableList<Song> songList, boolean deleteSong) {

        songsTable.getColumns().remove(songsTable.getColumns().size() - 1);

        TableColumn<Song, Void> removeSongColumn = new TableColumn<>("Rimuovi");
        Callback<TableColumn<Song, Void>, TableCell<Song, Void>> cellFactory = param -> new TableCell<>() {
            final HBox btnBox = new HBox();

            final Button addBtn = new Button("Rimuovi");

            {
                addBtn.setOnAction(event1 -> {
                    Song song = songsTable.getItems().get(getIndex());
                    songList.remove(song);

                    if (context.getCurrentPlaylist() != null && deleteSong) {
                        Playlist playlist = context.getCurrentPlaylist();
                        try {
                            int deleted = playlistDAO.deletePlaylistSong(playlist.getId(), song.id);
                            String msg = "";

                            if (deleted > 0) {
                                msg = "'" + song.getTitle() + "' è stata rimossa dalla playlist '" + playlist.getName() + "'";
                                NodeHelpers.createAlert(null, Alert.AlertType.CONFIRMATION, "Conferma", null, msg, false);
                            } else {
                                msg = "Non è stato possibile rimuovere '" + song.getTitle() + "' dalla playlist '" + playlist.getName() + "'";
                                NodeHelpers.createAlert(null, Alert.AlertType.WARNING, "Attenzione", null, msg, false);
                            }
                        } catch (RemoteException e) {
                            throw new RMIStubException(e);
                        }
                    }
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
        songsTable.getColumns().add(0, removeSongColumn);
    }

    /**
     * Aggiunge ad ogni riga un bottone di aggiunta rapida della canzone ad una determinata playlist
     */
    public void addPlaylistDropdown() {

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
                                    String msg;
                                    if (rows.length > 0) {
                                        msg = "'" + song.getTitle() + "' è stata aggiunta alla playlist '" + p.getName() + "'";
                                        NodeHelpers.createAlert(null, Alert.AlertType.CONFIRMATION, "Conferma", null, msg, false);
                                    } else {
                                        msg = "'" + song.getTitle() + "' è già presente in '" + p.getName() + "'";
                                        NodeHelpers.createAlert(null, Alert.AlertType.INFORMATION, "Info", null, msg, false);
                                    }
                                } catch (RemoteException e) {
                                    throw new RMIStubException(e);
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

        songsTable.getColumns().add(1, playlistColumn);
    }
}
