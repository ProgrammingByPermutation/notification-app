module org.nullinside {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.nullinside to javafx.fxml;
    exports org.nullinside;
}