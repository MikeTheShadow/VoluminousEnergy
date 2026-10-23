package com.veteam.voluminousenergy.gametest;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

import java.util.List;

/**
 * Machine storage and transfer through a machine's bottom-face fluid capability (spec
 * machines/storage-and-transfer). Optional tests assert intended behaviour that 1.21.1 does not have
 * yet and log what happens instead.
 */
public class StorageTests {
    private static final BlockPos MACHINE = new BlockPos(2, 1, 2);
    private static final BlockPos SECOND_MACHINE = new BlockPos(0, 1, 0);
    private static final int CAPACITY = VETileEntity.DEFAULT_TANK_CAPACITY;

    private StorageTests() {
    }

    static void register(List<GameTestDefinition> tests) {
        tests.add(GameTestDefinition.of("storage_fluid_fill_capacity", 20, StorageTests::fluidFillCapacity));
        tests.add(GameTestDefinition.of("storage_fluid_mixer_fill_routing", 20, StorageTests::fluidMixerFillRouting));
        tests.add(GameTestDefinition.of("storage_fluid_held_fluid_refuses_other", 20, StorageTests::fluidHeldFluidRefusesOther));
        tests.add(GameTestDefinition.of("storage_fluid_drain_rules", 20, StorageTests::fluidDrainRules));
        tests.add(GameTestDefinition.of("storage_fluid_drain_disabled_side", 20, StorageTests::fluidDrainDisabledSide));
        tests.add(GameTestDefinition.of("storage_fluid_electrolyzer_drains", 20, StorageTests::fluidElectrolyzerDrains));
        tests.add(GameTestDefinition.of("storage_fluid_empty_requests", 20, StorageTests::fluidEmptyRequests));
        tests.add(GameTestDefinition.of("storage_fluid_simulate", 20, StorageTests::fluidSimulate));

        tests.add(GameTestDefinition.optional("storage_fluid_fill_refuses_disabled_side", 20, StorageTests::fluidFillRefusesDisabledSide));
        tests.add(GameTestDefinition.optional("storage_fluid_drain_amount_refuses_disabled_side", 20, StorageTests::fluidDrainAmountRefusesDisabledSide));
        tests.add(GameTestDefinition.optional("storage_fluid_empty_machine_refuses_wrong_fluid", 20, StorageTests::fluidEmptyMachineRefusesWrongFluid));
        tests.add(GameTestDefinition.optional("storage_fluid_mixer_second_ingredient_first", 20, StorageTests::fluidMixerSecondIngredientFirst));
        tests.add(GameTestDefinition.optional("storage_fluid_combustion_oxidizer_routing", 20, StorageTests::fluidCombustionOxidizerRouting));
        tests.add(GameTestDefinition.optional("storage_fluid_out_of_range_tank_index", 20, StorageTests::fluidOutOfRangeTankIndex));
    }

    /**
     * An empty Distillation Unit's input tank takes Crude Oil up to its capacity and refuses the rest.
     */
    private static void fluidFillCapacity(GameTestHelper helper) {
        VETileEntity unit = MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        MachineKit.setTankSide(unit, 0, true);
        helper.runAfterDelay(2, () -> {
            IFluidHandler fluids = MachineKit.fluids(helper, MACHINE, Direction.DOWN);
            helper.assertValueEqual(fill(fluids, VEFluids.CRUDE_OIL_REG.get(), 5000), CAPACITY, "crude oil accepted");
            helper.assertValueEqual(unit.getFluidStackFromTank(0).getAmount(), CAPACITY, "crude oil in the input tank");
            GameTestLog.pass(helper, "storage_fluid_fill_capacity", "");
        });
    }

    /**
     * A fill without a tank index stays in a Fluid Mixer's first input tank once it holds that fluid,
     * so the second ingredient can still reach the second input tank.
     */
    private static void fluidMixerFillRouting(GameTestHelper helper) {
        VETileEntity mixer = MachineKit.place(helper, MACHINE, VEBlocks.FLUID_MIXER.block().get());
        MachineKit.setTankSide(mixer, 0, true);
        MachineKit.setTankSide(mixer, 1, true);
        helper.runAfterDelay(2, () -> {
            IFluidHandler fluids = MachineKit.fluids(helper, MACHINE, Direction.DOWN);
            Fluid hydrogen = VEFluids.HYDROGEN_REG.get();

            helper.assertValueEqual(fill(fluids, hydrogen, 5000), CAPACITY, "hydrogen accepted");
            helper.assertValueEqual(mixer.getFluidStackFromTank(0).getAmount(), CAPACITY, "hydrogen in the first input tank");
            helper.assertTrue(mixer.getFluidStackFromTank(1).isEmpty(), "hydrogen overflowed into the second input tank");

            helper.assertValueEqual(fill(fluids, hydrogen, 1000), 0, "hydrogen accepted into a full first input tank");
            helper.assertTrue(mixer.getFluidStackFromTank(1).isEmpty(), "hydrogen overflowed into the second input tank");

            helper.assertValueEqual(fill(fluids, VEFluids.NITROGEN_REG.get(), 1000), 1000, "nitrogen accepted");
            helper.assertValueEqual(mixer.getFluidStackFromTank(1).getAmount(), 1000, "nitrogen in the second input tank");
            helper.assertValueEqual(mixer.getFluidStackFromTank(0).getAmount(), CAPACITY, "hydrogen in the first input tank after nitrogen");
            GameTestLog.pass(helper, "storage_fluid_mixer_fill_routing", "");
        });
    }

    /**
     * A Distillation Unit's input tank holding Crude Oil refuses Lava.
     */
    private static void fluidHeldFluidRefusesOther(GameTestHelper helper) {
        VETileEntity unit = MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        MachineKit.setTankSide(unit, 0, true);
        MachineKit.setTank(unit, 0, new FluidStack(VEFluids.CRUDE_OIL_REG.get(), 1000));
        helper.runAfterDelay(2, () -> {
            IFluidHandler fluids = MachineKit.fluids(helper, MACHINE, Direction.DOWN);
            helper.assertValueEqual(fill(fluids, Fluids.LAVA, 1000), 0, "lava accepted");
            helper.assertTrue(unit.getFluidStackFromTank(0).is(VEFluids.CRUDE_OIL_REG.get()), "the input tank no longer holds crude oil");
            helper.assertValueEqual(unit.getFluidStackFromTank(0).getAmount(), 1000, "crude oil in the input tank");
            GameTestLog.pass(helper, "storage_fluid_held_fluid_refuses_other", "");
        });
    }

    /**
     * An enabled output tank drains, and an input tank drains only while extraction from input tanks
     * is allowed.
     */
    private static void fluidDrainRules(GameTestHelper helper) {
        VETileEntity unit = MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        MachineKit.setTank(unit, 0, new FluidStack(VEFluids.CRUDE_OIL_REG.get(), 1000));
        MachineKit.setTank(unit, 1, new FluidStack(VEFluids.NAPHTHA_REG.get(), 1000));
        MachineKit.setTankSide(unit, 0, true);
        MachineKit.setTankSide(unit, 1, true);
        IFluidHandler fluids = MachineKit.fluids(helper, MACHINE, Direction.DOWN);

        helper.assertValueEqual(drain(fluids, VEFluids.NAPHTHA_REG.get(), 500).getAmount(), 500, "naphtha drained from the output tank");
        helper.assertValueEqual(unit.getFluidStackFromTank(1).getAmount(), 500, "naphtha left in the output tank");
        withInputExtraction(false, () ->
                helper.assertTrue(drain(fluids, VEFluids.CRUDE_OIL_REG.get(), 500).isEmpty(), "crude oil drained from the input tank while not allowed"));
        withInputExtraction(true, () ->
                helper.assertValueEqual(drain(fluids, VEFluids.CRUDE_OIL_REG.get(), 500).getAmount(), 500, "crude oil drained from the input tank while allowed"));
        GameTestLog.pass(helper, "storage_fluid_drain_rules", "");
    }

    /**
     * An output tank whose side is disabled refuses a drain naming its fluid.
     */
    private static void fluidDrainDisabledSide(GameTestHelper helper) {
        VETileEntity unit = MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        MachineKit.setTank(unit, 1, new FluidStack(VEFluids.NAPHTHA_REG.get(), 1000));
        MachineKit.setTankSide(unit, 1, false);
        IFluidHandler fluids = MachineKit.fluids(helper, MACHINE, Direction.DOWN);

        helper.assertTrue(drain(fluids, VEFluids.NAPHTHA_REG.get(), 500).isEmpty(), "naphtha drained through a disabled side");
        helper.assertValueEqual(unit.getFluidStackFromTank(1).getAmount(), 1000, "naphtha in the output tank");
        GameTestLog.pass(helper, "storage_fluid_drain_disabled_side", "");
    }

    /**
     * A Fluid Electrolyzer's two output tanks drain by the fluid named, one tank per request, and a
     * drain naming only an amount skips the protected input tank.
     */
    private static void fluidElectrolyzerDrains(GameTestHelper helper) {
        VETileEntity electrolyzer = MachineKit.place(helper, MACHINE, VEBlocks.FLUID_ELECTROLYZER.block().get());
        Fluid oxygen = VEFluids.OXYGEN_REG.get();
        Fluid hydrogen = VEFluids.HYDROGEN_REG.get();
        for (int tank = 0; tank < 3; tank++) {
            MachineKit.setTankSide(electrolyzer, tank, true);
        }
        MachineKit.setTank(electrolyzer, 1, new FluidStack(oxygen, 1000));
        MachineKit.setTank(electrolyzer, 2, new FluidStack(hydrogen, 1000));
        IFluidHandler fluids = MachineKit.fluids(helper, MACHINE, Direction.DOWN);

        FluidStack drainedHydrogen = drain(fluids, hydrogen, 600);
        helper.assertTrue(drainedHydrogen.is(hydrogen), "the named drain returned " + drainedHydrogen.getFluid());
        helper.assertValueEqual(drainedHydrogen.getAmount(), 600, "hydrogen drained");
        helper.assertValueEqual(electrolyzer.getFluidStackFromTank(2).getAmount(), 400, "hydrogen left in the second output tank");
        helper.assertValueEqual(electrolyzer.getFluidStackFromTank(1).getAmount(), 1000, "oxygen after draining hydrogen");

        helper.assertValueEqual(drain(fluids, oxygen, 1500).getAmount(), 1000, "oxygen drained when asking for more than the tank holds");
        helper.assertTrue(electrolyzer.getFluidStackFromTank(1).isEmpty(), "oxygen left after draining the whole tank");

        MachineKit.setTank(electrolyzer, 0, new FluidStack(Fluids.WATER, 1000));
        MachineKit.setTank(electrolyzer, 1, new FluidStack(oxygen, 1000));
        withInputExtraction(false, () -> {
            FluidStack drained = fluids.drain(500, FluidAction.EXECUTE);
            helper.assertTrue(drained.is(oxygen), "a drain naming only an amount returned " + drained.getFluid());
            helper.assertValueEqual(drained.getAmount(), 500, "oxygen drained by amount");
        });
        helper.assertValueEqual(electrolyzer.getFluidStackFromTank(0).getAmount(), 1000, "water in the protected input tank");
        helper.assertValueEqual(electrolyzer.getFluidStackFromTank(2).getAmount(), 400, "hydrogen after a drain by amount");
        GameTestLog.pass(helper, "storage_fluid_electrolyzer_drains", "");
    }

    /**
     * Requests for no fluid or for 0 mB move nothing.
     */
    private static void fluidEmptyRequests(GameTestHelper helper) {
        VETileEntity unit = MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        Fluid crudeOil = VEFluids.CRUDE_OIL_REG.get();
        MachineKit.setTank(unit, 0, new FluidStack(crudeOil, 1000));
        MachineKit.setTank(unit, 1, new FluidStack(VEFluids.NAPHTHA_REG.get(), 1000));
        MachineKit.setTankSide(unit, 0, true);
        MachineKit.setTankSide(unit, 1, true);
        IFluidHandler fluids = MachineKit.fluids(helper, MACHINE, Direction.DOWN);

        helper.assertValueEqual(fluids.fill(FluidStack.EMPTY, FluidAction.EXECUTE), 0, "empty fluid accepted");
        helper.assertValueEqual(fill(fluids, crudeOil, 0), 0, "0 mB of crude oil accepted");
        helper.assertTrue(fluids.drain(0, FluidAction.EXECUTE).isEmpty(), "a drain of 0 mB returned fluid");
        helper.assertTrue(fluids.drain(FluidStack.EMPTY, FluidAction.EXECUTE).isEmpty(), "a drain of empty fluid returned fluid");
        helper.assertValueEqual(unit.getFluidStackFromTank(0).getAmount(), 1000, "crude oil in the input tank");
        helper.assertValueEqual(unit.getFluidStackFromTank(1).getAmount(), 1000, "naphtha in the output tank");
        GameTestLog.pass(helper, "storage_fluid_empty_requests", "");
    }

    /**
     * Simulated fills and drains report what they would move and leave the tanks alone.
     */
    private static void fluidSimulate(GameTestHelper helper) {
        VETileEntity mixer = MachineKit.place(helper, MACHINE, VEBlocks.FLUID_MIXER.block().get());
        MachineKit.setTankSide(mixer, 0, true);
        MachineKit.setTankSide(mixer, 1, true);
        VETileEntity unit = MachineKit.place(helper, SECOND_MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        Fluid naphtha = VEFluids.NAPHTHA_REG.get();
        MachineKit.setTank(unit, 1, new FluidStack(naphtha, 1000));
        MachineKit.setTankSide(unit, 1, true);
        helper.runAfterDelay(2, () -> {
            IFluidHandler mixerFluids = MachineKit.fluids(helper, MACHINE, Direction.DOWN);
            int simulatedFill = mixerFluids.fill(new FluidStack(VEFluids.HYDROGEN_REG.get(), 1000), FluidAction.SIMULATE);
            helper.assertValueEqual(simulatedFill, 1000, "hydrogen a simulated fill reports");
            helper.assertTrue(mixer.getFluidStackFromTank(0).isEmpty(), "a simulated fill changed the first input tank");
            helper.assertTrue(mixer.getFluidStackFromTank(1).isEmpty(), "a simulated fill changed the second input tank");

            IFluidHandler unitFluids = MachineKit.fluids(helper, SECOND_MACHINE, Direction.DOWN);
            FluidStack simulatedDrain = unitFluids.drain(new FluidStack(naphtha, 500), FluidAction.SIMULATE);
            helper.assertTrue(simulatedDrain.is(naphtha), "a simulated drain returned " + simulatedDrain.getFluid());
            helper.assertValueEqual(simulatedDrain.getAmount(), 500, "naphtha a simulated drain reports");
            helper.assertValueEqual(unit.getFluidStackFromTank(1).getAmount(), 1000, "naphtha after a simulated drain");
            GameTestLog.pass(helper, "storage_fluid_simulate", "");
        });
    }

    /**
     * Optional: a disabled input side refuses fills, as a disabled item slot refuses hoppers.
     */
    private static void fluidFillRefusesDisabledSide(GameTestHelper helper) {
        String name = "storage_fluid_fill_refuses_disabled_side";
        VETileEntity unit = MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        MachineKit.setTankSide(unit, 0, false);
        helper.runAfterDelay(2, () -> {
            int accepted = fill(MachineKit.fluids(helper, MACHINE, Direction.DOWN), VEFluids.CRUDE_OIL_REG.get(), 1000);
            GameTestLog.value(name, "accepted", accepted);
            helper.assertValueEqual(accepted, 0, "crude oil accepted through a disabled side");
            GameTestLog.pass(helper, name, "");
        });
    }

    /**
     * Optional: a drain naming only an amount takes nothing from an output tank whose side is disabled.
     */
    private static void fluidDrainAmountRefusesDisabledSide(GameTestHelper helper) {
        String name = "storage_fluid_drain_amount_refuses_disabled_side";
        VETileEntity electrolyzer = MachineKit.place(helper, MACHINE, VEBlocks.FLUID_ELECTROLYZER.block().get());
        MachineKit.setTank(electrolyzer, 1, new FluidStack(VEFluids.OXYGEN_REG.get(), 1000));
        MachineKit.setTankSide(electrolyzer, 1, false);
        IFluidHandler fluids = MachineKit.fluids(helper, MACHINE, Direction.DOWN);
        withInputExtraction(false, () -> {
            FluidStack drained = fluids.drain(500, FluidAction.EXECUTE);
            GameTestLog.value(name, "drained", drained.getAmount() + " " + drained.getFluid());
            helper.assertTrue(drained.isEmpty(), "a drain by amount took " + drained.getAmount() + " mB through a disabled side");
        });
        GameTestLog.pass(helper, name, "");
    }

    /**
     * Optional: an empty Distillation Unit refuses Lava, which no Distillation Unit recipe takes.
     */
    private static void fluidEmptyMachineRefusesWrongFluid(GameTestHelper helper) {
        String name = "storage_fluid_empty_machine_refuses_wrong_fluid";
        VETileEntity unit = MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        MachineKit.setTankSide(unit, 0, true);
        helper.runAfterDelay(2, () -> {
            int accepted = fill(MachineKit.fluids(helper, MACHINE, Direction.DOWN), Fluids.LAVA, 1000);
            GameTestLog.value(name, "accepted", accepted);
            helper.assertValueEqual(accepted, 0, "lava accepted into an empty input tank");
            GameTestLog.pass(helper, name, "");
        });
    }

    /**
     * Optional: Nitrogen piped into an empty Fluid Mixer goes to the second input tank, where the
     * recipe expects it, leaving the first free for Hydrogen.
     */
    private static void fluidMixerSecondIngredientFirst(GameTestHelper helper) {
        String name = "storage_fluid_mixer_second_ingredient_first";
        VETileEntity mixer = MachineKit.place(helper, MACHINE, VEBlocks.FLUID_MIXER.block().get());
        MachineKit.setTankSide(mixer, 0, true);
        MachineKit.setTankSide(mixer, 1, true);
        helper.runAfterDelay(2, () -> {
            fill(MachineKit.fluids(helper, MACHINE, Direction.DOWN), VEFluids.NITROGEN_REG.get(), 1000);
            GameTestLog.value(name, "first_input", mixer.getFluidStackFromTank(0).getAmount() + " " + mixer.getFluidStackFromTank(0).getFluid());
            GameTestLog.value(name, "second_input", mixer.getFluidStackFromTank(1).getAmount() + " " + mixer.getFluidStackFromTank(1).getFluid());
            helper.assertTrue(mixer.getFluidStackFromTank(0).isEmpty(), "nitrogen went into the first input tank");
            helper.assertValueEqual(mixer.getFluidStackFromTank(1).getAmount(), 1000, "nitrogen in the second input tank");
            GameTestLog.pass(helper, name, "");
        });
    }

    /**
     * Optional: Oxygen piped into an empty Combustion Generator goes to the oxidizer tank, not the fuel
     * tank.
     */
    private static void fluidCombustionOxidizerRouting(GameTestHelper helper) {
        String name = "storage_fluid_combustion_oxidizer_routing";
        VETileEntity generator = MachineKit.place(helper, MACHINE, VEBlocks.COMBUSTION_GENERATOR.block().get());
        MachineKit.setTankSide(generator, 0, true);
        MachineKit.setTankSide(generator, 1, true);
        helper.runAfterDelay(2, () -> {
            fill(MachineKit.fluids(helper, MACHINE, Direction.DOWN), VEFluids.OXYGEN_REG.get(), 1000);
            GameTestLog.value(name, "fuel_tank", generator.getFluidStackFromTank(0).getAmount() + " " + generator.getFluidStackFromTank(0).getFluid());
            GameTestLog.value(name, "oxidizer_tank", generator.getFluidStackFromTank(1).getAmount() + " " + generator.getFluidStackFromTank(1).getFluid());
            helper.assertTrue(generator.getFluidStackFromTank(0).isEmpty(), "oxygen went into the fuel tank");
            helper.assertValueEqual(generator.getFluidStackFromTank(1).getAmount(), 1000, "oxygen in the oxidizer tank");
            GameTestLog.pass(helper, name, "");
        });
    }

    /**
     * Optional: asking whether a fluid is valid for a tank index the face does not have answers false.
     */
    private static void fluidOutOfRangeTankIndex(GameTestHelper helper) {
        String name = "storage_fluid_out_of_range_tank_index";
        MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        IFluidHandler fluids = MachineKit.fluids(helper, MACHINE, Direction.DOWN);
        FluidStack crudeOil = new FluidStack(VEFluids.CRUDE_OIL_REG.get(), 1000);
        for (int tank : new int[]{-1, fluids.getTanks()}) {
            try {
                helper.assertFalse(fluids.isFluidValid(tank, crudeOil), "tank " + tank + " reported crude oil as valid");
            } catch (IndexOutOfBoundsException exception) {
                GameTestLog.value(name, "tank_" + tank, exception.getClass().getSimpleName());
                helper.fail("isFluidValid threw for tank " + tank + ": " + exception.getMessage());
            }
        }
        GameTestLog.pass(helper, name, "");
    }

    private static int fill(IFluidHandler fluids, Fluid fluid, int amount) {
        return fluids.fill(new FluidStack(fluid, amount), FluidAction.EXECUTE);
    }

    private static FluidStack drain(IFluidHandler fluids, Fluid fluid, int amount) {
        return fluids.drain(new FluidStack(fluid, amount), FluidAction.EXECUTE);
    }

    /**
     * Runs a check with the {@code Allow Extraction from Input Tanks} option set, then restores it
     * before any other test can see the change.
     */
    private static void withInputExtraction(boolean allowed, Runnable check) {
        boolean previous = Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.get();
        try {
            Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.set(allowed);
            check.run();
        } finally {
            Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.set(previous);
        }
    }
}
