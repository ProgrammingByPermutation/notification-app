package org.nullinside.notification_app.alerts;

import org.nullinside.notification_app.config.Configuration;
import org.nullinside.notification_app.config.AbstractAlertConfig;

public abstract class AbstractAlert implements IAlert {
    private int id;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean getIsEnabled() {
        var config = getConfigObject();
        if (null == config) {
            return false;
        }

        return config.isEnabled;
    }

    @Override
    public void setIsEnabled(boolean isEnabled) {
        var config = getConfigObject();
        if (null == config) {
            return;
        }

        config.isEnabled = isEnabled;
        Configuration.getInstance().updateSavedConfigurations();
    }

    @Override
    public long getUpdateInterval() {
        var config = getConfigObject();
        if (null == config) {
            return -1;
        }

        return config.updateInterval;
    }

    @Override
    public void setUpdateInterval(long updateInterval) {
        var config = getConfigObject();
        if (null == config) {
            return;
        }

        config.updateInterval = updateInterval;
        Configuration.getInstance().updateSavedConfigurations();
    }

    protected abstract AbstractAlertConfig getConfigObject();
}
