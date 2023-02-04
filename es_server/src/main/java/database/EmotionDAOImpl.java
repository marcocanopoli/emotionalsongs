package database;

import common.Emotion;
import common.interfaces.EmotionDAO;
import server.ServerApp;
import server.ServerLogger;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione dell'interfaccia <code>EmotionDAO</code>
 *
 * @see EmotionDAO
 */
public class EmotionDAOImpl implements EmotionDAO {

    /**
     * Costruttore della classe.
     * Si occupa del bind dello stub al registry
     *
     * @param registry il registo RMI
     * @throws RemoteException se la comunicazione col registry o l'export falliscono
     */
    public EmotionDAOImpl(Registry registry) throws RemoteException {
        EmotionDAO emotionDAOStub = (EmotionDAO) UnicastRemoteObject.exportObject(this, 3939);
        registry.rebind("EmotionService", emotionDAOStub);
    }

    //================================================================================
    // SELECT
    //================================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Emotion> getAllEmotions() {
        Connection conn = ServerApp.getConnection();

        final String query = EmotionDAO.emoSelQueries.get(EmoSel.ALL_EMO);

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            List<Emotion> results = new ArrayList<>();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");

                results.add(new Emotion(id, name, description));
            }
            return results;

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
            return null;
        }

    }
}
