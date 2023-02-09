package emotionalsongs.client.gui;

import emotionalsongs.client.ClientApp;
import emotionalsongs.client.ClientContext;
import emotionalsongs.common.Emotion;
import emotionalsongs.common.Song;
import emotionalsongs.common.SongEmotion;
import emotionalsongs.common.User;
import emotionalsongs.common.interfaces.SongDAO;
import emotionalsongs.exceptions.RMIStubException;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

/**
 * Controller per FXML della vista di inserimento tag emozionali per ogni canzone.
 * Permette di inserire un rating da 1 a 5 ed un commento per ogni emozione.
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 */
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

    ClientContext context = ClientContext.getInstance();
    List<SongEmotion> songEmotions;
    SongDAO songDAO = ClientApp.getSongDAO();
    User user = context.getUser();
    Song song = context.getCurrentSong();
    List<Emotion> emotions = context.getEmotions();
    HashMap<Integer, Button> notesInsertBtns = new HashMap<>();
    HashMap<Integer, Integer> emotionsRatings = new HashMap<>();
    HashMap<Integer, String> emotionsNotes = new HashMap<>();


    /**
     * Metodo di inizializzazione chiamato alla creazione della vista.
     * Mostra i dettagli della canzone e inizializza i componenti di rating
     */
    public void initialize() throws IOException {

        songEmotions = songDAO.getUserSongEmotionsCountRating(user.getId(), song.id);

        songAuthor.setText(song.getAuthor());
        songAlbum.setText(song.getAlbum());
        songTitle.setText(song.getTitle());
        songYear.setText(song.getYear());
        songGenre.setText(song.getGenre());
        songDuration.setText(song.getDuration());

        initEmotions();
    }

    /**
     * Inizializza i componenti di rating per le singole emozioni.
     * I pannelli sono creati dinamicamente a partire dalle emozioni disponibili
     * sul DB per agevolare la possibile aggiunta di emozioni
     */
    private void initEmotions() {

        for (Emotion emo : emotions) {

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
            emotionsRatings.put(emo.id(), rating);
            emotionsNotes.put(emo.id(), notes);

            ToggleGroup group = new ToggleGroup();

            addCommentSection(emoBox, emo.id(), notes);
            addRatingSection(group, emoBox, emo.id(), rating);

            TitledPane emoPane = new TitledPane(emo.name(), emoBox);
            emoPane.setMinWidth(400);
            emoPane.setMaxWidth(400);
            emoPane.setCollapsible(false);

            emoContainer.getChildren().add(emoPane);

        }

    }

    /**
     * Aggiunge la sezione di rating al pannello della singola emozione.
     * Setta i listener che inseriscono a DB i nuovi rating
     *
     * @param group     il <code>ToggleGroup</code> in cui inserire i toggle dei rating
     * @param emoBox    il contenitore della sigola emozione
     * @param emotionId l'id dell'emozione
     * @param rating    il rating iniziale, se esiste
     */
    private void addRatingSection(ToggleGroup group, GridPane emoBox, int emotionId, int rating) {
        Button resetBtn = new Button("Reset rating");
        resetBtn.setMaxWidth(Double.MAX_VALUE);

        resetBtn.setOnAction(event -> {
            try {
                songDAO.deleteSongEmotion(user.getId(), song.id, emotionId);
                group.selectToggle(null);
            } catch (RemoteException e) {
                throw new RMIStubException(e);
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
                                songDAO.setSongEmotion(user.getId(), song.id, emotionId, newRating, emotionsNotes.get(emotionId));
                                emotionsRatings.replace(emotionId, rating);
                            } catch (RemoteException e) {
                                throw new RMIStubException(e);
                            }
                        }
                        notesInsertBtns.get(emotionId).setDisable(newVal == null);
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

    /**
     * Aggiunge l'input per i commenti alla singola emozione.
     * Setta un bottone di invio/reset a database
     *
     * @param emoBox    il contenitore della singola emozione
     * @param emotionId l'id dell'emozione
     * @param notes     le note iniziali, se esistono
     */
    private void addCommentSection(GridPane emoBox, int emotionId, String notes) {

        Button commentBtn = new Button();
        commentBtn.setMaxWidth(Double.MAX_VALUE);
        commentBtn.setText(notes.isBlank() ? "Reset commento" : "Inserisci commento");
        commentBtn.setDisable(emotionsRatings.get(emotionId) == 0);

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
                String newComment = comment.getText() != null ? comment.getText() : "";
                songDAO.setSongEmotion(user.getId(), song.id, emotionId, emotionsRatings.get(emotionId), newComment);
                emotionsNotes.replace(emotionId, newComment);
            } catch (RemoteException e) {
                throw new RMIStubException(e);
            }
        });

        notesInsertBtns.put(emotionId, commentBtn);

        emoBox.add(commentBtn, 1, 1);
        emoBox.add(comment, 0, 1);
    }
}