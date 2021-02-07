package org.nullinside.notification_app;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nullinside.notification_app.config.TwitchChatAlertConfig;

import java.io.File;
import java.io.IOException;

public class Configuration {
    // TODO: Make singleton
    // TODO: Make subscribable

    public final static String MAIN_GUI = "controllers/app";
    public TwitchChatAlertConfig twitchChatAlertGlobalConfig = new TwitchChatAlertConfig();

    public static String getConfigurationFilename() {
        String filename = System.getProperty("user.home");
        return filename + "/AppData/Roaming/nullinside/notification-app/config.json";
    }

    public static Configuration getConfiguration() {
        String filename = Configuration.getConfigurationFilename();

        var objectMapper = new ObjectMapper();
        var file = new File(filename);

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

        var configuration = new Configuration();
        if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {
            try {
                objectMapper.writeValue(file, configuration);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return configuration;
    }

    public boolean writeConfiguration() {
        String filename = Configuration.getConfigurationFilename();

        var objectMapper = new ObjectMapper();
        var file = new File(filename);

        if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {
            try {
                objectMapper.writeValue(file, this);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }
}
