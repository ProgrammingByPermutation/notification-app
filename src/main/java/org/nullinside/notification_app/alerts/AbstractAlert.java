package org.nullinside.notification_app.alerts;

public abstract class AbstractAlert implements IAlert {
    private int id;
    private boolean isEnabled;

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean getIsEnabled() {
        return this.isEnabled;
    }

    @Override
    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}
