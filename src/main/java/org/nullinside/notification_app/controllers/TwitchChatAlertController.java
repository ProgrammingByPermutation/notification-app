package org.nullinside.notification_app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.util.Pair;
import org.nullinside.notification_app.config.TwitchChatAlertConfig;

import java.util.ArrayList;

public class TwitchChatAlertController extends AbstractBaseController {
    public PasswordField cClientId;
    public PasswordField cClientSecret;
    public TextField cUsername;
    public PasswordField cOauth;
    public TextField cChannel;
    public TextField cAlertSoundFilename;
    public TwitchChatAlertConfig config;

    public TwitchChatAlertController() {
        config = new TwitchChatAlertConfig();
    }

    public TwitchChatAlertController(TwitchChatAlertConfig config) {
        this.config = config;
    }

    @Override
    protected ArrayList<Pair<TextInputControl, String>> getFieldToPropertyMapping() {
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

    @FXML
    public void initialize() {
        updateControlsWithProperties();
    }

    @FXML
    public void test() {
        System.out.println(config.clientId);
        System.out.println(config.clientSecret);
        System.out.println(config.username);
        System.out.println(config.oauth);
        System.out.println(config.channel);
        System.out.println(config.alertSoundFilename);
    }
}
