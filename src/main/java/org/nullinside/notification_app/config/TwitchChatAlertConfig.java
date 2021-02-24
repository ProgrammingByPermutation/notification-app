package org.nullinside.notification_app.config;

/**
 * The configuration for a Twitch Chat alert.
 */
public class TwitchChatAlertConfig extends AbstractAlertConfig {
    /**
     * The default update interval for the alert.
     */
    private static final long DEFAULT_UPDATE_INTERVAL = 30000;
    /**
     * The client id from the Twitch API.
     */
    public final String clientId;
    /**
     * The client secret from the Twitch API.
     */
    public final String clientSecret;
    /**
     * The username of user we'll log in as.
     */
    public final String username;
    /**
     * The OAuth token of the user we'll log in as. (https://twitchapps.com/tmi)
     */
    public final String oauth;
    /**
     * The channel to monitor.
     */
    public final String channel;
    /**
     * The path to the alert sound to play when someone types in chat.
     */
    public final String alertSoundFilename;

    /**
     * Instantiates a new instance of the class.
     */
    public TwitchChatAlertConfig() {
        setUpdateInterval(DEFAULT_UPDATE_INTERVAL);
        clientId = null;
        clientSecret = null;
        username = null;
        oauth = null;
        channel = null;
        alertSoundFilename = null;
    }

    /**
     * Instantiates a new instance of the class.
     *
     * @param clientId           The client id from the Twitch API.
     * @param clientSecret       The client secret from the Twitch API.
     * @param username           The username of user we'll log in as.
     * @param oauth              The OAuth token of the user we'll log in as.
     * @param channel            The channel to monitor.
     * @param alertSoundFilename The path to the alert sound to play when someone types in chat.
     */
    public TwitchChatAlertConfig(String clientId, String clientSecret, String username,
                                 String oauth, String channel, String alertSoundFilename) {
        setUpdateInterval(DEFAULT_UPDATE_INTERVAL);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.username = username;
        this.oauth = oauth;
        this.channel = channel;
        this.alertSoundFilename = alertSoundFilename;
    }

    /**
     * Creates a clone of the configuration.
     *
     * @return A new instance of the configuration.
     */
    public TwitchChatAlertConfig clone() {
        return new TwitchChatAlertConfig(clientId, clientSecret, username, oauth, channel, alertSoundFilename);
    }
}
