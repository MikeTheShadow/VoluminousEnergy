package com.veteam.voluminousenergy.gametest;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Registers every Voluminous Energy gametest. Test functions go into the test function registry,
 * and each test instance runs one of them in an empty structure.
 */
@EventBusSubscriber(modid = VoluminousEnergy.MODID)
public class VEGameTests {

    private VEGameTests() {
    }

    static List<GameTestDefinition> all() {
        List<GameTestDefinition> tests = new ArrayList<>();
        tests.add(GameTestDefinition.of("smoke", 20, GameTestHelper::succeed));
        StorageTests.register(tests);
        RecipeTests.register(tests);
        MenuTests.register(tests);
        ItemTests.register(tests);
        WorldTests.register(tests);
        DepositTests.register(tests);
        WorldgenTests.register(tests);
        CompatTests.register(tests);
        return tests;
    }

    @SubscribeEvent
    static void registerFunctions(RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, helper -> {
            for (GameTestDefinition test : all()) {
                helper.register(id(test.name()), test.function());
            }
        });
    }

    @SubscribeEvent
    static void registerTests(RegisterGameTestsEvent event) {
        Holder<TestEnvironmentDefinition<?>> environment = event.registerEnvironment(id("default"));
        for (GameTestDefinition test : all()) {
            TestData<Holder<TestEnvironmentDefinition<?>>> data = new TestData<>(environment, Level.OVERWORLD, id(test.structure()),
                    test.maxTicks(), 0, test.required(), Rotation.NONE, test.manualOnly(), 1, 1, false, 0);
            ResourceKey<Consumer<GameTestHelper>> function = ResourceKey.create(Registries.TEST_FUNCTION, id(test.name()));
            event.registerTest(id(test.name()), new FunctionGameTestInstance(function, data));
        }
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, path);
    }
}
