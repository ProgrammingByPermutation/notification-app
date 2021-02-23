package org.nullinside.notification_app.alerts;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.nullinside.notification_app.alerts.common.NoopAlert;
import org.nullinside.notification_app.alerts.common.TestUtilities;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link AlertsManager}.
 */
class AlertsManagerTest {
    /**
     * Ensures that we are not leaking {@link AlertsManager} singletons between tests.
     */
    @AfterEach
    void tearDown() {
        var manager = AlertsManager.getInstance();
        manager.dispose();
    }

    /**
     * Test the get instance to ensure it is a singleton.
     */
    @Test
    void getInstance() {
        var first = AlertsManager.getInstance();
        var second = AlertsManager.getInstance();
        assertEquals(first, second, "Not receiving singleton instance");

        first.dispose();
        var afterDispose = AlertsManager.getInstance();
        assertNotEquals(first, afterDispose, "Did not receive a new instance after dispose");
    }

    /**
     * Tests adding alerts to the manager.
     */
    @Test
    void addAlert() {
        var noop = new NoopAlert();
        var manager = AlertsManager.getInstance();
        manager.addAlert(noop);
        TestUtilities.assertCollectionEqualUnordered(new Object[]{noop}, manager.getAlerts(),
                "Alert not added to collection");

        var noop2 = new NoopAlert();
        manager.addAlert(noop2);
        TestUtilities.assertCollectionEqualUnordered(new Object[]{noop, noop2}, manager.getAlerts(),
                "Second alert not added to collection");
    }

    /**
     * Tests removing alerts from the manager.
     */
    @Test
    void removeAlert() {
        var noop = new NoopAlert();
        var noop2 = new NoopAlert();
        var noop3 = new NoopAlert();
        final boolean[] noopVarRemoved = {false, false,false};

        var manager = AlertsManager.getInstance();
        manager.addAlertsUpdatedListener((added, alert) -> {
            if (added) {
                return;
            }

            noopVarRemoved[0] = (alert == noop) ? true : noopVarRemoved[0];
            noopVarRemoved[1] = (alert == noop2) ? true : noopVarRemoved[1];
            noopVarRemoved[2] = (alert == noop3) ? true : noopVarRemoved[2];
        });

        manager.addAlert(noop);
        manager.addAlert(noop2);
        manager.addAlert(noop3);

        // Assert the initial condition.
        TestUtilities.assertCollectionEqualUnordered(new Object[]{noop, noop2, noop3}, manager.getAlerts(),
                "Adding alerts to collection failed");
        assertFalse(noopVarRemoved[0], "Alert received removed callback while still in alert manager");
        assertFalse(noopVarRemoved[1], "Alert received removed callback while still in alert manager");
        assertFalse(noopVarRemoved[2], "Alert received removed callback while still in alert manager");

        // Remove by object instance.
        manager.removeAlert(noop3);
        TestUtilities.assertCollectionEqualUnordered(new Object[]{noop, noop2}, manager.getAlerts(),
                "Removing alert by object failed");
        assertFalse(noopVarRemoved[0], "Alert received removed callback while still in alert manager");
        assertFalse(noopVarRemoved[1], "Alert received removed callback while still in alert manager");
        assertTrue(noopVarRemoved[2], "Alert did not receive callback when removed from alert manager");

        // Remove by id.
        manager.removeAlert(noop.getId());
        TestUtilities.assertCollectionEqualUnordered(new Object[]{noop2}, manager.getAlerts(),
                "Removing alert by alert id failed");
        assertTrue(noopVarRemoved[0], "Alert did not receive callback when removed from alert manager");
        assertFalse(noopVarRemoved[1], "Alert received removed callback while still in alert manager");
        assertTrue(noopVarRemoved[2], "Alert did not receive callback when removed from alert manager");
    }
}