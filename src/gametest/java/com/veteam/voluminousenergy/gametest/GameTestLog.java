package com.veteam.voluminousenergy.gametest;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.gametest.framework.GameTestHelper;

/**
 * Writes the result lines the autotest harness reads. Vanilla logs only failures, so each test
 * logs its own pass with any figures worth keeping in the report.
 */
public class GameTestLog {
    private static final String PREFIX = "VE-AUTOTEST";

    private GameTestLog() {
    }

    /**
     * Logs a pass and ends the test successfully.
     */
    public static void pass(GameTestHelper helper, String name, String details) {
        note(name, details);
        helper.succeed();
    }

    /**
     * Logs a pass for a test that the framework ends itself, such as one using
     * {@code succeedWhen}.
     */
    public static void note(String name, String details) {
        VoluminousEnergy.LOGGER.info("{} PASS {} {}", PREFIX, name, details);
    }

    public static void value(String name, String key, Object value) {
        VoluminousEnergy.LOGGER.info("{} VALUE {} {}={}", PREFIX, name, key, value);
    }

    /**
     * Fails the test unless the actual value equals the expected one. Vanilla's
     * {@code assertValueEqual} reports the two values the other way round.
     */
    public static <T> void assertEqual(GameTestHelper helper, T actual, T expected, String name) {
        helper.assertTrue(actual.equals(expected), "Expected " + name + " to be " + expected + ", but was " + actual);
    }
}
