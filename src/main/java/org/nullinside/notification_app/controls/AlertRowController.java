package org.nullinside.notification_app.controls;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.nullinside.notification_app.App;
import org.nullinside.notification_app.alerts.IAlert;
import org.nullinside.notification_app.controllers.AbstractBaseController;

public class AlertRowController extends AbstractBaseController {
    public Button deleteButton;
    public VBox alertContainer;
    public CheckBox enabledCheckBox;
    private IAlert alert;

    @FXML
    public void initialize() {
        HBox.setHgrow(alertContainer, Priority.ALWAYS);

        if (null == alert) {
            return;
        }

        enabledCheckBox.setSelected(alert.getIsEnabled());
    }

    @FXML
    public void onEnabledChanged(ActionEvent event) {
        CheckBox checkbox = (CheckBox) event.getSource();
        alert.setIsEnabled(checkbox.isSelected());
    }

    @FXML
    public void onEditClicked(ActionEvent event) {
        App.setRoot(alert.getGui());
    }

    public IAlert getAlert() {
        return alert;
    }

    public void setAlert(IAlert alert) {
        this.alert = alert;
        alert.setPreviewGui(alertContainer);
        initialize();
    }
}
