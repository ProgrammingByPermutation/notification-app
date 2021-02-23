package org.nullinside.notification_app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.nullinside.notification_app.alerts.AlertsManager;
import org.nullinside.notification_app.config.GlobalConfig;

import java.io.IOException;

/**
 * The main entrypoint of the JavaFX GUI.
 */
public class App extends Application {
    /**
     * The scene of the main GUI passed to us from JavaFX.
     */
    private static Scene scene;

    /**
     * Sets the scene's root control.
     *
     * @param fxml The path to the FXML file in the 'resources' folder.
     */
    public static void setRoot(String fxml) {
        try {
            scene.setRoot(loadFXML(fxml).getValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the scene's root control.
     *
     * @param node The instance of the root control.
     */
    public static void setRoot(Parent node) {
        scene.setRoot(node);
    }

    /**
     * Loads the control from the FXML file in the 'resources' folder.
     *
     * @param fxml The path to the FXML file in the 'resources' folder.
     * @return A pair mapping the controller to the GUI control.
     * @throws IOException Failed to read the FXML file.
     */
    public static Pair<Object, Parent> loadFXML(String fxml) throws IOException {
        var fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent parent = fxmlLoader.load();
        var controller = fxmlLoader.getController();
        return new Pair<>(controller, parent);
    }

    /**
     * Loads the control from the FXML file in the 'resources' folder.
     *
     * @param fxml       The path to the FXML file in the 'resources' folder.
     * @param controller The controller object to load manually into the control.
     * @return A pair mapping the controller to the GUI control.
     * @throws IOException Failed to read the FXML file.
     */
    public static Pair<Object, Parent> loadFXML(String fxml, Object controller) throws IOException {
        var fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        fxmlLoader.setController(controller);
        Parent parent = fxmlLoader.load();
        return new Pair<>(controller, parent);
    }

    /**
     * The entry point of application.
     *
     * @param args The application arguments.
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * The method called by JavaFX when the application starts.
     *
     * @param stage The main GUI passed to us from JavaFX.
     * @throws IOException Failed to read the FXML file of the main GUI.
     */
    @Override
    public void start(Stage stage) throws IOException {
        GlobalConfig.getInstance().initialize();
        scene = new Scene(loadFXML(GlobalConfig.MAIN_GUI).getValue(), 640, 480);
        stage.setScene(scene);
        stage.getIcons().add(new Image(App.class.getResourceAsStream("application.png")));
        stage.show();
    }

    /**
     * The method called by JavaFX when the application is exiting.
     */
    @Override
    public void stop() {
        var manager = AlertsManager.getInstance();
        manager.dispose();

        var config = GlobalConfig.getInstance();
        config.writeConfiguration();
    }
}