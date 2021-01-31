package org.nullinside.notification_app;

import org.nullinside.notification_app.alerts.IAlert;

import java.util.ArrayList;

public class AlertsManager {
    private final ArrayList<IAlert> alerts = new ArrayList<>();
    private int nextId = 0;

    public void addAlert(IAlert alert) {
        alert.setId(++nextId);
        alerts.add(alert);
    }

    public void removeAlert(IAlert alert) {
        alerts.remove(alert);
        alert.dispose();
    }

    public void removeAlert(int id) {
        var alert = this.alerts.stream().filter(a -> a.getId() == id).findFirst().orElse(null);
        if (null == alert) {
            return;
        }

        alerts.remove(alert);
        alert.dispose();
    }
}
