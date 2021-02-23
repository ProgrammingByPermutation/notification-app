package org.nullinside.twitch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The Twitch API.
 */
public class TwitchService {
    /**
     * The link to get an OAuth token from Twitch.
     */
    public final String TWITCH_OAUTH_TOKEN_URL = "https://id.twitch.tv/oauth2/token?client_id=%s&client_secret=%s&grant_type=client_credentials";
    /**
     * The client id from the Twitch API.
     */
    public final String TWITCH_CLIENT_ID;
    /**
     * The client secret from the Twitch API.
     */
    public final String TWITCH_CLIENT_SECRET;
    /**
     * The username of user we'll log in as.
     */
    public final String TWITCH_USERNAME;
    /**
     * The OAuth token of the user we'll log in as. (https://twitchapps.com/tmi)
     */
    public final String TWITCH_USER_OAUTH_TOKEN;
    /**
     * The channel to monitor.
     */
    public final String TWITCH_CHANNEL;
    /**
     * The Twitch chat listener objects.
     */
    private final ArrayList<TwitchChatListener> chatListener = new ArrayList<>();

    /**
     * Instantiates a new instance of the class.
     *
     * @param clientId       The client id from the Twitch API.
     * @param clientSecret   The client secret from the Twitch API.
     * @param username       The username of user we'll log in as.
     * @param userOAuthToken The OAuth token of the user we'll log in as. (https://twitchapps.com/tmi)
     * @param channel        The channel to monitor.
     */
    public TwitchService(String clientId, String clientSecret, String username, String userOAuthToken, String channel) {
        TWITCH_CLIENT_ID = clientId;
        TWITCH_CLIENT_SECRET = clientSecret;
        TWITCH_USERNAME = username;
        TWITCH_USER_OAUTH_TOKEN = userOAuthToken;
        TWITCH_CHANNEL = channel;
    }

    /**
     * Gets an application access token.
     *
     * @return The access token.
     */
    public String getAccessToken() {
        try {
            var url = new URL(String.format(TWITCH_OAUTH_TOKEN_URL, TWITCH_CLIENT_ID, TWITCH_CLIENT_SECRET));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            Map<String, String> output = connectionOutputToMap(conn);
            if (null != output) {
                return output.get("access_token");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Connects to Twitch chat.
     *
     * @return True if successful, false otherwise.
     */
    public boolean connectToChat() {
        var chat = new TwitchChatListener(TWITCH_USERNAME, TWITCH_USER_OAUTH_TOKEN, TWITCH_CHANNEL, true);
        chatListener.add(chat);
        return chat.connect();
    }

    /**
     * Disconnects from Twitch chat.
     *
     * @return True if successful, false otherwise.
     */
    public boolean disconnectChats() {
        chatListener.forEach(TwitchChatListener::disconnect);
        chatListener.clear();
        return true;
    }

    /**
     * Converts the output of an HTTP connection.
     *
     * @param conn The HTTP connection.
     * @return The map representing the output.
     */
    private Map<String, String> connectionOutputToMap(HttpURLConnection conn) {
        var objectMapper = new ObjectMapper();
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<>() {
        };

        try {
            return objectMapper.readValue(conn.getInputStream(), typeRef);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
