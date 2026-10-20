package com.veteam.voluminousenergy.gametest;

import net.minecraft.gametest.framework.GameTestHelper;

import java.util.function.Consumer;

/**
 * One gametest: its name under the mod's namespace, the empty structure it runs in, its tick limit,
 * and whether it only runs on request, as the worldgen surveys do.
 */
public record GameTestDefinition(String name, Consumer<GameTestHelper> function, String structure, int maxTicks, boolean manualOnly) {
    public static final String SMALL = "gametest/empty_5x5x5";
    public static final String LARGE = "gametest/empty_9x8x9";

    public static GameTestDefinition of(String name, int maxTicks, Consumer<GameTestHelper> function) {
        return new GameTestDefinition(name, function, SMALL, maxTicks, false);
    }

    public static GameTestDefinition large(String name, int maxTicks, Consumer<GameTestHelper> function) {
        return new GameTestDefinition(name, function, LARGE, maxTicks, false);
    }

    public static GameTestDefinition manual(String name, int maxTicks, Consumer<GameTestHelper> function) {
        return new GameTestDefinition(name, function, SMALL, maxTicks, true);
    }
}
