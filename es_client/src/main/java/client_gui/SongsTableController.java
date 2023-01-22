package client_gui;

import client.ClientApp;
import client.ClientContext;
import common.Playlist;
import common.Song;
import common.User;
import common.interfaces.PlaylistDAO;
import common.interfaces.SongDAO;
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
    @FXML
    private TableColumn<Song, String> authorColumn;
    @FXML
    private TableColumn<Song, String> albumColumn;
    @FXML
    private TableColumn<Song, String> yearColumn;
    @FXML
    private TableColumn<Song, String> titleColumn;
    @FXML
    private TableColumn<Song, String> genreColumn;
    @FXML
    private TableColumn<Song, String> durationColumn;
    @FXML
    private TableColumn<Song, Void> emotionViewColumn;


    public void initialize() {
        ClientContext context = ClientContext.getInstance();
        User user = context.getUser();

        SongDAO songDAO = ClientApp.getSongDAO();

//        searchBtn.setOnAction(event -> {
//            String searched = searchText.getText().trim();
//
//            try {
//                if (!searched.isEmpty()) {
//                    List<Song> results = songDAO.searchByString(searched);
//                    authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
//                    albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
//                    yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
//                    titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
//                    genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
//                    durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
//
//                    addEmotionsBtns();
//
//                    if (user != null) {
//                        addPlaylistDropdown(user.getID());
//                    }
//
//                    songsTable.getItems().clear();
//                    songsTable.getItems().addAll(results);
//
//                }
//            } catch (RemoteException e) {
//                throw new RuntimeException(e);
//            }
//        });
    }

    private void addEmotionsBtns() {
        ClientContext context = ClientContext.getInstance();

        Callback<TableColumn<Song, Void>, TableCell<Song, Void>> cellFactory = param ->
                new TableCell<>() {
                    final HBox btnContainer;

                    private final Button viewBtn = new Button("Vedi");
                    private final Button insertBtn = new Button("Inserisci");

                    {
                        viewBtn.setOnAction(event1 -> {
                            Song song = getTableView().getItems().get(getIndex());
                            context.setCurrentSong(song);
                            ClientApp.createStage("songInfoView.fxml", "Info canzone", true);
                        });

                        insertBtn.setOnAction(event1 -> {
                            Song song = getTableView().getItems().get(getIndex());
                            context.setCurrentSong(song);
                            ClientApp.createStage("songInfoView.fxml", "Info canzone", true);
                        });

                        User user = context.getUser();

                        if (user != null) {
                            btnContainer = new HBox(10, viewBtn, insertBtn);
                        } else {
                            btnContainer = new HBox(viewBtn);
                        }
                        btnContainer.setAlignment(Pos.CENTER);
                    }


                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnContainer);
                        }
                    }
                };

        emotionViewColumn.setCellFactory(cellFactory);
    }

    private void addPlaylistDropdown(int userId) throws RemoteException {
        TableColumn<Song, Void> playlistColumn = new TableColumn<>("Aggiungi alla playlist");
        PlaylistDAO playlistDAO = ClientApp.getPlaylistDAO();
        List<Playlist> playlists = playlistDAO.getUserPlaylists(userId);

        Callback<TableColumn<Song, Void>, TableCell<Song, Void>> cellFactory = param ->
                new TableCell<>() {

                    private final MenuButton playlistChoice = new MenuButton("Aggiungi a:");

                    {
//                        Song song = getTableView().getItems().get(getIndex());
                        for (Playlist p : playlists) {
                            MenuItem item = new MenuItem(p.getName());
                            item.setOnAction(event -> {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Conferma");
                                alert.setHeaderText(null);
                                alert.setContentText("La canzone è stata aggiunta alla playlist '" + p.getName() + "'!");

                                alert.showAndWait();
                            });
                            playlistChoice.getItems().add(item);
                        }

                        playlistChoice.setAlignment(Pos.CENTER);

                    }


                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(playlistChoice);
                        }
                    }
                };

        playlistColumn.setCellFactory(cellFactory);
        songsTable.getColumns().add(playlistColumn);
    }

}
