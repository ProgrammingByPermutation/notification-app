package org.nullinside.notification_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.nullinside.notification_app.App;
import org.nullinside.notification_app.alerts.AlertsManager;
import org.nullinside.notification_app.alerts.IAlert;
import org.nullinside.notification_app.alerts.TwitchChatAlert;
import org.nullinside.notification_app.controls.AlertRowController;

import java.io.IOException;
import java.util.HashMap;

/**
 * The main GUI controller.
 */
public class AppController extends AbstractBaseController {
    /**
     * The collection mapping alert unique identifier to its GUI component.
     */
    public HashMap<Integer, Parent> alertIdToGui = new HashMap<>();
    /**
     * The GUI component containing the list of created alarms.
     */
    public VBox alarmList;

    /**
     * Initializes the JavaFX GUI.
     */
    @FXML
    public void initialize() {
        var manager = AlertsManager.getInstance();
        for (var alert : manager.getAlerts()) {
            addAlert(alert);
        }
    }

    /**
     * Shows the {@link org.nullinside.notification_app.config.GlobalConfig} settings dialog.
     */
    @FXML
    public void showSettings() {
        App.setRoot("controllers/settings");
    }

    /**
     * Adds a {@link TwitchChatAlert}.
     */
    public void addTwitchChat() {
        var manager = AlertsManager.getInstance();
        var alert = new TwitchChatAlert();
        manager.addAlert(alert);
        addAlert(alert);
    }

    /**
     * Adds a new alert to the {@link #alarmList}.
     *
     * @param alert The alert to add.
     */
    private void addAlert(IAlert alert) {
        Pair<Object, Parent> pair;
        try {
            pair = App.loadFXML("controls/alertRow");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        var controller = (AlertRowController) pair.getKey();
        controller.setAlert(alert);
        controller.deleteButton.setOnAction(event -> {
            var manager = AlertsManager.getInstance();
            manager.removeAlert(alert.getId());
            var gui = alertIdToGui.getOrDefault(alert.getId(), null);
            if (null != gui) {
                alarmList.getChildren().remove(gui);
            }
        });

        alarmList.getChildren().add(pair.getValue());
        alertIdToGui.put(alert.getId(), pair.getValue());
    }
}
