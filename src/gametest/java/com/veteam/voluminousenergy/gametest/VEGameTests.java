package com.veteam.voluminousenergy.gametest;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.neoforge.gametest.GameTestHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Registers every Voluminous Energy gametest. Each test runs in an empty structure under the mod's
 * namespace, which is what {@code neoforge.enabledGameTestNamespaces} filters generated tests by.
 */
@GameTestHolder(VoluminousEnergy.MODID)
public class VEGameTests {
    private static final String BATCH = "defaultBatch";

    private VEGameTests() {
    }

    static List<GameTestDefinition> all() {
        List<GameTestDefinition> tests = new ArrayList<>();
        tests.add(GameTestDefinition.of("smoke", 20, GameTestHelper::succeed));
        StorageTests.register(tests);
        return tests;
    }

    @GameTestGenerator
    public static List<TestFunction> generate() {
        List<TestFunction> functions = new ArrayList<>();
        for (GameTestDefinition test : all()) {
            functions.add(new TestFunction(BATCH, VoluminousEnergy.MODID + "." + test.name(), VoluminousEnergy.MODID + ":" + test.structure(),
                    Rotation.NONE, test.maxTicks(), 0, test.required(), false, 1, 1, false, test.function()));
        }
        return functions;
    }
}
