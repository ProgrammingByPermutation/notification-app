module org.nullinside {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires pircbotx;
    requires javafx.media;

    opens org.nullinside.notification_app to javafx.fxml;
    opens org.nullinside.notification_app.controllers to javafx.fxml;
    opens org.nullinside.notification_app.alerts to javafx.fxml;
    exports org.nullinside.notification_app;
}