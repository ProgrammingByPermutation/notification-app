package org.nullinside.notification_app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.nullinside.notification_app.alerts.IAlert;

import java.io.IOException;

/**
 * The main entrypoint of the JavaFX GUI.
 */
public class App extends Application {
    private final static AlertsManager alertsManager = new AlertsManager();
    private static Scene scene;

    public static void addAlert(IAlert alert) {
        alertsManager.addAlert(alert);

    }

    public static void removeAlert(int id) {
        alertsManager.removeAlert(id);
    }

    public static void setRoot(String fxml) {
        try {
            scene.setRoot(loadFXML(fxml).getValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setRoot(Parent node) {
        scene.setRoot(node);
    }

    public static Pair<Object, Parent> loadFXML(String fxml) throws IOException {
        var fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent parent = fxmlLoader.load();
        var controller = fxmlLoader.getController();
        return new Pair<>(controller, parent);
    }

    public static Pair<Object, Parent> loadFXML(String fxml, Object controller) throws IOException {
        var fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        fxmlLoader.setController(controller);
        Parent parent = fxmlLoader.load();
        return new Pair<>(controller, parent);
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("controllers/app").getValue(), 640, 480);
        stage.setScene(scene);
        stage.getIcons().add(new Image(App.class.getResourceAsStream("application.png")));
        stage.show();
    }

    @Override
    public void stop() {
        alertsManager.dispose();
    }
}