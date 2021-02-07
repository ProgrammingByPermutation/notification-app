package org.nullinside.notification_app.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.nullinside.notification_app.AlertsManager;
import org.nullinside.notification_app.App;
import org.nullinside.notification_app.alerts.IAlert;
import org.nullinside.notification_app.alerts.TwitchChatAlert;

import java.util.HashMap;

public class AppController extends AbstractBaseController {
    public HashMap<Integer, HBox> alertIdToGui = new HashMap<>();
    public VBox alarmList;

    @FXML
    public void initialize() {
        var manager = AlertsManager.getInstance();
        for (var alert : manager.getAlerts()) {
            addAlert(alert);
        }
    }

    @FXML
    public void showSettings() {
        App.setRoot("controllers/settings");
    }

    public void addTwitchChat() {
        var manager = AlertsManager.getInstance();
        var alert = new TwitchChatAlert();
        manager.addAlert(alert);

        addAlert(alert);
    }

    private void addAlert(IAlert alert) {
        var remove = new Button("X");
        remove.setOnAction(actionEvent -> {
            var manager = AlertsManager.getInstance();
            manager.removeAlert(alert.getId());
            var gui = alertIdToGui.getOrDefault(alert.getId(), null);
            if (null != gui) {
                alarmList.getChildren().remove(gui);
            }
        });

        var edit = new Button("Edit");
        edit.setOnAction(actionEvent -> App.setRoot(alert.getGui()));
        var alertContainer = new HBox();
        var customAlertContainer = new VBox();
        var enabled = new CheckBox();
        enabled.setSelected(alert.getIsEnabled());
        enabled.setOnAction(actionEvent -> alert.setIsEnabled(((CheckBox) actionEvent.getSource()).isSelected()));
        enabled.setPadding(new Insets(0, 10, 0, 0));
        alertContainer.setStyle("-fx-border-color: black;");
        alert.setPreviewGui(customAlertContainer);
        alertContainer.getChildren().addAll(enabled, customAlertContainer, edit, remove);
        HBox.setHgrow(customAlertContainer, Priority.ALWAYS);
        alarmList.getChildren().add(alertContainer);
        alertIdToGui.put(alert.getId(), alertContainer);
    }
}
