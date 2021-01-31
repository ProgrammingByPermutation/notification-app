package org.nullinside.notification_app.alerts;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.nullinside.notification_app.App;
import org.nullinside.notification_app.Configuration;
import org.nullinside.twitch.TwitchService;

import java.io.IOException;

public class TwitchChatAlert extends AbstractAlert {
    private final String TITLE = "Twitch Chat Alerts";
    private Scene scene;
    private TwitchService twitch;
    private String clientId;
    private String clientSecret;
    private String username;
    private String oauth;
    private String channel;

    public TwitchChatAlert() {
        var config = Configuration.getConfiguration();
        this.clientId = config.TWITCH_CLIENT_ID;
        this.clientSecret = config.TWITCH_CLIENT_SECRET;
        this.username = config.TWITCH_USERNAME;
        this.oauth = config.TWITCH_USER_OAUTH;
        this.channel = config.TWITCH_CHANNEL;
    }

    @Override
    public void setIsEnabled(boolean isEnabled) {
        if (!isEnabled && this.getIsEnabled() && null != this.twitch) {
            this.twitch.disconnectChats();
        }

        super.setIsEnabled(isEnabled);
    }

    @Override
    public void check() {
        if (null != this.twitch) {
            return;
        }

        this.twitch = new TwitchService(this.clientId, this.clientSecret, this.username, this.oauth, this.channel);
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
        var title = new Label(this.TITLE);
        var channel = new Label(this.channel);
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
