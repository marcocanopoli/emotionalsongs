package client_gui;

import client.EsClientMain;
import common.interfaces.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.rmi.RemoteException;

public class EsClientController {
    @FXML
    public Button add;

    @FXML
    protected void add() throws RemoteException {
        UserService userService = EsClientMain.getUserService();
        userService.addUser();
    }
}