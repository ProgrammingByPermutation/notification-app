package org.nullinside.notification_app.alerts;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.nullinside.notification_app.App;
import org.nullinside.notification_app.Configuration;
import org.nullinside.twitch.TwitchService;

import java.io.IOException;

public class TwitchChatAlert implements IAlert {
    private final String TITLE = "Twitch Chat Alerts";
    private boolean isEnabled;
    private Scene scene;
    private TwitchService twitch;
    private Configuration config;
    private String channel;

    @Override
    public boolean getIsEnabled() {
        return this.isEnabled;
    }

    @Override
    public void setIsEnabled(boolean isEnabled) {
        if (!isEnabled && this.isEnabled && null != this.twitch) {
            this.twitch.disconnectChats();
        }

        this.isEnabled = isEnabled;
    }

    @Override
    public void check() {
        if (null != this.twitch) {
            return;
        }

        this.twitch = new TwitchService(config.TWITCH_CLIENT_ID, config.TWITCH_CLIENT_SECRET, config.TWITCH_USERNAME,
                config.TWITCH_USER_OAUTH, config.TWITCH_CHANNEL);
        this.twitch.connectToChat();
    }

    @Override
    public Scene getGui() {
        if (null != scene) {
            return scene;
        }

        try {
            scene = new Scene(App.loadFXML("twitchChatAlert"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return scene;
    }

    @Override
    public void setPreviewGui(VBox parent) {
        Label title = new Label(this.TITLE);
        Label channel = new Label(this.channel);
        parent.getChildren().addAll(title, channel);
    }

    @Override
    public void dispose() {
        if (null != this.twitch) {
            this.twitch.disconnectChats();
            this.twitch = null;
        }
    }
}
