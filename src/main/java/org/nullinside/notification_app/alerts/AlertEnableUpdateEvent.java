package org.nullinside.notification_app.alerts;

/**
 * An event for subscribing to updates made through {@link IAlert#setIsEnabled(boolean)}.
 */
public interface AlertEnableUpdateEvent {
    /**
     * Invoked on changes to the enabled state of alerts.
     *
     * @param enabled True if enabled, false if disabled.
     * @param alert   The alert.
     */
    void onAlertEnableChanged(boolean enabled, IAlert alert);
}
