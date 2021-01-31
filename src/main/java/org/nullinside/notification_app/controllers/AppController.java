package org.nullinside.notification_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.nullinside.notification_app.App;
import org.nullinside.notification_app.alerts.TwitchChatAlert;

public class AppController {
    public VBox alarmList;

    @FXML
    public void showSettings() {
        App.setRoot("controllers/settings");
    }

    public void addTwitchChat() {
        var alert = new TwitchChatAlert();
        App.addAlert(alert);

        var alertContainer = new VBox();
        alert.setPreviewGui(alertContainer);
        alarmList.getChildren().add(alertContainer);
    }
}
