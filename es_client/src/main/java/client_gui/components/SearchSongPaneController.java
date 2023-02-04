package client_gui.components;

import client.ClientApp;
import client.ClientContext;
import common.Song;
import common.interfaces.SongDAO;
import exceptions.DAOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Controller per FXML del pannello di ricerca delle canzoni.
 * Contiene funzionalità di ricerca per titolo, per autore o per autore/anno
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 */
public class SearchSongPaneController {
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
    private ClientContext context;
    private SongDAO songDAO;

    /**
     * Metodo di inizializzazione chiamato alla creazione della vista.
     * Contiene i listener per la validazione delle stringhe in input
     */
    public void initialize() {
        songDAO = ClientApp.getSongDAO();
        context = ClientContext.getInstance();

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
    }

    /**
     * Metodo di ricerca per titolo.
     * Al click sul bottone, prende la stringa in input e la utilizza per la ricerca a database tramite DAO
     */
    @FXML
    private void searchByTitle() {
        String title = titleInput.getText().trim();

        try {
            List<Song> results = songDAO.getSongsByTitle(title);
            context.setSearchedSongs(results);
            if (!results.isEmpty()) titleInput.clear();

        } catch (RemoteException e) {
            throw new DAOException(e);
        }

    }

    /**
     * Metodo di ricerca per autore e titolo.
     * Al click sul bottone, prende la stringa in input e la utilizza per la ricerca a database tramite DAO
     * E' possibile ricercare per autore e per autore/anno, ma non solo per anno
     */
    @FXML
    public void searchByAuthorYear() {
        String author = authorInput.getText().trim();
        Integer year = yearInput.getText().isBlank() ? null : Integer.parseInt(yearInput.getText());

        try {
            List<Song> results = songDAO.getSongsByAuthorYear(author, year);
            context.setSearchedSongs(results);
            if (!results.isEmpty()) authorInput.clear();
            if (!results.isEmpty()) yearInput.clear();
        } catch (RemoteException e) {
            throw new DAOException(e);
        }
    }

}
