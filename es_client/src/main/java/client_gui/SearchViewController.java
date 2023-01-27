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

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.util.List;

public class SearchViewController {
    @FXML
    public TextField authorInput;
    @FXML
    public TextField titleInput;
    @FXML
    public TextField yearInput;
    @FXML
    public Button byTitleBtn;
    @FXML
    public Button byAuthorYearBtn;
    @FXML
    private TableView<Song> searchSongsTable;
    private ClientContext context;
    private PlaylistDAO playlistDAO;
    private SongDAO songDAO;

    public void initialize() {
        playlistDAO = ClientApp.getPlaylistDAO();
        songDAO = ClientApp.getSongDAO();
        context = ClientContext.getInstance();

        User user = context.getUser();

        context.addPropertyChangeListener(this::userChangeListener);


        titleInput.textProperty().addListener((observable, oldValue, newValue) -> {
            byAuthorYearBtn.setDefaultButton(false);
            byTitleBtn.setDefaultButton(true);
            byTitleBtn.setDisable(newValue.isEmpty());
        });

        authorInput.textProperty().addListener((observable, oldValue, newValue) -> {
            String year = yearInput.getText();
            byTitleBtn.setDefaultButton(false);
            byAuthorYearBtn.setDefaultButton(true);
            byAuthorYearBtn.setDisable(newValue.isEmpty() || !year.isBlank() && year.length() != 4);
        });

        yearInput.textProperty().addListener((observable, oldValue, newValue) -> {
            byTitleBtn.setDefaultButton(false);
            byAuthorYearBtn.setDefaultButton(true);
            byAuthorYearBtn.setDisable((newValue.length() > 0 && newValue.length() < 4) || authorInput.getText().isBlank());
        });

        yearInput.setTextFormatter(new TextFormatter<>(change ->
        {
            if (change.getControlNewText().matches("[0-9]*") &&
                    change.getControlNewText().length() <= 4) {

                return change;
            } else {

                return null;
            }
        }));

        addEmotionsBtns();

        if (user != null) {
            addPlaylistDropdown();
        }

    }

    private void userChangeListener(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("user")) {

            User newUser = (User) e.getNewValue();

            if (newUser != null) {
                addPlaylistDropdown();
            } else {
                int tableSize = searchSongsTable.getColumns().size();
                searchSongsTable.getColumns().remove(tableSize - 1);
            }
        }
    }

    private void addEmotionsBtns() {

        TableColumn<Song, Void> emotionColumn = new TableColumn<>("Emozioni");
        Callback<TableColumn<Song, Void>, TableCell<Song, Void>> cellFactory = param -> new TableCell<>() {
            final HBox btnBox = new HBox();

            final Button viewBtn = new Button("Vedi dettagli");

            {
                viewBtn.setOnAction(event1 -> {
                    Song song = searchSongsTable.getItems().get(getIndex());
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


        emotionColumn.setMinWidth(120);
        emotionColumn.setCellFactory(cellFactory);
        searchSongsTable.getColumns().add(emotionColumn);
    }

    private void addPlaylistDropdown() {

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
                                        alert.setHeaderText(null);
                                        alert.setContentText("'" + song.getTitle() + "' è stata aggiunta alla playlist '" + p.getName() + "'!");

                                        alert.showAndWait();
                                    } else {
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("Info");
                                        alert.setHeaderText(null);
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

    @FXML
    private void searchByTitle() {
        String title = titleInput.getText().trim();

        try {
            List<Song> results = songDAO.searchByTitle(title);
            searchSongsTable.getItems().clear();
            searchSongsTable.getItems().addAll(results);
            if (!results.isEmpty()) titleInput.clear();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void searchByAuthorYear() {
        String author = authorInput.getText().trim();
        Integer year = yearInput.getText().isBlank() ? null : Integer.parseInt(yearInput.getText());

        try {
            List<Song> results = songDAO.searchByAuthorYear(author, year);
            searchSongsTable.getItems().clear();
            searchSongsTable.getItems().addAll(results);
            if (!results.isEmpty()) authorInput.clear();
            if (!results.isEmpty()) yearInput.clear();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


}
