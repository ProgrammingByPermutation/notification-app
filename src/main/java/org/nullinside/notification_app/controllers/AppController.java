package org.nullinside.notification_app.controllers;

import javafx.fxml.FXML;
import org.nullinside.notification_app.App;

import java.io.IOException;

public class AppController {
    @FXML
    public void showSettings() throws IOException {
        App.setRoot("controllers/settings");
    }
}
