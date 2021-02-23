package org.nullinside.notification_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.util.Pair;
import org.nullinside.notification_app.config.TwitchChatAlertConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * The controller mapping the {@link org.nullinside.notification_app.alerts.TwitchChatAlert} configuration
 * settings to it's GUI.
 */
public class TwitchChatAlertController extends AbstractBaseController {
    /**
     * The client id from the Twitch API.
     */
    public PasswordField cClientId;
    /**
     * The client secret from the Twitch API.
     */
    public PasswordField cClientSecret;
    /**
     * The username of user we'll log in as.
     */
    public TextField cUsername;
    /**
     * The OAuth token of the user we'll log in as.
     */
    public PasswordField cOauth;
    /**
     * The channel to monitor.
     */
    public TextField cChannel;
    /**
     * The path to the alert sound to play when someone types in chat.
     */
    public TextField cAlertSoundFilename;
    /**
     * The configuration object.
     */
    public TwitchChatAlertConfig config;

    /**
     * Instantiates a new instance of the class.
     */
    public TwitchChatAlertController() {
        config = new TwitchChatAlertConfig();
    }

    /**
     * Instantiates a new instance of the class.
     *
     * @param config The configuration object.
     */
    public TwitchChatAlertController(TwitchChatAlertConfig config) {
        this.config = config;
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
                add(new Pair<>(cClientId, "config.clientId"));
                add(new Pair<>(cClientSecret, "config.clientSecret"));
                add(new Pair<>(cUsername, "config.username"));
                add(new Pair<>(cOauth, "config.oauth"));
                add(new Pair<>(cChannel, "config.channel"));
                add(new Pair<>(cAlertSoundFilename, "config.alertSoundFilename"));
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
     * Saves the configuration and navigates to the main GUI.
     */
    @FXML
    private void save() {
        navigateToMainUI(true);
    }

    /**
     * Discards the configuration and navigates to the main GUI.
     */
    @FXML
    private void cancel() {
        navigateToMainUI(false);
    }
}
