package com.veteam.voluminousenergy.gametest;

import net.minecraft.gametest.framework.GameTestHelper;

import java.util.function.Consumer;

/**
 * One gametest: its name under the mod's namespace, the empty structure it runs in, its tick limit,
 * and whether its failure fails the run. Optional tests state intended behaviour the mod does not
 * have yet; the run reports them without failing.
 */
public record GameTestDefinition(String name, Consumer<GameTestHelper> function, String structure, int maxTicks, boolean required) {
    public static final String SMALL = "gametest/empty_5x5x5";

    public static GameTestDefinition of(String name, int maxTicks, Consumer<GameTestHelper> function) {
        return new GameTestDefinition(name, function, SMALL, maxTicks, true);
    }

    public static GameTestDefinition optional(String name, int maxTicks, Consumer<GameTestHelper> function) {
        return new GameTestDefinition(name, function, SMALL, maxTicks, false);
    }
}
