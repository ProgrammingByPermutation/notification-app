package org.nullinside.notification_app.alerts;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;

public interface IAlert {
    // TODO: Add interval

    int getId();

    void setId(int id);

    boolean getIsEnabled();

    void setIsEnabled(boolean isEnabled);

    void check();

    Scene getGui();

    void setPreviewGui(VBox parent);

    void dispose();
}
