package org.nullinside.notification_app.alerts;

import org.nullinside.notification_app.Configuration;
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

    protected abstract AbstractAlertConfig getConfigObject();
}
