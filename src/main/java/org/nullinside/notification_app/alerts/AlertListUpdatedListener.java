package org.nullinside.notification_app.alerts;

import org.nullinside.notification_app.alerts.IAlert;

public interface AlertListUpdatedListener {
    void onAlertListUpdated(boolean added, IAlert alert);
}
