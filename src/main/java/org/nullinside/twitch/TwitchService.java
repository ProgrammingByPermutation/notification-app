package org.nullinside.twitch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TwitchService {
    public final String TWITCH_OAUTH_TOKEN_URL = "https://id.twitch.tv/oauth2/token?client_id=%s&client_secret=%s&grant_type=client_credentials";
    public final String TWITCH_CLIENT_ID;
    public final String TWITCH_CLIENT_SECRET;
    public final String TWITCH_USERNAME;
    public final String TWITCH_USER_OAUTH_TOKEN;
    public final String TWITCH_CHANNEL;
    private final ArrayList<TwitchChatListener> chatListener = new ArrayList<>();

    public TwitchService(String clientId, String clientSecret, String username, String userOAuthToken, String channel) {
        TWITCH_CLIENT_ID = clientId;
        TWITCH_CLIENT_SECRET = clientSecret;
        TWITCH_USERNAME = username;
        TWITCH_USER_OAUTH_TOKEN = userOAuthToken;
        TWITCH_CHANNEL = channel;
    }

    public String getAccessToken() {
        try {
            URL url = new URL(String.format(TWITCH_OAUTH_TOKEN_URL, this.TWITCH_CLIENT_ID, this.TWITCH_CLIENT_SECRET));
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

    public boolean connectToChat() {
        var chat = new TwitchChatListener(this.TWITCH_USERNAME, this.TWITCH_USER_OAUTH_TOKEN, this.TWITCH_CHANNEL);
        chatListener.add(chat);
        return chat.connect();
    }

    public boolean disconnectChats() {
        chatListener.forEach(TwitchChatListener::disconnect);
        chatListener.clear();
        return true;
    }

    private Map<String, String> connectionOutputToMap(HttpURLConnection conn) {
        ObjectMapper objectMapper = new ObjectMapper();
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
