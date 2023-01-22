package client_gui;

import client.ClientApp;
import client.ClientContext;
import common.Playlist;
import common.Song;
import common.User;
import common.interfaces.PlaylistDAO;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

public class PlaylistsController {

    @FXML
    public Label playlistName;
    @FXML
    public Label playlistSongs;
    @FXML
    public Label playlistDuration;
    @FXML
    private TableView<Song> playlistSongsTable;
    //    @FXML
//    private TitledPane currentPlaylistPane;
    @FXML
    private ListView<Playlist> playlistsList;
    @FXML
    private Button newPlaylistBtn;

    public void initialize() throws IOException {
        PlaylistDAO playlistDAO = ClientApp.getPlaylistDAO();
        ClientContext context = ClientContext.getInstance();

        addTableEmotionAddBtn(context);

//        initSongsTable();
        initPlaylistList(playlistDAO, context);

        newPlaylistBtn.setOnAction(event -> {
            ClientApp.createStage("newPlaylistView.fxml", "Nuova playlist", true);
        });
    }

    private void setCurrentPlaylist(PlaylistDAO playlistDAO, Playlist playlist) throws RemoteException {

        List<Song> songs = playlistDAO.getPlaylistSongs(playlist.getId());
        Integer duration = 0;

        for (Song song : songs) {
            duration += song.getDurationInt();
        }

        String durationString = duration == 0 ? "0" : String.format("%d:%02d:%02d", duration / 3600, (duration % 3600) / 60, (duration % 60));

        playlistName.setText(playlist.getName());
        playlistSongs.setText(String.valueOf(songs.size()));
        playlistDuration.setText(durationString);

//        currentPlaylistPane.setExpanded(true);

        playlistSongsTable.getItems().clear();
        playlistSongsTable.getItems().addAll(songs);


    }


    private void initPlaylistList(PlaylistDAO playlistDAO, ClientContext context) throws RemoteException {
        User user = context.getUser();
        List<Playlist> playlists = playlistDAO.getUserPlaylists(user.getID());

        context.setUserPlaylists(playlists);

        ObservableList<Playlist> userPlaylists = context.getUserPlaylists();

        userPlaylists.addListener((ListChangeListener.Change<? extends Playlist> playlist) ->
                playlistsList.setItems(FXCollections.observableArrayList(userPlaylists))
        );

        playlistsList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Playlist playlist, boolean empty) {
                super.updateItem(playlist, empty);
                setText(empty ? "" : playlist.getName());
            }
        });


//        playlistsList.getItems().clear();
        playlistsList.setItems(FXCollections.observableArrayList(userPlaylists));

        playlistsList.setOnMouseClicked(playlist -> {
            if (playlistsList.getSelectionModel().getSelectedItem() != null) {

                try {
                    setCurrentPlaylist(playlistDAO, playlistsList.getSelectionModel().getSelectedItem());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
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


        TableColumn<Song, Void> emotionColumn = new TableColumn<>("Emozioni");
        emotionColumn.setMinWidth(150);
        emotionColumn.setCellFactory(cellFactory);
        playlistSongsTable.getColumns().add(emotionColumn);
    }
}

