package org.nullinside.notification_app.alerts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.nullinside.notification_app.App;
import org.nullinside.notification_app.config.AbstractAlertConfig;
import org.nullinside.notification_app.config.GlobalConfig;
import org.nullinside.notification_app.config.TwitchChatAlertConfig;
import org.nullinside.notification_app.controllers.TwitchChatAlertController;
import org.nullinside.twitch.TwitchService;

import java.io.IOException;

/**
 * An alert that monitors twitch chat. It notifies the user when someone types in chat.
 */
public class TwitchChatAlert extends AbstractAlert {
    /**
     * The title to display in the preview panel of the UI.
     */
    private final String TITLE = "Twitch Chat Alerts";
    /**
     * The GUI's controller that handles updating the configuration of the alert.
     */
    private final TwitchChatAlertController controller;
    /**
     * The twitch service responsible for interacting with the twitch website and it's APIs.
     */
    private TwitchService twitch;

    /**
     * Instantiates a new instance of the class.
     */
    public TwitchChatAlert() {
        controller = new TwitchChatAlertController();

        // Get the global configuration settings and store them in our object.
        var config = GlobalConfig.getInstance();

        controller.config = null != config.twitchChatAlertGlobalConfig ? config.twitchChatAlertGlobalConfig.clone() :
                new TwitchChatAlertConfig();

        controller.addClosedListener(saved -> {
            if (saved) {
                GlobalConfig.getInstance().updateSavedConfigurations();
            }
        });
    }

    /**
     * Sets a flag indicating whether the alert is enabled.
     *
     * @param isEnabled True if enabled, false otherwise.
     */
    @Override
    public void setIsEnabled(boolean isEnabled) {
        if (!isEnabled && null != twitch) {
            twitch.disconnectChats();
            twitch = null;
        }

        super.setIsEnabled(isEnabled);
    }

    /**
     * Gets the alert configuration.
     *
     * @return The alert configuration object.
     */
    @Override
    protected AbstractAlertConfig getConfigObject() {
        return controller.config;
    }

    /**
     * Executes the logic of alert. This includes identifying if an alert condition
     * exists and providing some kind of indication to the user that it happened.
     */
    @Override
    public void check() {
        if (getIsEnabled() && null != twitch) {
            return;
        }

        twitch = new TwitchService(controller.config.clientId, controller.config.clientSecret,
                controller.config.username, controller.config.oauth, controller.config.channel,
                controller.config.alertSoundFilename);
        twitch.connectToChat();
    }

    /**
     * Gets the window GUI to display when configuring the alert.
     *
     * @return The window GUI to display when configuring the alert.
     */
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

    /**
     * Sets the preview GUI to display in the list of all alerts.
     * <p>
     * The parent row that houses a preview of this Alert's configuration is
     * passed in and the alert is given an opportunity to add any UI elements
     * it would like.
     *
     * @param parent The parent that houses the alert in the alert list.
     */
    @Override
    public void setPreviewRow(VBox parent) {
        var title = new Label(TITLE);
        var channel = new Label(controller.config.channel);
        parent.getChildren().addAll(title, channel);
    }

    /**
     * Gets the alert configuration in JSON string format.
     *
     * @return The alert configuration in JSON string format.
     */
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

    /**
     * Sets the alert configuration in JSON string format.
     *
     * @param config The alert configuration in JSON string format.
     */
    @Override
    public void setConfig(String config) {
        var objectMapper = new ObjectMapper();
        try {
            controller.config = objectMapper.readValue(config, TwitchChatAlertConfig.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Dispose of managed and unmanaged resources used by the alert.
     */
    @Override
    public void dispose() {
        if (null != twitch) {
            twitch.disconnectChats();
            twitch = null;
        }
    }
}
