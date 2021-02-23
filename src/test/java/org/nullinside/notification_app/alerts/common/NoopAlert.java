package org.nullinside.notification_app.alerts.common;

import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import org.nullinside.notification_app.alerts.AbstractAlert;
import org.nullinside.notification_app.config.AbstractAlertConfig;

public class NoopAlert extends AbstractAlert {
    /**
     * True if dispose was called at some point, false otherwise.
     */
    public boolean disposeWasCalled = false;

    /**
     * Gets the alert configuration.
     *
     * @return The alert configuration object.
     */
    @Override
    protected AbstractAlertConfig getConfigObject() {
        return null;
    }

    /**
     * Gets the alert configuration in JSON string format.
     *
     * @return The alert configuration in JSON string format.
     */
    @Override
    public String getConfig() {
        return null;
    }

    /**
     * Sets the alert configuration in JSON string format.
     *
     * @param config The alert configuration in JSON string format.
     */
    @Override
    public void setConfig(String config) {

    }

    /**
     * Gets the window GUI to display when configuring the alert.
     *
     * @return The window GUI to display when configuring the alert.
     */
    @Override
    public Parent getGui() {
        return null;
    }

    /**
     * Sets the preview GUI to display in the list of all alerts.
     * <p>
     * The parent row that houses a preview of this Alert's configuration is
     * passed in and the alert is given an opportunity to add any UI elements
     * it would like.
     *
     * @param parent The parent that houses the alert in the alert list.
     */
    @Override
    public void setPreviewRow(VBox parent) {

    }

    /**
     * Executes the logic of alert. This includes identifying if an alert condition
     * exists and providing some kind of indication to the user that it happened.
     */
    @Override
    public void check() {

    }

    /**
     * Dispose of managed and unmanaged resources used by the alert.
     */
    @Override
    public void dispose() {
        disposeWasCalled = true;
    }
}
