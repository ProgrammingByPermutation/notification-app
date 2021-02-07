package org.nullinside.notification_app.alerts;

import javafx.scene.Parent;
import javafx.scene.layout.VBox;

public interface IAlert {
    // TODO: Add interval

    int getId();

    void setId(int id);

    boolean getIsEnabled();

    void setIsEnabled(boolean isEnabled);

    long getUpdateInterval();

    void setUpdateInterval(long updateInterval);

    String getConfig();

    void setConfig(String config);

    Parent getGui();

    void setPreviewGui(VBox parent);

    void check();

    void dispose();
}
