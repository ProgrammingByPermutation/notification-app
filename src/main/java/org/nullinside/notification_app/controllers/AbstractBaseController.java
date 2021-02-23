package org.nullinside.notification_app.controllers;

import javafx.scene.control.TextInputControl;
import javafx.util.Pair;
import org.nullinside.notification_app.App;
import org.nullinside.notification_app.config.GlobalConfig;
import org.nullinside.utilities.ReflectionUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An abstract controller for handling common operations between a GUI and a configuration.
 */
public abstract class AbstractBaseController {
    /**
     * A collection of listeners to call when the configuration GUI closes.
     */
    protected List<ControllerClosedEvent> closedListeners = new ArrayList<>();

    /**
     * Maps controls to the fields they update.
     *
     * @return A collection of mappings of control to their property names in the class.
     */
    protected List<Pair<TextInputControl, String>> getFieldToPropertyMapping() {
        return null;
    }

    /**
     * Updates the text of the controls defined in the {@link #getFieldToPropertyMapping()} method with
     * the properties they map to.
     */
    protected void updateControlsWithProperties() {
        var mapping = getFieldToPropertyMapping();
        if (null == mapping) {
            return;
        }

        for (var pair : mapping) {
            var control = pair.getKey();
            var propertyName = pair.getValue();

            var map = ReflectionUtilities.getFieldNested(getClass(), propertyName, this);
            if (null == map) {
                continue;
            }

            map.getValue().setAccessible(true);
            try {
                String value = (String) map.getValue().get(map.getKey());
                control.setText(value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates the String properties defined in the getFieldToPropertyMapping() method with
     * the text from the controls they map to.
     */
    protected void updatePropertiesWithControls() {
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // So here's the deal. The properties will always belong to a configuration object that is supposed         //
        // to be immutable. Java's reflection will NOT stop you from updating a "final" field; it's apparently only //
        // a compile time protection. So we have to do the job of constructing a new configuration object by hand   //
        // being very careful not to update the original object.                                                    //
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // Determine if there is a mapping.
        var mapping = getFieldToPropertyMapping();
        if (null == mapping || mapping.size() == 0) {
            return;
        }

        // Get the name of the configuration variable in the object. This may be in "parent.config.field" format or
        // the config might be at the root of the object in "config.field" format.
        var configName = mapping.get(0).getValue();
        if (configName.contains(".")) {
            configName = configName.substring(0, configName.lastIndexOf("."));
        }

        // Now we pull out the parent holding the configuration and we pull out the Field storing the configuration.
        var configPair = ReflectionUtilities.getFieldNested(getClass(), configName, this);
        if (null == configPair) {
            System.err.printf("Failed to map properties: %s", getClass().getName());
            return;
        }

        // Set it to accessible so we can get the config object from the parent.
        configPair.getValue().setAccessible(true);

        // Get the config object.
        Object configObject;
        try {
            configObject = configPair.getValue().get(configPair.getKey());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.err.printf("Failed to get config object: %s", getClass().getName());
            return;
        }

        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        // At this point we need to construct a brand new instance of the configuration. The configurations are //
        // supposed to be immutable so this is mandatory.                                                       //
        //////////////////////////////////////////////////////////////////////////////////////////////////////////

        // The mapping variables should be in the exact order we pass into the constructor. Convert the arguments
        // into an array.
        String[] args = mapping.stream().map(pair -> pair.getKey().getText()).toArray(String[]::new);

        // Convert the types of the arguments into an array. We only support strings at this time.
        Class<?>[] types = new Class[args.length];
        Arrays.fill(types, String.class);

        // Create a new instance of the configuration.
        var newConfig = ReflectionUtilities.createInstance(configObject.getClass().getName(), types, args);
        if (null == newConfig) {
            System.err.printf("%s: Failed to get clone: %s", getClass().getName(), configObject.getClass().getName());
            return;
        }

        // Swap the existing configuration with the new instance we constructed.
        try {
            configPair.getValue().set(configPair.getKey(), newConfig);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.err.printf("Failed to update with new config clone: %s", getClass().getName());
        }
    }

    /**
     * Adds a listener that is called when the configuration GUI closes.
     *
     * @param listener The listener.
     */
    public void addClosedListener(ControllerClosedEvent listener) {
        closedListeners.add(listener);
    }

    /**
     * Navigates to the main GUI.
     *
     * @param save True if the configuration should be saved, false otherwise.
     */
    protected void navigateToMainUI(boolean save) {
        if (save) {
            updatePropertiesWithControls();
        }

        for (var listener : closedListeners) {
            listener.onClosed(save);
        }

        App.setRoot(GlobalConfig.MAIN_GUI);
    }
}
