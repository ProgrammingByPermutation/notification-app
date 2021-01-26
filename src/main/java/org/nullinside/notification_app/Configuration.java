package org.nullinside.notification_app;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Configuration {
    public String TWITCH_CLIENT_ID = "";
    public String TWITCH_CLIENT_SECRET = "";
    public String TWITCH_USERNAME = "";
    /**
     * https://twitchapps.com/tmi/
     */
    public String TWITCH_USER_OAUTH = "";
    public String TWITCH_CHANNEL = "";
    public String TWITCH_MESSAGE_NOTIFICATION_SOUND = "";

    public static String getConfigurationFilename() {
        String filename = System.getProperty("user.home");
        return filename + "/AppData/Roaming/nullinside/notification-app/config.json";
    }

    public static Configuration getConfiguration() {
        String filename = Configuration.getConfigurationFilename();

        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(filename);

        if (file.exists()) {
            try {
                return objectMapper.readValue(file, Configuration.class);
            } catch (JsonMappingException e) {
                System.out.println("Failed to read the configuration file due to a mismatch " +
                        "between source code and file");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Configuration configuration = new Configuration();
        if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {
            try {
                objectMapper.writeValue(file, configuration);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return configuration;
    }
}
