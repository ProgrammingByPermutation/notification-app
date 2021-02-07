module org.nullinside {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires pircbotx;
    requires javafx.media;

    opens org.nullinside.notification_app to javafx.fxml;
    opens org.nullinside.notification_app.controllers to javafx.fxml;
    opens org.nullinside.notification_app.alerts to javafx.fxml;
    opens org.nullinside.notification_app.controls to javafx.fxml;
    exports org.nullinside.notification_app;
    exports org.nullinside.notification_app.alerts;
    exports org.nullinside.notification_app.controllers;
    exports org.nullinside.notification_app.config;
    exports org.nullinside.utilities;
}