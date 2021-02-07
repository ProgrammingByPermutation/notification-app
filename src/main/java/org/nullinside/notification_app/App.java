package org.nullinside.notification_app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.nullinside.notification_app.alerts.IAlert;
import org.nullinside.utilities.ReflectionUtilities;

import java.io.IOException;

/**
 * The main entrypoint of the JavaFX GUI.
 */
public class App extends Application {
    private static Scene scene;

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

    public static void saveConfigurations() {
        var manager = AlertsManager.getInstance();
        var config = Configuration.getInstance();
        config.savedConfigs.clear();

        for (var alert : manager.getAlerts()) {
            config.savedConfigs.add(new AlertConfiguration(alert));
        }

        config.writeConfiguration();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        loadConfiguration();
        scene = new Scene(loadFXML(Configuration.MAIN_GUI).getValue(), 640, 480);
        stage.setScene(scene);
        stage.getIcons().add(new Image(App.class.getResourceAsStream("application.png")));
        stage.show();
    }

    @Override
    public void stop() {
        var manager = AlertsManager.getInstance();
        manager.dispose();
    }

    private void loadConfiguration() {
        var manager = AlertsManager.getInstance();
        var config = Configuration.getInstance();
        for (var alertConfig : config.savedConfigs) {
            IAlert alert = (IAlert) ReflectionUtilities.createInstance(alertConfig.className);
            if (null == alert) {
                continue;
            }

            alert.setConfig(alertConfig.config);
            manager.addAlert(alert);
        }
    }
}