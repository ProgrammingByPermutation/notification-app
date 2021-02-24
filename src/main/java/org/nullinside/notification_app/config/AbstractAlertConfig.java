package org.nullinside.notification_app.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract configuration containing common data for alert configurations.
 */
public abstract class AbstractAlertConfig {
    /**
     * Subscribes to the alert's enabled state changing
     */
    @JsonIgnore
    private final ArrayList<EnableUpdateEvent> enabledSubscribers;
    /**
     * True if enabled, false otherwise.
     */
    private boolean isEnabled;
    /**
     * The update interval in milliseconds.
     */
    private long updateInterval;

    /**
     * Instantiates a new instance of the class.
     */
    protected AbstractAlertConfig() {
        enabledSubscribers = new ArrayList<>();
    }

    /**
     * Copy constructor.
     *
     * @param config The object to copy.
     */
    protected AbstractAlertConfig(AbstractAlertConfig config) {
        this.enabledSubscribers = new ArrayList<>(config.enabledSubscribers);
        this.isEnabled = config.isEnabled;
        this.updateInterval = config.updateInterval;
    }

    /**
     * Gets the interval at which the alert should check if it should alert.
     *
     * @return The update interval in milliseconds.
     */
    public long getUpdateInterval() {
        synchronized (this) {
            return updateInterval;
        }
    }

    /**
     * Sets the interval at which the alert should check if it should alert.
     *
     * @param updateInterval The update interval in milliseconds.
     */
    public void setUpdateInterval(long updateInterval) {
        synchronized (this) {
            this.updateInterval = updateInterval;
        }
    }

    /**
     * Gets a flag indicating whether the alert is enabled.
     *
     * @return True if enabled, false otherwise.
     */
    public boolean getIsEnabled() {
        return isEnabled;
    }

    /**
     * Sets a flag indicating whether the alert is enabled.
     *
     * @param isEnabled True if enabled, false otherwise.
     */
    public void setIsEnabled(boolean isEnabled) {
        if (isEnabled == this.isEnabled) {
            return;
        }

        this.isEnabled = isEnabled;

        // Update the subscribers with changes to the enabled state.
        ArrayList<EnableUpdateEvent> subs;
        synchronized (enabledSubscribers) {
            subs = new ArrayList<>(enabledSubscribers);
        }

        for (var sub : subs) {
            try {
                sub.onEnableChanged(this.isEnabled, this);
            } catch (Exception e) {
                // Do nothing with exceptions, the people that threw them are terrible.
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets the subscriptions to the alert's enabled state changing.
     *
     * @return The subscribers.
     */
    public List<EnableUpdateEvent> getEnabledSubscribers() {
        synchronized (enabledSubscribers) {
            return new ArrayList<>(enabledSubscribers);
        }
    }

    /**
     * Subscribes to the alert's enabled state changing through {@link #setIsEnabled(boolean)}.
     *
     * @param subs The subscription to add.
     */
    public void addEnabledSubscribers(List<EnableUpdateEvent> subs) {
        synchronized (enabledSubscribers) {
            subs.stream().filter(sub -> !enabledSubscribers.contains(sub)).forEach(enabledSubscribers::add);
        }
    }

    /**
     * Subscribes to the alert's enabled state changing through {@link #setIsEnabled(boolean)}.
     *
     * @param sub The subscription to add.
     */
    public void addEnabledSubscribers(EnableUpdateEvent sub) {
        synchronized (enabledSubscribers) {
            enabledSubscribers.add(sub);
        }
    }

    /**
     * An event for subscribing to updates made through {@link AbstractAlertConfig#setIsEnabled(boolean)}.
     */
    public interface EnableUpdateEvent {
        /**
         * Invoked on changes to the enabled state of alerts.
         *
         * @param enabled True if enabled, false if disabled.
         * @param config  The configuration.
         */
        void onEnableChanged(boolean enabled, AbstractAlertConfig config);
    }
}
