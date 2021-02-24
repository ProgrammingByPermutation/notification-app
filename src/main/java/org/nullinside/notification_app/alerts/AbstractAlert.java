package org.nullinside.notification_app.alerts;

import org.nullinside.notification_app.config.AbstractAlertConfig;
import org.nullinside.notification_app.config.GlobalConfig;

import java.util.ArrayList;

/**
 * An abstract alert containing common operations by an alert object.
 */
public abstract class AbstractAlert implements IAlert {
    /**
     * Subscribes to the alert's enabled state changing
     */
    private final ArrayList<AlertEnableUpdateEvent> alertEnabledSubscribers = new ArrayList<>();
    /**
     * The unique identifier of the alert per the containing alert manager.
     */
    private int id;

    /**
     * Gets the unique identifier of the alert per the containing alert manager.
     *
     * @return The id.
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the alert per the containing alert manager.
     *
     * @param id The id.
     */
    @Override
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets a flag indicating whether the alert is enabled.
     *
     * @return True if enabled, false otherwise.
     */
    @Override
    public boolean getIsEnabled() {
        var config = getConfigObject();
        if (null == config) {
            return false;
        }

        // Get it, it's atomic.
        return config.getIsEnabled();
    }

    /**
     * Sets a flag indicating whether the alert is enabled.
     *
     * @param isEnabled True if enabled, false otherwise.
     */
    @Override
    public void setIsEnabled(boolean isEnabled) {
        var config = getConfigObject();
        if (null == config) {
            return;
        }

        // If there are no changes, do nothing.
        if (config.getIsEnabled() == isEnabled) {
            return;
        }

        // Set it, it's atomic.
        config.setIsEnabled(isEnabled);

        // When we update the enabled state we need to update the written configuration file.
        GlobalConfig.getInstance().updateSavedConfigurations();

        // Update the subscribers with changes to the enabled state.
        invokeEnabledChanged(isEnabled);
    }

    /**
     * Invokes the enabled changed subscribers.
     *
     * @param isEnabled True if the alert is enabled, false otherwise.
     */
    private void invokeEnabledChanged(boolean isEnabled) {
        ArrayList<AlertEnableUpdateEvent> subs;
        synchronized (alertEnabledSubscribers) {
            subs = new ArrayList<>(alertEnabledSubscribers);
        }

        for (var sub : subs) {
            try {
                sub.onAlertEnableChanged(isEnabled, this);
            } catch (Exception e) {
                // Do nothing with exceptions, the people that threw them are terrible.
                e.printStackTrace();
            }
        }
    }

    /**
     * Subscribes to the alert's enabled state changing through {@link #setIsEnabled(boolean)}.
     *
     * @param sub The subscription to add.
     */
    @Override
    public void addEnabledChangedListener(AlertEnableUpdateEvent sub) {
        boolean subToConfig;
        synchronized (alertEnabledSubscribers) {
            subToConfig = alertEnabledSubscribers.size() == 0;
            alertEnabledSubscribers.add(sub);
        }

        // If someone subscribes to us, they want to know about updates to the underlying config.
        if (subToConfig) {
            var config = getConfigObject();
            config.addEnabledSubscribers((enabled, configObj) -> {
                invokeEnabledChanged(enabled);
            });
        }
    }

    /**
     * Gets the interval at which the alert should check if it should alert.
     *
     * @return The update interval in milliseconds.
     */
    @Override
    public long getUpdateInterval() {
        var config = getConfigObject();
        if (null == config) {
            return -1;
        }

        return config.getUpdateInterval();
    }

    /**
     * Sets the interval at which the alert should check if it should alert.
     *
     * @param updateInterval The update interval in milliseconds.
     */
    @Override
    public void setUpdateInterval(long updateInterval) {
        var config = getConfigObject();
        if (null == config) {
            return;
        }

        config.setUpdateInterval(updateInterval);

        // When we update the interval we need to update the written configuration file.
        GlobalConfig.getInstance().updateSavedConfigurations();
    }

    /**
     * Gets the alert configuration.
     *
     * @return The alert configuration object.
     */
    protected abstract AbstractAlertConfig getConfigObject();
}
