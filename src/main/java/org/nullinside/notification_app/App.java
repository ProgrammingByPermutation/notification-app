package org.nullinside.notification_app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.nullinside.twitch.TwitchService;

import java.io.IOException;

/**
 * The main entrypoint of the JavaFX GUI.
 */
public class App extends Application {
    private static Scene scene;
    private TwitchService service;

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.getIcons().add(new Image(App.class.getResourceAsStream("application.png")));
        stage.show();

        // Read the configuration if it exists
        Configuration config = Configuration.getConfiguration();
        service = new TwitchService(config.TWITCH_CLIENT_ID, config.TWITCH_CLIENT_SECRET, config.TWITCH_USERNAME, config.TWITCH_USER_OAUTH, config.TWITCH_CHANNEL);
        service.connectToChat();
    }

    @Override
    public void stop() {
        service.disconnectChats();
    }
}