package org.nullinside.notification_app.alerts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class AlertsManager {
    private static AlertsManager instance;
    private final LinkedList<AlertUpdateTracker> alerts = new LinkedList<>();
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
        while (!poisonPill) {
            while (alerts.size() > 0 && alerts.getFirst().nextUpdateTime <= System.currentTimeMillis()) {
                var currentAlert = alerts.getFirst();
                if (currentAlert.alert.getIsEnabled()) {
                    currentAlert.alert.check();
                }
                currentAlert.nextUpdateTime = currentAlert.alert.getUpdateInterval() + System.currentTimeMillis();
                alerts.removeFirst();
                insertAlertSequentially(currentAlert);
            }

            try {
                long wait = 1000;
                if (alerts.size() > 0) {
                    wait = alerts.getFirst().nextUpdateTime - System.currentTimeMillis();

                    if (wait < 0) {
                        wait = 0;
                    }
                }

                waitHandle.tryAcquire(wait, TimeUnit.MILLISECONDS);
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
        alerts.addFirst(new AlertUpdateTracker(alert, System.currentTimeMillis()));

        for (var event : alertsChanged) {
            event.onAlertListUpdated(true, alert);
        }

        waitHandle.release();
    }

    private void insertAlertSequentially(AlertUpdateTracker alert) {
        for (int i = 0; i < alerts.size(); i++) {
            if (alerts.get(i).nextUpdateTime <= alert.nextUpdateTime) {
                alerts.add(i, alert);
                return;
            }
        }

        alerts.addLast(alert);
    }

    public void removeAlert(IAlert alert) {
        var foundAlert = alerts.stream().filter(a -> a.alert == alert).findFirst().orElse(null);
        if (null == foundAlert) {
            return;
        }

        removeAlert(foundAlert);
    }

    public void removeAlert(int id) {
        var alert = alerts.stream().filter(a -> a.alert.getId() == id).findFirst().orElse(null);
        if (null == alert) {
            return;
        }

        removeAlert(alert);
    }

    private void removeAlert(AlertUpdateTracker alert) {
        alerts.remove(alert);

        for (var event : alertsChanged) {
            event.onAlertListUpdated(false, alert.alert);
        }

        alert.alert.dispose();
    }

    public IAlert[] getAlerts() {
        return alerts.stream().map(alert -> alert.alert).toArray(IAlert[]::new);
    }

    public void addAlertsUpdatedListener(AlertListUpdatedListener listener) {
        alertsChanged.add(listener);
    }

    public void removeAlertsUpdatedListener(AlertListUpdatedListener listener) {
        alertsChanged.remove(listener);
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
            alert.alert.dispose();
        }

        alerts.clear();
        alertsChanged.clear();
        instance = null;
    }
}
