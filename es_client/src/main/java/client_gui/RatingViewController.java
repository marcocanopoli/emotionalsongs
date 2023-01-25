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
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

public class RatingViewController {
    @FXML
    public VBox emotionsBox;

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

        List<Emotion> emotions = context.getEmotions();

        initEmotions(emotions);
    }

    private void initEmotions(List<Emotion> emotions) {

//        HBox labels = new HBox();
//        labels.setSpacing(24);
//        labels.setPadding(new Insets(0, 0, 0, 75));

        for (Emotion emo : emotions) {
            HBox emoBox = new HBox();
            emoBox.setSpacing(15);
            emoBox.setAlignment(Pos.CENTER_LEFT);

            ToggleGroup group = new ToggleGroup();

            int rating = 0;
            String notes = null;

            for (SongEmotion se : songEmotions) {
                if (se.emotionId() == (emo.id())) {
                    rating = se.rating();
                    notes = se.notes();
                }
            }

            Label emoName = new Label(emo.name());
            emoBox.getChildren().add(emoName);

            addResetBtn(group, emoBox, emo.id());
            addToggles(group, emoBox, emo.id(), rating);
            addCommentSection(emoBox, emo.id(), notes);

            emotionsBox.getChildren().add(emoName);
//            TitledPane emoPane = new TitledPane();
//            emoPane.setText(emo.name());
//            emoPane.setContent(emoBox);
            emotionsBox.getChildren().add(emoBox);

        }

    }

    private void addResetBtn(ToggleGroup group, HBox emoBox, int emotionId) {
        Button resetBtn = new Button("Reset");

        resetBtn.setOnAction(event -> {
            try {
                songDAO.deleteSongEmotion(user.getId(), song.id, emotionId);
                group.selectToggle(null);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        emoBox.getChildren().add(resetBtn);
    }

    private void addToggles(ToggleGroup group, HBox emoBox, int emotionId, int rating) {
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

            emoBox.getChildren().add(radio);
        }
    }

    private void addCommentSection(HBox emoBox, int emotionId, String notes) {

        VBox notesBox = new VBox();
        notesBox.setSpacing(10);

        ButtonBar btnBar = new ButtonBar();

        Button commentBtn = new Button("Salva commento");
        commentBtn.setDisable(true);

        Button resetCommentBtn = new Button("Reset commento");
        commentBtn.setDisable(true);

        TextArea comment = new TextArea();
        comment.setText(notes);
        comment.setWrapText(true);
        comment.setPromptText("Inserisci commento");
        comment.setPrefWidth(200);
        comment.setPrefHeight(50);

        comment.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= 256 ? change : null));


        commentBtn.setOnAction(event -> {
            try {
                songDAO.setSongEmotionNotes(user.getId(), song.id, emotionId, comment.getText());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });


        comment.textProperty().addListener((observable, oldValue, newValue) ->
                commentBtn.setDisable(newValue.isEmpty()));

        emoBox.getChildren().add(commentBtn);
        emoBox.getChildren().add(comment);
    }
}