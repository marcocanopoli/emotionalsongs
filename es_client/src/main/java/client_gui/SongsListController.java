package client_gui;

import client.ClientApp;
import client.ClientContext;
import common.Playlist;
import common.Song;
import common.User;
import common.interfaces.PlaylistDAO;
import common.interfaces.SongDAO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

public class SongsListController {
    @FXML
    private TableView<Song> searchSongsTable;
    @FXML
    private Button searchBtn;
    @FXML
    private TextField searchText;

    private HashMap<Integer, String> columns;


    public void initialize() {
        ClientContext context = ClientContext.getInstance();
        User user = context.getUser();

        context.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals("user")) {

                User newUser = (User) e.getNewValue();

                if (newUser != null) {
                    addPlaylistDropdown(context);
                } else {
                    int tableSize = searchSongsTable.getColumns().size();
                    searchSongsTable.getColumns().remove(tableSize - 1);
                }
            }
        });

        SongDAO songDAO = ClientApp.getSongDAO();

        addEmotionsBtns(context);

        if (user != null) {
            addPlaylistDropdown(context);
        }

        searchBtn.setOnAction(event -> {
            String searched = searchText.getText().trim();

            try {
                if (!searched.isEmpty()) {
                    List<Song> results = songDAO.searchByString(searched);
                    searchSongsTable.getItems().clear();
                    searchSongsTable.getItems().addAll(results);
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void addEmotionsBtns(ClientContext context) {

        Callback<TableColumn<Song, Void>, TableCell<Song, Void>> cellFactory = param ->
                new TableCell<>() {

                    final HBox btnBox = new HBox();
                    private final Button viewBtn = new Button("Vedi dettagli");

                    {
                        viewBtn.setOnAction(event1 -> {
                            Song song = getTableView().getItems().get(getIndex());
                            context.setCurrentSong(song);
                            ClientApp.createStage("songInfoView.fxml", "Info canzone", true);
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


        TableColumn<Song, Void> emotionColumn = new TableColumn<>("Emozioni");
        emotionColumn.setMinWidth(120);
        emotionColumn.setCellFactory(cellFactory);
        searchSongsTable.getColumns().add(emotionColumn);
    }

    private void addPlaylistDropdown(ClientContext context) {
        PlaylistDAO playlistDAO = ClientApp.getPlaylistDAO();
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
                                    int rows = playlistDAO.addSongToPlaylist(p.getId(), song.id);
                                    if (rows > 0) {
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("Conferma");
//                                        alert.setHeaderText(null);
                                        alert.setContentText("'" + song.getTitle() + "' è stata aggiunta alla playlist '" + p.getName() + "'!");

                                        alert.showAndWait();
                                    } else {
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("Info");
//                                        alert.setHeaderText(null);
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
