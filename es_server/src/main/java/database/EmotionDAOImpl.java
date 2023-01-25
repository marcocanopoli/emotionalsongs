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

public class EmotionDAOImpl implements EmotionDAO {

    public EmotionDAOImpl(Registry registry) throws RemoteException {
        EmotionDAO emotionDAOStub = (EmotionDAO) UnicastRemoteObject.exportObject(this, 3939);
        registry.rebind("EmotionService", emotionDAOStub);
    }

    @Override
    public List<Emotion> getEmotions() throws RemoteException {
        Connection conn = ServerApp.getConnection();

        final String QUERY = "SELECT * "
                + "FROM emotions "
                + "ORDER BY id ASC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(QUERY)) {
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
