package org.nullinside.notification_app;

import org.nullinside.notification_app.alerts.IAlert;

import java.util.ArrayList;

public class AlertsManager {
    private static AlertsManager instance;
    private final ArrayList<IAlert> alerts = new ArrayList<>();
    private int nextId = 0;
    private final ArrayList<AlertListUpdatedListener> alertsChanged = new ArrayList<>();

    private AlertsManager() {

    }

    public static AlertsManager getInstance() {
        if (null == instance) {
            instance = new AlertsManager();
        }

        return instance;
    }

    public void addAlert(IAlert alert) {
        alert.setId(++nextId);
        alerts.add(alert);

        for (var event : alertsChanged) {
            event.onAlertListUpdated(true, alert);
        }
    }

    public void removeAlert(IAlert alert) {
        alerts.remove(alert);

        for (var event : alertsChanged) {
            event.onAlertListUpdated(false, alert);
        }

        alert.dispose();
    }

    public void removeAlert(int id) {
        var alert = alerts.stream().filter(a -> a.getId() == id).findFirst().orElse(null);
        if (null == alert) {
            return;
        }

        removeAlert(alert);
    }

    public IAlert[] getAlerts() {
        return alerts.toArray(new IAlert[0]);
    }

    public void addAlertsUpdatedListener(AlertListUpdatedListener listener) {
        alertsChanged.add(listener);
    }

    public void dispose() {
        for (var alert : alerts) {
            alert.dispose();
        }

        alerts.clear();
        alertsChanged.clear();
        instance = null;
    }
}
