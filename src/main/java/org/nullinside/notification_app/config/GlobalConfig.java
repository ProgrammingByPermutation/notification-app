package org.nullinside.notification_app.config;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nullinside.notification_app.alerts.AlertsManager;
import org.nullinside.notification_app.alerts.IAlert;
import org.nullinside.utilities.ReflectionUtilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The global configuration of the application.
 */
public class GlobalConfig {
    /**
     * The path to the FXML file of the main GUI.
     */
    public final static String MAIN_GUI = "controllers/app";
    /**
     * The singleton instance of this class.
     */
    private static GlobalConfig instance;
    /**
     * The collection of alert configurations.
     */
    public final ArrayList<AlertConfiguration> alertConfigs = new ArrayList<>();
    /**
     * The {@link org.nullinside.notification_app.alerts.TwitchChatAlert} global configuration
     * settings used for each new {@link org.nullinside.notification_app.alerts.TwitchChatAlert} alert.
     */
    public TwitchChatAlertConfig twitchChatAlertGlobalConfig;

    /**
     * Instantiates a new instance of the class.
     */
    private GlobalConfig() {
        twitchChatAlertGlobalConfig = new TwitchChatAlertConfig();
    }

    /**
     * Gets the singleton instance of this class.
     *
     * @return The singleton instance of this class.
     */
    public static GlobalConfig getInstance() {
        if (null == instance) {
            instance = getConfiguration();
        }

        return instance;
    }

    /**
     * Gets the filename of the configuration on disk.
     *
     * @return The configuration filename.
     */
    private static String getConfigurationFilename() {
        String filename = System.getProperty("user.home");
        return filename + "/AppData/Roaming/nullinside/notification-app/config.json";
    }

    /**
     * Gets a {@link GlobalConfig} object based on the configuration currently written to disk.
     *
     * @return The global configuration object according to what we have written to disk.
     */
    private static GlobalConfig getConfiguration() {
        // Create a file object based on where the config file should be saved.
        String filename = GlobalConfig.getConfigurationFilename();

        var objectMapper = new ObjectMapper();
        var file = new File(filename);

        // If the file exists, read it as JSON and return it.
        if (file.exists()) {
            try {
                return objectMapper.readValue(file, GlobalConfig.class);
            } catch (JsonMappingException e) {
                System.out.println("Failed to read the configuration file due to a mismatch " +
                        "between source code and file");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // If the file doesn't exist, create a brand new object and write it to disk.
        var configuration = new GlobalConfig();
        if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {
            try {
                objectMapper.writeValue(file, configuration);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return configuration;
    }

    /**
     * Handles updating the saved configurations whenever the list of alerts updates.
     *
     * @param added True if the alert was added, false if it was removed.
     * @param alert The alert.
     */
    private void onAlertListUpdated(boolean added, IAlert alert) {
        updateSavedConfigurations();
    }

    /**
     * Initializes the alerts manager at the beginning of the application.
     */
    public void initialize() {
        // First we need to get the alert manager and unsubscribe from it. If we don't do this,
        // we'll get updates for every single alert we add to it.
        var manager = AlertsManager.getInstance();
        manager.removeAlertsUpdatedListener(this::onAlertListUpdated);

        // Add all of the alerts from the configuration file.
        for (var alertConfig : alertConfigs) {
            IAlert alert = (IAlert) ReflectionUtilities.createInstance(alertConfig.className, null, null);
            if (null == alert) {
                continue;
            }

            alert.setConfig(alertConfig.config);
            manager.addAlert(alert);
        }

        // Re-subscribe to the alerts manager.
        manager.addAlertsUpdatedListener(this::onAlertListUpdated);
    }

    /**
     * Refreshes the list of configurations of the alerts from the {@link AlertsManager}.
     */
    public void updateSavedConfigurations() {
        var manager = AlertsManager.getInstance();

        // Clear what we have, it isn't accurate.
        alertConfigs.clear();

        // Re-create the list from the alert manager.
        for (var alert : manager.getAlerts()) {
            alertConfigs.add(new AlertConfiguration(alert));
        }

        // Write the configuration to disk.
        writeConfiguration();
    }

    /**
     * Writes the configuration to disk.
     *
     * @return True if successful, false otherwise.
     */
    public boolean writeConfiguration() {
        // Create a file object based on where the config file should be saved.
        String filename = GlobalConfig.getConfigurationFilename();

        var objectMapper = new ObjectMapper();
        var file = new File(filename);

        // If the parent directory doesn't exist create it.
        if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {
            try {
                // Write the file to disk.
                objectMapper.writeValue(file, this);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }
}
