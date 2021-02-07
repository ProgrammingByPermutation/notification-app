package org.nullinside.notification_app;

import org.nullinside.notification_app.alerts.IAlert;

public class AlertConfiguration {
    public String className;
    public String config;

    public AlertConfiguration() {

    }

    public AlertConfiguration(String className, String config) {
        this.className = className;
        this.config = config;
    }

    public AlertConfiguration(IAlert alert) {
        className = alert.getClass().getName();
        config = alert.getConfig();
    }
}
