package org.nullinside.notification_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.util.Pair;
import org.nullinside.notification_app.App;
import org.nullinside.notification_app.config.GlobalConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * The global settings controller.
 */
public class GlobalConfigController extends AbstractBaseController {
    /**
     * The global configuration.
     */
    private final GlobalConfig config;
    /**
     * The client secret from the Twitch API.
     */
    @FXML
    private PasswordField twitchClientSecret;
    /**
     * The username of user we'll log in as.
     */
    @FXML
    private TextField twitchUsername;
    /**
     * The OAuth token of the user we'll log in as.
     */
    @FXML
    private PasswordField twitchUserOAuth;
    /**
     * The channel to monitor.
     */
    @FXML
    private TextField twitchChannel;
    /**
     * The path to the alert sound to play when someone types in chat.
     */
    @FXML
    private TextField twitchMessageNotificationSound;
    /**
     * The client id from the Twitch API.
     */
    @FXML
    private PasswordField twitchClientId;

    /**
     * Instantiates a new instance of the class.
     */
    public GlobalConfigController() {
        config = GlobalConfig.getInstance();
    }

    /**
     * Maps controls to the fields they update.
     *
     * @return A collection of mappings of control to their property names in the class.
     */
    @Override
    protected List<Pair<TextInputControl, String>> getFieldToPropertyMapping() {
        return new ArrayList<>() {
            {
                add(new Pair<>(twitchClientId, "config.twitchChatAlertGlobalConfig.clientId"));
                add(new Pair<>(twitchClientSecret, "config.twitchChatAlertGlobalConfig.clientSecret"));
                add(new Pair<>(twitchUsername, "config.twitchChatAlertGlobalConfig.username"));
                add(new Pair<>(twitchUserOAuth, "config.twitchChatAlertGlobalConfig.oauth"));
                add(new Pair<>(twitchChannel, "config.twitchChatAlertGlobalConfig.channel"));
                add(new Pair<>(twitchMessageNotificationSound, "config.twitchChatAlertGlobalConfig.alertSoundFilename"));
            }
        };
    }

    /**
     * Initializes the JavaFX GUI.
     */
    @FXML
    public void initialize() {
        updateControlsWithProperties();
    }

    /**
     * Saves the configuration to disk and navigate to the main GUI.
     */
    @FXML
    private void saveConfiguration() {
        updatePropertiesWithControls();
        config.writeConfiguration();
        App.setRoot(GlobalConfig.MAIN_GUI);
    }

    /**
     * Discards the configuration and navigates to the main GUI.
     */
    @FXML
    private void cancelConfiguration() {
        App.setRoot(GlobalConfig.MAIN_GUI);
    }
}
