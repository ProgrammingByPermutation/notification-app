package org.nullinside.notification_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.util.Pair;
import org.nullinside.notification_app.App;
import org.nullinside.notification_app.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class SettingsController extends AbstractBaseController {
    private final Configuration config;
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
        config = Configuration.getInstance();
    }

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

    @FXML
    public void initialize() {
        updateControlsWithProperties();
    }

    @FXML
    private void saveConfiguration() {
        updatePropertiesWithControls();
        config.writeConfiguration();
        App.setRoot(Configuration.MAIN_GUI);
    }

    @FXML
    private void cancelConfiguration() {
        App.setRoot(Configuration.MAIN_GUI);
    }
}
