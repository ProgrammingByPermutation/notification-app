package org.nullinside.notification_app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.nullinside.notification_app.alerts.IAlert;
import org.nullinside.twitch.TwitchService;

import java.io.IOException;

/**
 * The main entrypoint of the JavaFX GUI.
 */
public class App extends Application {
    private final static AlertsManager alertsManager = new AlertsManager();
    private static Scene scene;
    private TwitchService service;

    public static void addAlert(IAlert alert) {
        alertsManager.addAlert(alert);

    }

    public static void removeAlert(int id) {
        alertsManager.removeAlert(id);
    }

    public static void setRoot(String fxml) {
        try {
            scene.setRoot(loadFXML(fxml));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Parent loadFXML(String fxml) throws IOException {
        var fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("controllers/app"), 640, 480);
        stage.setScene(scene);
        stage.getIcons().add(new Image(App.class.getResourceAsStream("application.png")));
        stage.show();
    }

    @Override
    public void stop() {
        service.disconnectChats();
    }
}