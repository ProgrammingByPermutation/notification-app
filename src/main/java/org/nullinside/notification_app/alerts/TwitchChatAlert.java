package org.nullinside.notification_app.alerts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.nullinside.notification_app.App;
import org.nullinside.notification_app.Configuration;
import org.nullinside.notification_app.config.AbstractAlertConfig;
import org.nullinside.notification_app.config.TwitchChatAlertConfig;
import org.nullinside.notification_app.controllers.TwitchChatAlertController;
import org.nullinside.twitch.TwitchService;

import java.io.IOException;

public class TwitchChatAlert extends AbstractAlert {
    private final String TITLE = "Twitch Chat Alerts";
    private TwitchChatAlertController controller;
    private TwitchService twitch;

    public TwitchChatAlert() {
        controller = new TwitchChatAlertController();
        var config = Configuration.getInstance();
        controller.config.clientId = config.twitchChatAlertGlobalConfig.clientId;
        controller.config.clientSecret = config.twitchChatAlertGlobalConfig.clientSecret;
        controller.config.username = config.twitchChatAlertGlobalConfig.username;
        controller.config.oauth = config.twitchChatAlertGlobalConfig.oauth;
        controller.config.channel = config.twitchChatAlertGlobalConfig.channel;
        controller.config.alertSoundFilename = config.twitchChatAlertGlobalConfig.alertSoundFilename;
        controller.addClosedListener(saved -> {
            if (saved) {
                Configuration.getInstance().updateSavedConfigurations();
            }
        });
    }

    @Override
    public void setIsEnabled(boolean isEnabled) {
        if (!isEnabled && getIsEnabled() && null != twitch) {
            twitch.disconnectChats();
        }

        super.setIsEnabled(isEnabled);
    }

    @Override
    protected AbstractAlertConfig getConfigObject() {
        return controller.config;
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
        try {
            var pair = App.loadFXML("controllers/twitchChatAlert", controller);
            return pair.getValue();
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
    public String getConfig() {
        var objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(controller.config);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setConfig(String config) {
        var objectMapper = new ObjectMapper();
        try {
            controller.config = objectMapper.readValue(config, TwitchChatAlertConfig.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        if (null != twitch) {
            twitch.disconnectChats();
            twitch = null;
        }
    }
}
