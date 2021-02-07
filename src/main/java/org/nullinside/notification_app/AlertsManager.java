package org.nullinside.notification_app;

import org.nullinside.notification_app.alerts.IAlert;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class AlertsManager {
    private static AlertsManager instance;
    private final ArrayList<IAlert> alerts = new ArrayList<>();
    private int nextId = 0;
    private final ArrayList<AlertListUpdatedListener> alertsChanged = new ArrayList<>();
    private final Thread alertRunnerThread;
    private boolean poisonPill;
    private final Semaphore waitHandle;

    private AlertsManager() {
        waitHandle = new Semaphore(0);
        alertRunnerThread = new Thread(this::performAlertChecking);
        alertRunnerThread.setName("Alert Manager Thread");
        alertRunnerThread.setDaemon(true);
        alertRunnerThread.start();
    }

    private void performAlertChecking() {
        while(!poisonPill) {
            try {
                waitHandle.tryAcquire(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
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
        poisonPill = true;
        waitHandle.release();
        try {
            alertRunnerThread.join(30000);

            if (alertRunnerThread.isAlive()) {
                alertRunnerThread.interrupt();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        for (var alert : alerts) {
            alert.dispose();
        }

        alerts.clear();
        alertsChanged.clear();
        instance = null;
    }
}
