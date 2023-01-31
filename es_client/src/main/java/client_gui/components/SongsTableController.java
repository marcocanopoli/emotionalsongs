package client_gui.components;

import client.ClientApp;
import client.ClientContext;
import common.NodeHelpers;
import common.Playlist;
import common.Song;
import common.interfaces.PlaylistDAO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.rmi.RemoteException;
import java.util.List;

public class SongsTableController {
    @FXML
    private TableView<Song> songsTable;
    private final ClientContext context = ClientContext.getInstance();
    private final ObservableList<Song> newPlaylistSongs = context.getNewPlaylistSongs();
    private final PlaylistDAO playlistDAO = ClientApp.getPlaylistDAO();

    public void initialize() {
        addEmotionsInfoBtn();
    }

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

    public void addTableEmotionAddBtn() {

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

    public void addRemoveSongBtn(ObservableList<Song> songList, boolean isCurrentPlaylist) {

        songsTable.getColumns().remove(songsTable.getColumns().size() - 1);

        TableColumn<Song, Void> removeSongColumn = new TableColumn<>("Rimuovi");
        Callback<TableColumn<Song, Void>, TableCell<Song, Void>> cellFactory = param -> new TableCell<>() {
            final HBox btnBox = new HBox();

            final Button addBtn = new Button("Rimuovi");

            {
                addBtn.setOnAction(event1 -> {
                    Song song = songsTable.getItems().get(getIndex());
                    songList.remove(song);

                    if (context.getCurrentPlaylist() != null && isCurrentPlaylist) {
                        try {
                            playlistDAO.deletePlaylistSong(context.getCurrentPlaylist().getId(), song.id);
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
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
                                        msg = "'" + song.getTitle() + "' è stata aggiunta alla playlist '" + p.getName() + "'!";
                                        NodeHelpers.createAlert(Alert.AlertType.CONFIRMATION, "Conferma", null, msg, false);
                                    } else {
                                        msg = "'" + song.getTitle() + "' è già presente in '" + p.getName() + "'!";
                                        NodeHelpers.createAlert(Alert.AlertType.INFORMATION, "Info", null, msg, false);
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

        songsTable.getColumns().add(1, playlistColumn);
    }
}
