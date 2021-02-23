package org.nullinside.notification_app.config;

/**
 * An abstract configuration containing common data for alert configurations.
 */
public abstract class AbstractAlertConfig {
    /**
     * True if enabled, false otherwise.
     */
    public boolean isEnabled;
    /**
     * The update interval in milliseconds.
     */
    private long updateInterval;

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
}
