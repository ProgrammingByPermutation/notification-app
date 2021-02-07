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
        twitchClientId.setText(configuration.twitchChatAlertGlobalConfig.clientId);
        twitchClientSecret.setText(configuration.twitchChatAlertGlobalConfig.clientSecret);
        twitchUsername.setText(configuration.twitchChatAlertGlobalConfig.username);
        twitchUserOAuth.setText(configuration.twitchChatAlertGlobalConfig.oauth);
        twitchChannel.setText(configuration.twitchChatAlertGlobalConfig.channel);
        twitchMessageNotificationSound.setText(configuration.twitchChatAlertGlobalConfig.alertSoundFilename);
    }

    @FXML
    private void saveConfiguration() {
        configuration.twitchChatAlertGlobalConfig.clientId = twitchClientId.getText().strip();
        configuration.twitchChatAlertGlobalConfig.clientSecret = twitchClientSecret.getText().strip();
        configuration.twitchChatAlertGlobalConfig.username = twitchUsername.getText().strip();
        configuration.twitchChatAlertGlobalConfig.oauth = twitchUserOAuth.getText().strip();
        configuration.twitchChatAlertGlobalConfig.channel = twitchChannel.getText().strip();
        configuration.twitchChatAlertGlobalConfig.alertSoundFilename = twitchMessageNotificationSound.getText().strip();
        configuration.writeConfiguration();
        cancelConfiguration();
    }

    @FXML
    private void cancelConfiguration() {
        App.setRoot("controllers/app");
    }
}
