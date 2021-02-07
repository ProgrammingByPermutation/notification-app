package org.nullinside.notification_app.alerts;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.nullinside.notification_app.App;
import org.nullinside.notification_app.Configuration;
import org.nullinside.notification_app.controllers.TwitchChatAlertController;
import org.nullinside.twitch.TwitchService;

import java.io.IOException;

public class TwitchChatAlert extends AbstractAlert {
    private final String TITLE = "Twitch Chat Alerts";
    private Parent parent;
    private TwitchChatAlertController controller;
    private TwitchService twitch;

    public TwitchChatAlert() {
        controller = new TwitchChatAlertController();
        var config = Configuration.getConfiguration();
        controller.config.clientId = config.twitchChatAlertGlobalConfig.clientId;
        controller.config.clientSecret = config.twitchChatAlertGlobalConfig.clientSecret;
        controller.config.username = config.twitchChatAlertGlobalConfig.username;
        controller.config.oauth = config.twitchChatAlertGlobalConfig.oauth;
        controller.config.channel = config.twitchChatAlertGlobalConfig.channel;
        controller.config.alertSoundFilename = config.twitchChatAlertGlobalConfig.alertSoundFilename;
    }

    @Override
    public void setIsEnabled(boolean isEnabled) {
        if (!isEnabled && getIsEnabled() && null != twitch) {
            twitch.disconnectChats();
        }

        super.setIsEnabled(isEnabled);
    }

    @Override
    public void check() {
        if (null != twitch) {
            return;
        }

        twitch = new TwitchService(controller.config.clientId, controller.config.clientSecret,
                controller.config.username, controller.config.oauth, controller.config.channel);
        twitch.connectToChat();
    }

    @Override
    public Parent getGui() {
        if (null != parent) {
            return parent;
        }

        try {
            var pair = App.loadFXML("controllers/twitchChatAlert", controller);
            parent = pair.getValue();
            return parent;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setPreviewGui(VBox parent) {
        var title = new Label(TITLE);
        var channel = new Label(controller.config.channel);
        parent.getChildren().addAll(title, channel);
    }

    @Override
    public void dispose() {
        if (null != twitch) {
            twitch.disconnectChats();
            twitch = null;
        }
    }
}
