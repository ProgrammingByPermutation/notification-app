package org.nullinside.notification_app.controllers;

/**
 * An event dictating that the GUI associated with a controller closed.
 */
public interface ControllerClosedEvent {
    /**
     * Invoked when the GUI associated with a controller is closed.
     *
     * @param saved True if the configuration was saved, false if changes were discarded.
     */
    void onClosed(boolean saved);
}
