package org.nullinside.notification_app.alerts;

import javafx.scene.Parent;
import javafx.scene.layout.VBox;

/**
 * The interface Alert.
 */
public interface IAlert {
    /**
     * Gets the unique identifier of the alert per the containing alert manager.
     *
     * @return The id.
     */
    int getId();

    /**
     * Sets the unique identifier of the alert per the containing alert manager.
     *
     * @param id The id.
     */
    void setId(int id);

    /**
     * Gets a flag indicating whether the alert is enabled.
     *
     * @return True if enabled, false otherwise.
     */
    boolean getIsEnabled();

    /**
     * Sets a flag indicating whether the alert is enabled.
     *
     * @param isEnabled True if enabled, false otherwise.
     */
    void setIsEnabled(boolean isEnabled);

    /**
     * Subscribes to the alert's enabled state changing through {@link #setIsEnabled(boolean)}.
     *
     * @param sub The subscription to add.
     */
    void addEnabledChangedListener(AlertEnableUpdateEvent sub);

    /**
     * Gets the interval at which the alert should check if it should alert.
     *
     * @return The update interval in milliseconds.
     */
    long getUpdateInterval();

    /**
     * Sets the interval at which the alert should check if it should alert.
     *
     * @param updateInterval The update interval in milliseconds.
     */
    void setUpdateInterval(long updateInterval);

    /**
     * Gets the alert configuration in JSON string format.
     *
     * @return The alert configuration in JSON string format.
     */
    String getConfig();

    /**
     * Sets the alert configuration in JSON string format.
     *
     * @param config The alert configuration in JSON string format.
     */
    void setConfig(String config);

    /**
     * Gets the window GUI to display when configuring the alert.
     *
     * @return The window GUI to display when configuring the alert.
     */
    Parent getGui();

    /**
     * Sets the preview GUI to display in the list of all alerts.
     * <p>
     * The parent row that houses a preview of this Alert's configuration is
     * passed in and the alert is given an opportunity to add any UI elements
     * it would like.
     *
     * @param parent The parent that houses the alert in the alert list.
     */
    void setPreviewRow(VBox parent);

    /**
     * Executes the logic of alert. This includes identifying if an alert condition
     * exists and providing some kind of indication to the user that it happened.
     */
    void check();

    /**
     * Dispose of managed and unmanaged resources used by the alert.
     */
    void dispose();
}
