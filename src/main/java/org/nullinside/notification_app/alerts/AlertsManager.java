package org.nullinside.notification_app.alerts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * The manager responsible for containing the list of alerts and executing them.
 */
public class AlertsManager {
    /**
     * The singleton instance of this class.
     */
    private static AlertsManager instance;
    /**
     * The collection of all current alerts.
     */
    private final LinkedList<AlertUpdateTracker> alerts = new LinkedList<>();
    /**
     * The collection of alerts that were requested for removal outside of the {@link #alertRunnerThread}.
     */
    private final ArrayList<AlertUpdateTracker> alertsToRemove = new ArrayList<>();
    /**
     * The list of subscriptions to invoke when changes are made to the {@link #alerts} list.
     */
    private final ArrayList<AlertListUpdatedEvent> alertsListChangedSubscribers = new ArrayList<>();
    /**
     * The thread that handles updating the alerts.
     */
    private final Thread alertRunnerThread;
    /**
     * The wait handle for pausing the {@link #alertRunnerThread} between alert updates.
     */
    private final Semaphore waitHandle;
    /**
     * The next unique identifier to use for an added alert.
     */
    private int nextId = 0;
    /**
     * The poison pill for killing the {@link #alertRunnerThread}.
     */
    private boolean poisonPill;

    /**
     * Instantiates a new alerts manager object. It is private to preserve the singleton without
     * allowing outside instantiation.
     */
    private AlertsManager() {
        waitHandle = new Semaphore(0);
        alertRunnerThread = new Thread(this::performAlertChecking);
        alertRunnerThread.setName("Alert Manager Thread");
        alertRunnerThread.setDaemon(true);
        alertRunnerThread.start();
    }

    /**
     * Gets or creates the singleton instance of this class.
     *
     * @return The alerts manager instance.
     */
    public static AlertsManager getInstance() {
        if (null == instance) {
            instance = new AlertsManager();
        }

        return instance;
    }

    /**
     * The main loop of the {@link #alertRunnerThread} responsible for running the alerts.
     */
    private void performAlertChecking() {
        // While we have not been killed, perform the checking.
        while (!poisonPill) {
            // The list of alerts will always be organized where the next soonest alert that needs to be updated
            // will be towards the front of the list. As such, we simply we need to, starting from the beginning,
            // loop through the list and stop when the next update time is larger than the current time. After that,
            // the list will always be later and we don't need to worry about it.
            ArrayList<AlertUpdateTracker> alertsToUpdate = new ArrayList<>();
            AlertUpdateTracker nextSoonestAlert = null;
            synchronized (alerts) {
                // Get rid of any alerts the user requested we get rid of.
                synchronized (alertsToRemove) {
                    for (var alert : alertsToRemove) {
                        alert.alert.dispose();
                        alerts.remove(alert);
                    }
                }

                // Loop through each alert and determine:
                // 1. What we need to update now (alertsToUpdate)
                // 2. The first thing we'll need to update on the next loop (nextSoonestAlert) so that we know
                //    how long to wait before looping again.
                for (int i = 0; i < alerts.size(); i++) {
                    // Check if the current alert should be updated.
                    var currentAlert = alerts.getFirst();
                    if (currentAlert.nextUpdateTime > System.currentTimeMillis()) {
                        // If it shouldn't, we're done here.
                        nextSoonestAlert = currentAlert;
                        break;
                    }

                    // Add to the list we'll actually iterate though.
                    alertsToUpdate.add(currentAlert);

                    // Update the alert update time according to the update interval.
                    currentAlert.nextUpdateTime = currentAlert.alert.getUpdateInterval() + System.currentTimeMillis();

                    // Remove it from the list and re-insert it in the correct position in the list.
                    // If this becomes too slow, refactor so that we group alerts in a list per update interval. That way
                    // instead of O(n) search time over all alerts we'll be at O(n) search time over the same update
                    // intervals, which practically speaking, will almost always be a smaller list.
                    alerts.removeFirst();
                }

                // We do this separately because technically speaking an alert could have a update interval that is
                // so small we keep spinning on it and never to get to any of the others. Doing this separate from
                // the loop above ensure that we visit everyone we have to.
                for (var alert : alertsToUpdate) {
                    insertAlertSequentially(alert);
                }

                // If there wasn't something we aren't processing this round, then the first thing
                // in the list will be the next soonest thing we need to update when we're done with this loop.
                if (null == nextSoonestAlert && alerts.size() > 0) {
                    nextSoonestAlert = alerts.getFirst();
                }
            }

            for (var alert : alertsToUpdate) {
                // Perform the check
                if (alert.alert.getIsEnabled()) {
                    alert.alert.check();
                }
            }

            // By default, wait 1 second while we have nothing to do.
            long wait = 1000;
            if (null != nextSoonestAlert) {
                // Otherwise, the first thing in the list is the next thing we need to update so determine
                // how many milliseconds between now and then.
                wait = nextSoonestAlert.nextUpdateTime - System.currentTimeMillis();

                // If something goes wrong, go with no wait. We might have a race condition where between
                // the above code and here we should be checking the next alert. It would be rare but it could
                // happen.
                if (wait < 0) {
                    wait = 0;
                }
            }

            try {
                // Use the wait handle to wait for the time to expire. If we need to check sooner or get poisoned
                // out of this loop the outside world can use the wait handle to force a loop.
                waitHandle.tryAcquire(wait, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // We've been disposed. Technically speaking clearing out things below is a race condition.
        // If we were disposed in the middle of someone adding a new alert, for example, we could leave
        // the synchronize block below and immediately end up with a new alert in the collection.
        //
        // Practically speaking, we only dispose of this object when the program exits. Since the window
        // will be locked out and they won't be able to add a new alert, that shouldn't actually happen.
        synchronized (alerts) {
            for (var alert : alerts) {
                alert.alert.dispose();
            }

            alerts.clear();
        }

        synchronized (alertsToRemove) {
            alertsToRemove.clear();
        }

        // Clear out the subscribers.
        synchronized (alertsListChangedSubscribers) {
            alertsListChangedSubscribers.clear();
        }

        // Get rid of the singleton instance.
        instance = null;
    }

    /**
     * Add an alert to the manager.
     * <p>
     * It will automatically be checked when enabled.
     *
     * @param alert The alert.
     */
    public void addAlert(IAlert alert) {
        // Set and increment the id.
        alert.setId(++nextId);

        // Add the alert to the beginning of the list so that it gets called for the first time.
        synchronized (alerts) {
            alerts.addFirst(new AlertUpdateTracker(alert, System.currentTimeMillis()));
            alert.addEnabledChangedListener((enabled, updatedAlert) -> {
                if (!enabled) {
                    return;
                }

                synchronized (alerts) {
                    // Find the alert in our collection, we need to put it at the front of the list.
                    var alertWrapper = alerts.stream().filter(a -> a.alert == alert)
                            .findFirst()
                            .orElse(null);

                    if (null == alertWrapper) {
                        return;
                    }

                    // If we found it, do the update.
                    alertWrapper.nextUpdateTime = System.currentTimeMillis();
                    alerts.remove(alertWrapper);
                    alerts.addFirst(alertWrapper);
                }

                // Tell the alert update thread to stop waiting and poll.
                waitHandle.release();
            });
        }

        // Tell the alert update thread to stop waiting and poll.
        waitHandle.release();

        // Notify subscribers that something was added to the list.
        ArrayList<AlertListUpdatedEvent> subs;
        synchronized (alertsListChangedSubscribers) {
            subs = new ArrayList<>(alertsListChangedSubscribers);
        }

        for (var event : subs) {
            event.onAlertListUpdated(true, alert);
        }
    }

    /**
     * Inserts an alert sequentially in the {@link #alerts} list such that the alert before it is <= the
     * time it should be checked and the alert after it is >= the time it should be checked.
     *
     * @param alert The alert.
     */
    private void insertAlertSequentially(AlertUpdateTracker alert) {
        synchronized (alerts) {
            for (int i = 0; i < alerts.size(); i++) {
                if (alerts.get(i).nextUpdateTime <= alert.nextUpdateTime) {
                    alerts.add(i, alert);
                    return;
                }
            }

            alerts.addLast(alert);
        }
    }

    /**
     * Removes an alert from the manager. It will be disposed of automatically.
     *
     * @param alert The alert.
     */
    public void removeAlert(IAlert alert) {
        AlertUpdateTracker foundAlert;
        synchronized (alerts) {
            foundAlert = alerts.stream().filter(a -> a.alert == alert).findFirst().orElse(null);
        }

        if (null == foundAlert) {
            return;
        }

        removeAlert(foundAlert);
    }

    /**
     * Removes an alert from the manager. It will be disposed of automatically.
     *
     * @param id The unique identifier of the alert. {@link IAlert#getId()}
     */
    public void removeAlert(int id) {
        AlertUpdateTracker foundAlert;
        synchronized (alerts) {
            foundAlert = alerts.stream().filter(a -> a.alert.getId() == id).findFirst().orElse(null);
        }

        if (null == foundAlert) {
            return;
        }

        removeAlert(foundAlert);
    }

    /**
     * Removes an alert from the manager. It will be disposed of automatically.
     *
     * @param alert The alert to remove.
     */
    private void removeAlert(AlertUpdateTracker alert) {
        // Tell the other thread we want to remove something.
        synchronized (alertsToRemove) {
            alertsToRemove.add(alert);
        }

        // Signal to the thread in case it's on a long wait.
        waitHandle.release();

        // Tell the subscribers about the removal.
        ArrayList<AlertListUpdatedEvent> subs;
        synchronized (alertsListChangedSubscribers) {
            subs = new ArrayList<>(alertsListChangedSubscribers);
        }

        for (var event : subs) {
            event.onAlertListUpdated(false, alert.alert);
        }
    }

    /**
     * Gets the collection of all alerts.
     *
     * @return The collection of all alerts.
     */
    public IAlert[] getAlerts() {
        synchronized (alerts) {
            // There may be alerts that haven't been removed yet but will be soon. We won't want to
            // return those since they shouldn't exist.
            synchronized (alertsToRemove) {
                return alerts.stream().filter(alert -> !alertsToRemove.contains(alert))
                        .map(alert -> alert.alert)
                        .toArray(IAlert[]::new);
            }
        }
    }

    /**
     * Adds a listener for when an IAlert is added or removed from the manager.
     *
     * @param listener The listener.
     */
    public void addAlertsUpdatedListener(AlertListUpdatedEvent listener) {
        synchronized (alertsListChangedSubscribers) {
            alertsListChangedSubscribers.add(listener);
        }
    }

    /**
     * Remove a listener for when an IAlert is added or removed from the manager.
     *
     * @param listener The listener.
     */
    public void removeAlertsUpdatedListener(AlertListUpdatedEvent listener) {
        synchronized (alertsListChangedSubscribers) {
            alertsListChangedSubscribers.remove(listener);
        }
    }

    /**
     * Disposes of managed and unmanaged resources.
     */
    public void dispose() {
        // Poison the thread.
        poisonPill = true;
        waitHandle.release();

        // Wait for the thread to exit
        try {
            alertRunnerThread.join(30000);

            // If it doesn't exit, kill it.
            if (alertRunnerThread.isAlive()) {
                alertRunnerThread.interrupt();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * A helper class for storing a pair of an alert and when it should be updated next.
     */
    public class AlertUpdateTracker {
        /**
         * The alert.
         */
        public IAlert alert;
        /**
         * The next time the alert should be updated in system milliseconds.
         */
        public long nextUpdateTime;

        /**
         * Instantiates a new instance of the class.
         *
         * @param alert          The alert.
         * @param nextUpdateTime The next time the alert should be updated in system milliseconds.
         */
        public AlertUpdateTracker(IAlert alert, long nextUpdateTime) {
            this.alert = alert;
            this.nextUpdateTime = nextUpdateTime;
        }
    }
}
