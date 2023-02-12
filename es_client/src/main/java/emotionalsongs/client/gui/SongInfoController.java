package emotionalsongs.client.gui;

import emotionalsongs.client.ClientApp;
import emotionalsongs.client.ClientContext;
import emotionalsongs.common.Emotion;
import emotionalsongs.common.Song;
import emotionalsongs.common.SongEmotion;
import emotionalsongs.common.interfaces.SongDAO;
import emotionalsongs.exceptions.RMIStubException;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller per FXML dela vista di dettaglio di una singola canzone.
 * Mostra i dettagli della canzone e mostra statistiche sul totale di emozioni per la canzone.
 * Permette inoltre di visualizzare eventuali note registrate dagli utenti per la singola emozione.
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 */
public class SongInfoController {
    @FXML
    public TitledPane titledEmoPane;
    @FXML
    private VBox emotionsBox;
    @FXML
    private Label songAuthor;
    @FXML
    private Label songAlbum;
    @FXML
    private Label songTitle;
    @FXML
    private Label songYear;
    @FXML
    private Label songGenre;
    @FXML
    private Label songDuration;
    @FXML
    private ListView<String> notesList;
    @FXML
    private TextArea currentNote;
    private final ClientContext context = ClientContext.getInstance();
    private final SongDAO songDAO = ClientApp.getSongDAO();
    private final Song song = context.getCurrentSong();
    private final List<Emotion> emotions = context.getEmotions();
    private final ObservableList<String> emotionNotes = FXCollections.observableArrayList();
    private Integer total = 0;
    private Integer ratingTotal = 0;
    private final List<SongEmotion> songEmotions = new ArrayList<>();
    private final Float[] percentages = new Float[emotions.size()];
    private final int[] votesCount = new int[emotions.size()];
    private final int[] ratingTotals = new int[emotions.size()];
    private final Float[] ratingAvg = new Float[emotions.size()];

    /**
     * Metodo di inizializzazione chiamato alla creazione della finestra.
     * Setta i dettagli della canzone e recupera informazioni sui rating e note
     * delle singole emozioni per la data canzone.
     *
     * @see ClientContext
     */
    public void initialize() {

        songAuthor.setText(song.getAuthor());
        songAlbum.setText(song.getAlbum());
        songTitle.setText(song.getTitle());
        songYear.setText(song.getYear());
        songGenre.setText(song.getGenre());
        songDuration.setText(song.getDuration());

        getRatingTotals();

        titledEmoPane.setText("Emozioni registrate (" + total + " totali)");

        Property<ObservableList<String>> notesProperty = new SimpleObjectProperty<>(emotionNotes);
        notesList.itemsProperty().bind(notesProperty);

        addEmotionBoxes();
    }

    /**
     * Crea dinamicamente un componente di riepilogo per ogni emozione disponibile sul DB.
     * Mostra il numero di rating sul totale con una <code>ProgressBar</code> associata,
     * ed un bottone per recuperare le note della singola emozione.
     */
    private void addEmotionBoxes() {

        for (Emotion emo : emotions) {

            int emoId = emo.id();
            int emoIdx = emoId - 1;

            GridPane emoBox = new GridPane();
            emoBox.setMinHeight(30);
            emoBox.setHgap(10);

            ColumnConstraints column1 = new ColumnConstraints(100);
            ColumnConstraints column2 = new ColumnConstraints(210);
            ColumnConstraints column3 = new ColumnConstraints();
            ColumnConstraints column4 = new ColumnConstraints();
            ColumnConstraints column5 = new ColumnConstraints();
            ColumnConstraints column6 = new ColumnConstraints(90);
            column1.setHalignment(HPos.LEFT);
            column3.setHalignment(HPos.RIGHT);
            column4.setHalignment(HPos.RIGHT);
            column5.setHalignment(HPos.RIGHT);
            column6.setHalignment(HPos.RIGHT);
            column3.setMinWidth(50);
            column4.setMinWidth(80);
            column5.setMinWidth(130);
            column6.setHgrow(Priority.NEVER);
            emoBox.getColumnConstraints().add(column1);
            emoBox.getColumnConstraints().add(column2);
            emoBox.getColumnConstraints().add(column3);
            emoBox.getColumnConstraints().add(column4);
            emoBox.getColumnConstraints().add(column5);
            emoBox.getColumnConstraints().add(column6);

            Label emoLabel = new Label(emo.name());
            emoLabel.setPrefHeight(16);
            emoLabel.setPrefWidth(88);

            ProgressBar bar = new ProgressBar(0.0);
            bar.setMinWidth(200);
            bar.setProgress(percentages[emoIdx]);

            Button viewNotesBtn = new Button("Vedi note");
            viewNotesBtn.setOnAction(event -> getAndSetNotes(emoId));

            Label percLabel = new Label();
            Label partialLabel = new Label();
            Label avgLabel = new Label();

            String percentage = String.format("%.2f", percentages[emoIdx] * 100) + "%";
            String partial = votesCount[emoIdx] + " voti";
            String avg = "Media rating: " + String.format("%.2f", ratingAvg[emoIdx]);

            percLabel.setText(percentage);
            partialLabel.setText(partial);
            avgLabel.setText(avg);

            emoBox.add(emoLabel, 0, 1);
            emoBox.add(bar, 1, 1);
            emoBox.add(percLabel, 2, 1);
            emoBox.add(partialLabel, 3, 1);
            emoBox.add(avgLabel, 4, 1);
            emoBox.add(viewNotesBtn, 5, 1);

            emotionsBox.getChildren().add(emoBox);
        }
    }

    /**
     * Recupera le note per l'emozione scelta e le mostra in una lista.
     * Al click sulla singola nota, la stessa viene riproposta in maniera estesa in
     * una <code>TextArea</code> dedicata per facilitare la lettura delle note più lunghe.
     *
     * @param emotionId l'id dell'emozione scelta
     */
    private void getAndSetNotes(int emotionId) {
        try {
            List<String> notes = songDAO.getSongEmotionNotes(song.id, emotionId);

            if (!notes.isEmpty()) {
                emotionNotes.setAll(notes);
            }
        } catch (RemoteException e) {
            throw new RMIStubException(e);
        }

        notesList.setOnMouseClicked(row -> {
            String note = notesList.getSelectionModel().getSelectedItem();
            currentNote.setText(note);
        });
    }

    /**
     * Recupera da DB il totale di voti per le emozioni e ne calcola la percentuale sul totale
     */
    private void getRatingTotals() {
        try {
            songEmotions.addAll(songDAO.getSongEmotions(song.id));
            int[] emotionVotes = new int[emotions.size()];

            for (SongEmotion se : songEmotions) {
                total++;
                int emoId = se.emotionId() - 1;
                int rating = se.rating();
                ratingTotal += rating;
                ratingTotals[emoId] += rating;
                emotionVotes[emoId] += 1;
            }

            for (Emotion emo : emotions) {
                int id = emo.id() - 1;
                int count = emotionVotes[id];
                float percentage = ratingTotal > 0 ? (float) ratingTotals[id] / ratingTotal : 0;
                votesCount[id] = count;
                percentages[id] = percentage;
                ratingAvg[id] = ratingTotals[id] > 0 ? (float) ratingTotals[id] / count : 0;
            }

        } catch (RemoteException e) {
            throw new RMIStubException(e);
        }
    }
}