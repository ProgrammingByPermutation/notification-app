package org.nullinside.notification_app;

import org.nullinside.notification_app.alerts.IAlert;

public interface AlertListUpdatedListener {
    void onAlertListUpdated(boolean added, IAlert alert);
}
