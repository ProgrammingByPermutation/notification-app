package org.nullinside.notification_app.alerts;

/**
 * An event for subscribing to updates on the list of alerts.
 */
public interface AlertListUpdatedEvent {
    /**
     * Invoked on additions and removals of alerts.
     *
     * @param added True if the alert was added, false if removed.
     * @param alert The alert.
     */
    void onAlertListUpdated(boolean added, IAlert alert);
}
