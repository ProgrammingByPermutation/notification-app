package org.nullinside.notification_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.nullinside.notification_app.App;
import org.nullinside.notification_app.Configuration;

public class SettingsController {
    private final Configuration configuration;
    @FXML
    private PasswordField twitchClientSecret;
    @FXML
    private TextField twitchUsername;
    @FXML
    private PasswordField twitchUserOAuth;
    @FXML
    private TextField twitchChannel;
    @FXML
    private TextField twitchMessageNotificationSound;
    @FXML
    private PasswordField twitchClientId;

    public SettingsController() {
        configuration = Configuration.getConfiguration();
    }

    public void initialize() {
        twitchClientId.setText(configuration.TWITCH_CLIENT_ID);
        twitchClientSecret.setText(configuration.TWITCH_CLIENT_SECRET);
        twitchUsername.setText(configuration.TWITCH_USERNAME);
        twitchUserOAuth.setText(configuration.TWITCH_USER_OAUTH);
        twitchChannel.setText(configuration.TWITCH_CHANNEL);
        twitchMessageNotificationSound.setText(configuration.TWITCH_MESSAGE_NOTIFICATION_SOUND);
    }

    @FXML
    private void saveConfiguration() {
        configuration.TWITCH_CLIENT_ID = twitchClientId.getText().strip();
        configuration.TWITCH_CLIENT_SECRET = twitchClientSecret.getText().strip();
        configuration.TWITCH_USERNAME = twitchUsername.getText().strip();
        configuration.TWITCH_USER_OAUTH = twitchUserOAuth.getText().strip();
        configuration.TWITCH_CHANNEL = twitchChannel.getText().strip();
        configuration.TWITCH_MESSAGE_NOTIFICATION_SOUND = twitchMessageNotificationSound.getText().strip();
        configuration.writeConfiguration();
        this.cancelConfiguration();
    }

    @FXML
    private void cancelConfiguration() {
        App.setRoot("controllers/app");
    }
}
