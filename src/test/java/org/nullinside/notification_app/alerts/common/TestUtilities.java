package org.nullinside.notification_app.alerts.common;

import org.opentest4j.AssertionFailedError;

import java.util.Arrays;
import java.util.List;

/**
 * Common utilities to gap missing functionality from JUnit.
 */
public class TestUtilities {
    /**
     * Asserts that two collections contain the same elements in any order.
     *
     * @param expected The expected collection.
     * @param actual   The actual collection.
     * @param message  The message on assert.
     */
    public static void assertCollectionEqualUnordered(List<Object> expected, List<Object> actual, String message) {
        if (expected.size() != actual.size()) {
            throw new AssertionFailedError(String.format("Collection of different sizes. Expected: %s Actual: %s",
                    expected.size(), actual.size()), expected, actual);
        }

        if (!expected.containsAll(actual)) {
            throw new AssertionFailedError("Extra elements present in actual collection",
                    expected, actual);
        }

        if (!actual.containsAll(expected)) {
            throw new AssertionFailedError("Expected elements missing from actual collection",
                    expected, actual);
        }
    }

    /**
     * Asserts that two collections contain the same elements in any order.
     *
     * @param expected The expected collection.
     * @param actual   The actual collection.
     * @param message  The message on assert.
     */
    public static void assertCollectionEqualUnordered(Object[] expected, Object[] actual, String message) {
        var e = Arrays.asList(expected);
        var a = Arrays.asList(actual);

        assertCollectionEqualUnordered(e, a, message);
    }
}
