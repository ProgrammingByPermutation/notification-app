package org.nullinside.notification_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.nullinside.notification_app.App;
import org.nullinside.notification_app.alerts.IAlert;
import org.nullinside.notification_app.alerts.TwitchChatAlert;

import java.util.HashMap;

public class AppController {
    public HashMap<Integer, HBox> alertIdToGui = new HashMap<>();
    public VBox alarmList;

    @FXML
    public void showSettings() {
        App.setRoot("controllers/settings");
    }

    public void addTwitchChat() {
        addAlert(new TwitchChatAlert());
    }

    private void addAlert(IAlert alert) {
        App.addAlert(alert);
        var remove = new Button("X");
        remove.setOnAction(actionEvent -> {
            App.removeAlert(alert.getId());
            var gui = alertIdToGui.getOrDefault(alert.getId(), null);
            if (null != gui) {
                alarmList.getChildren().remove(gui);
            }
        });

        var edit = new Button("Edit");
        edit.setOnAction(actionEvent -> App.setRoot(alert.getGui()));
        var alertContainer = new HBox();
        var customAlertContainer = new VBox();
        alertContainer.setStyle("-fx-border-color: black;");
        alert.setPreviewGui(customAlertContainer);
        alertContainer.getChildren().addAll(customAlertContainer, edit, remove);
        HBox.setHgrow(customAlertContainer, Priority.ALWAYS);
        alarmList.getChildren().add(alertContainer);
        alertIdToGui.put(alert.getId(), alertContainer);
    }
}
