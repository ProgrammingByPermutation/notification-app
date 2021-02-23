package org.nullinside.notification_app.config;

import org.nullinside.notification_app.alerts.IAlert;

/**
 * Maps a class name to it's configuration in JSON format.
 */
public class AlertConfiguration {
    /**
     * The fully qualified class name.
     */
    public String className;
    /**
     * The configuration of the class in JSON format.
     */
    public String config;

    /**
     * Instantiates a instance of the class.
     */
    public AlertConfiguration() {

    }

    /**
     * Instantiates a instance of the class.
     *
     * @param className The fully qualified class name.
     * @param config    The configuration of the class in JSON format.
     */
    public AlertConfiguration(String className, String config) {
        this.className = className;
        this.config = config;
    }

    /**
     * Instantiates a instance of the class.
     *
     * @param alert The alert.
     */
    public AlertConfiguration(IAlert alert) {
        className = alert.getClass().getName();
        config = alert.getConfig();
    }
}
