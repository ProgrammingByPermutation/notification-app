package org.nullinside.notification_app.controllers;

import javafx.beans.binding.Bindings;
import javafx.scene.control.TextInputControl;
import javafx.util.Pair;
import org.nullinside.notification_app.App;
import org.nullinside.notification_app.Configuration;
import org.nullinside.utilities.ReflectionUtilities;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBaseController {
    protected List<ControllerClosedListener> closedListeners = new ArrayList<>();

    /**
     * Maps controls to the fields they update.
     *
     * @return A collection of mappings of control to their property names in the class.
     */
    protected List<Pair<TextInputControl, String>> getFieldToPropertyMapping() {
        return null;
    }

    /**
     * Updates the text of the controls defined in the getFieldToPropertyMapping() method with
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
                map.getValue().set(map.getKey(), control.getText());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets up a binding between the control text updating and the field as defined
     * in the getFieldToPropertyMapping() collection.
     */
    protected void bindControlsToProperties() {
        var mapping = getFieldToPropertyMapping();
        if (null == mapping) {
            return;
        }

        for (var pair : mapping) {
            var control = pair.getKey();
            var propertyName = pair.getValue();
            control.promptTextProperty().bind(Bindings.createStringBinding(() -> {
                var map = ReflectionUtilities.getFieldNested(getClass(), propertyName, this);
                if (null == map) {
                    return control.getText();
                }

                map.getValue().setAccessible(true);
                map.getValue().set(map.getKey(), control.getText());
                return control.getText();
            }, control.textProperty()));
        }
    }

    public void addClosedListener(ControllerClosedListener listener) {
        closedListeners.add(listener);
    }

    protected void navigateToMainUI(boolean save) {
        if (save) {
            updatePropertiesWithControls();
        }

        for (var listener : closedListeners) {
            listener.onClosed(save);
        }

        App.setRoot(Configuration.MAIN_GUI);
    }
}
