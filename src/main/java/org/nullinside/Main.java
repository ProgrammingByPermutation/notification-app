package org.nullinside;

/**
 * This is a hack required to compile a JavaFX application into a fat jar. The issue is that JavaFX needs
 * to derive from javafx.application.Application. Unfortunately, fat jars don't work with this derivation.
 * So we use this class as a proxy to pass through to teh JavaFX class. This circumvents the issue of the main
 * application deriving from something that makes the far jar upset.
 */
public class Main {
    /**
     * A pass through the JavaFX main method.
     *
     * @param args The arguments passed to the application.
     */
    public static void main(String[] args) {
        App.main(args);
    }
}
