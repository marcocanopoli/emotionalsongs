package client_gui;

import client.ClientApp;
import client.ClientContext;
import common.Emotion;
import common.Song;
import common.SongEmotion;
import common.User;
import common.interfaces.SongDAO;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

public class RatingViewController {
    @FXML
    private FlowPane emoContainer;
    @FXML
    public Label songAuthor;
    @FXML
    public Label songAlbum;
    @FXML
    public Label songTitle;
    @FXML
    public Label songYear;
    @FXML
    public Label songGenre;
    @FXML
    public Label songDuration;

    List<SongEmotion> songEmotions;

    SongDAO songDAO;

    User user;

    Song song;

    public void initialize() throws IOException {
        ClientContext context = ClientContext.getInstance();
        song = context.getCurrentSong();
        user = context.getUser();
        songDAO = ClientApp.getSongDAO();
        songEmotions = songDAO.getSongEmotionsRating(user.getId(), song.id);

        songAuthor.setText(song.getAuthor());
        songAlbum.setText(song.getAlbum());
        songTitle.setText(song.getTitle());
        songYear.setText(song.getYear());
        songGenre.setText(song.getGenre());
        songDuration.setText(song.getDuration());

        List<Emotion> emotions = context.getEmotions();

        initEmotions(emotions);
    }

    private void initEmotions(List<Emotion> emotions) {

        for (int i = 0; i < emotions.size(); i++) {

            Emotion emo = emotions.get(i);

            GridPane emoBox = new GridPane();
            emoBox.setHgap(20);
            emoBox.setVgap(20);

            ColumnConstraints column1 = new ColumnConstraints();
            column1.setFillWidth(true);

            ColumnConstraints column2 = new ColumnConstraints();
            column2.setFillWidth(true);
            column2.setHgrow(Priority.ALWAYS);
            column2.setMinWidth(150);

            RowConstraints secondRow = new RowConstraints();
            secondRow.setValignment(VPos.CENTER);

            RowConstraints firstRow = new RowConstraints();
            firstRow.setValignment(VPos.CENTER);

            emoBox.getColumnConstraints().add(column1);
            emoBox.getColumnConstraints().add(column2);
            emoBox.getRowConstraints().add(secondRow);
            emoBox.getRowConstraints().add(firstRow);

            int rating = 0;
            String notes = "";

            for (SongEmotion se : songEmotions) {
                if (se.emotionId() == (emo.id())) {
                    rating = se.rating();
                    notes = se.notes() == null ? "" : se.notes();
                }
            }

            ToggleGroup group = new ToggleGroup();

            addRatingSection(group, emoBox, emo.id(), rating);
            addCommentSection(emoBox, emo.id(), notes);

            TitledPane emoPane = new TitledPane(emo.name(), emoBox);
            emoPane.setMinWidth(400);
            emoPane.setMaxWidth(400);
            emoPane.setCollapsible(false);


            emoContainer.getChildren().add(emoPane);

        }

    }

    private void addRatingSection(ToggleGroup group, GridPane emoBox, int emotionId, int rating) {
        Button resetBtn = new Button("Reset rating");
        resetBtn.setMaxWidth(Double.MAX_VALUE);

        resetBtn.setOnAction(event -> {
            try {
                songDAO.deleteSongEmotion(user.getId(), song.id, emotionId);
                group.selectToggle(null);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        resetBtn.setDisable(rating == 0);

        emoBox.add(resetBtn, 1, 0);

        HBox radioGroup = new HBox();
        radioGroup.setSpacing(10);
        radioGroup.setAlignment(Pos.CENTER_LEFT);

        for (int i = 1; i < 6; i++) {

            group.selectedToggleProperty().addListener((observable, oldVal, newVal) ->
                    {

                        if (newVal != null) {

                            int newRating = (int) newVal.getUserData();

                            try {
                                songDAO.setSongEmotion(user.getId(), song.id, emotionId, newRating);
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
            );

            RadioButton radio = new RadioButton();
            radio.setText(String.valueOf(i));
            radio.setUserData(i);
            radio.setToggleGroup(group);
            radio.setSelected(rating == i);

            radioGroup.getChildren().add(radio);
        }
        group.selectedToggleProperty().addListener((obs, oldVal, newVal) ->
                resetBtn.setDisable(group.getSelectedToggle() == null));

        emoBox.add(radioGroup, 0, 0);
    }

    private void addCommentSection(GridPane emoBox, int emotionId, String notes) {

        Button commentBtn = new Button();
        commentBtn.setMaxWidth(Double.MAX_VALUE);
        commentBtn.setText(notes.isBlank() ? "Reset commento" : "Inserisci commento");

        TextArea comment = new TextArea();
        comment.setText(notes);
        comment.setWrapText(true);
        comment.setPromptText("Inserisci note riguardo l'emozione");
        comment.setPrefWidth(200);
        comment.setPrefHeight(50);

        comment.setTextFormatter(new TextFormatter<>(change -> {
            commentBtn.setText(change.getControlNewText().isBlank() ? "Reset commento" : "Inserisci commento");
            return change.getControlNewText().length() <= 256 ? change : null;
        }));


        commentBtn.setOnAction(event -> {
            try {
                songDAO.setSongEmotionNotes(user.getId(), song.id, emotionId, comment.getText());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        emoBox.add(commentBtn, 1, 1);
        emoBox.add(comment, 0, 1);
    }
}