package client_gui;

import client.ClientApp;
import client.ClientContext;
import common.Playlist;
import common.Song;
import common.interfaces.PlaylistDAO;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.rmi.RemoteException;
import java.util.List;

public class CurrentPlaylistController {

    @FXML
    public Label playlistName;
    @FXML
    public Label playlistSongs;
    @FXML
    public Label playlistDuration;
    @FXML
    private TableView<Song> playlistSongsTable;

    public void initialize() {
        ClientContext context = ClientContext.getInstance();

        context.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals("playlist")) {

                Playlist currentPlaylist = (Playlist) e.getNewValue();

                if (currentPlaylist != null) {
                    try {
                        setCurrentPlaylist(currentPlaylist);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        addTableEmotionAddBtn(context);
    }

    private void setCurrentPlaylist(Playlist playlist) throws RemoteException {
        PlaylistDAO playlistDAO = ClientApp.getPlaylistDAO();
        List<Song> songs = playlistDAO.getPlaylistSongs(playlist.getId());
        Integer duration = 0;

        for (Song song : songs) {
            duration += song.getDurationInt();
        }

        String durationString = duration == 0 ? "0" : String.format("%d:%02d:%02d", duration / 3600, (duration % 3600) / 60, (duration % 60));

        playlistName.setText(playlist.getName());
        playlistSongs.setText(String.valueOf(songs.size()));
        playlistDuration.setText(durationString);

        playlistSongsTable.getItems().clear();
        playlistSongsTable.getItems().addAll(songs);


    }

    private void addTableEmotionAddBtn(ClientContext context) {

        Callback<TableColumn<Song, Void>, TableCell<Song, Void>> cellFactory = param ->
                new TableCell<>() {

                    final HBox btnBox = new HBox();
                    private final Button emotionsAddBtn = new Button("Inserisci emozioni");

                    {
                        emotionsAddBtn.setOnAction(event1 -> {
                            Song song = getTableView().getItems().get(getIndex());
                            context.setCurrentSong(song);
                            ClientApp.createStage("ratingView.fxml", "Inserisci emozioni", true);
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
        playlistSongsTable.getColumns().add(emotionsAddColumn);
    }
}

