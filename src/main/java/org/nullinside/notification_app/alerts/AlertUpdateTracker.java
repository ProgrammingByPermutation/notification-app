package org.nullinside.notification_app.alerts;

public class AlertUpdateTracker {
    public IAlert alert;
    public long nextUpdateTime;

    public AlertUpdateTracker(IAlert alert, long nextUpdateTime) {
        this.alert = alert;
        this.nextUpdateTime = nextUpdateTime;
    }
}
