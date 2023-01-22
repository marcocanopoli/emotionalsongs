package client_gui;

import client.ClientApp;
import client.ClientContext;
import common.Playlist;
import common.Song;
import common.User;
import common.interfaces.SongDAO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.rmi.RemoteException;
import java.util.List;

public class SongsListController {
    @FXML
    private TableView<Song> searchSongsTable;
    @FXML
    private Button searchBtn;
    @FXML
    private TextField searchText;


    public void initialize() {
        ClientContext context = ClientContext.getInstance();
        User user = context.getUser();

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
        ObservableList<Playlist> userPlaylists = context.getUserPlaylists();

        Callback<TableColumn<Song, Void>, TableCell<Song, Void>> cellFactory = param ->
                new TableCell<>() {
                    final HBox btnBox = new HBox();
                    private final MenuButton playlistChoice = new MenuButton("Aggiungi a:");

                    {
//                        Song song = getTableView().getItems().get(getIndex());
                        for (Playlist p : userPlaylists) {
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
