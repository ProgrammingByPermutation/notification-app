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

/**
 * The controller for mapping an alert GUI row to the alert object it represents.
 */
public class AlertRowController extends AbstractBaseController {
    /**
     * The delete alert button.
     */
    public Button deleteButton;
    /**
     * The container of the {@link #alert}'s {@link IAlert#setPreviewRow(VBox)}.
     */
    public VBox alertContainer;
    /**
     * The checkbox representing if the alert is enabled.
     */
    public CheckBox enabledCheckBox;
    /**
     * The alert.
     */
    private IAlert alert;

    /**
     * Initializes the JavaFX GUI.
     */
    @FXML
    public void initialize() {
        HBox.setHgrow(alertContainer, Priority.ALWAYS);

        if (null == alert) {
            return;
        }

        enabledCheckBox.setSelected(alert.getIsEnabled());
    }

    /**
     * Handles changes to the enabled state in the GUI.
     *
     * @param event The {@link CheckBox} event of the enabled state changing.
     */
    @FXML
    public void onEnabledChanged(ActionEvent event) {
        CheckBox checkbox = (CheckBox) event.getSource();
        alert.setIsEnabled(checkbox.isSelected());
    }

    /**
     * Handles navigating to the alert's configuration GUI.
     *
     * @param event The button's event.
     */
    @FXML
    public void onEditClicked(ActionEvent event) {
        App.setRoot(alert.getGui());
    }

    /**
     * Gets the alert associated with the control.
     *
     * @return The alert.
     */
    public IAlert getAlert() {
        return alert;
    }

    /**
     * Sets the alert associated with the control.
     *
     * @param alert The alert.
     */
    public void setAlert(IAlert alert) {
        this.alert = alert;
        alert.setPreviewRow(alertContainer);
        initialize();
    }
}
