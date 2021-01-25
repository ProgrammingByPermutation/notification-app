module org.nullinside {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires pircbotx;

    opens org.nullinside.notification_app to javafx.fxml;
    exports org.nullinside.notification_app;
}