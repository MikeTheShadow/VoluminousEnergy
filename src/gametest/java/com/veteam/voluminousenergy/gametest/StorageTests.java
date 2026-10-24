package com.veteam.voluminousenergy.gametest;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.blocks.tiles.inventory.VEItemStackHandler;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.recipe.CrusherRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.processor.BasicProcessor;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.util.MultiFluidSlotWrapper;
import com.veteam.voluminousenergy.util.TankType;
import com.veteam.voluminousenergy.util.VEAttachments;
import com.veteam.voluminousenergy.util.VEFluidTank;
import com.veteam.voluminousenergy.util.VEItemCapabilities;
import com.veteam.voluminousenergy.util.VERelationalTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Machine storage and transfer: saving and loading, the 26.2 save format, side rules, transfers
 * through capabilities, bucket and battery slots, and the owner operations (spec
 * machines/storage-and-transfer).
 */
public class StorageTests {
    private static final BlockPos MACHINE = new BlockPos(2, 1, 2);

    private StorageTests() {
    }

    static void register(List<GameTestDefinition> tests) {
        tests.add(GameTestDefinition.large("storage_round_trip", 20, StorageTests::roundTrip));
        tests.add(GameTestDefinition.of("storage_hopper_enabled_side", 100, helper -> hopperIntoCrusher(helper, true)));
        tests.add(GameTestDefinition.of("storage_hopper_disabled_side", 100, helper -> hopperIntoCrusher(helper, false)));
        tests.add(GameTestDefinition.of("storage_output_refuses_insert", 20, StorageTests::outputRefusesInsert));
        tests.add(GameTestDefinition.of("storage_output_extract", 20, StorageTests::outputExtract));
        tests.add(GameTestDefinition.of("storage_fluid_fill_rules", 40, StorageTests::fluidFillRules));
        tests.add(GameTestDefinition.of("storage_fluid_drain_rules", 20, StorageTests::fluidDrainRules));
        tests.add(GameTestDefinition.of("storage_energy_insert_limit", 2400, StorageTests::energyInsertLimit));
        tests.add(GameTestDefinition.of("storage_transaction_abort", 20, StorageTests::transactionAbort));
        tests.add(GameTestDefinition.of("storage_nested_transaction_abort", 20, StorageTests::nestedTransactionAbort));
        tests.add(GameTestDefinition.of("storage_bucket_slots", 60, StorageTests::bucketSlots));
        tests.add(GameTestDefinition.of("storage_battery_box_charges", 60, StorageTests::batteryBoxCharges));
        tests.add(GameTestDefinition.of("storage_stalled_recipe_resumes", 1200, StorageTests::stalledRecipeResumes));
        tests.add(GameTestDefinition.of("storage_owner_operations", 20, StorageTests::ownerOperations));
        tests.add(GameTestDefinition.of("storage_fluid_multi_tank_extract", 20, StorageTests::fluidMultiTankExtract));
        tests.add(GameTestDefinition.of("storage_fluid_mixer_fill_routing", 40, StorageTests::fluidMixerFillRouting));
        tests.add(GameTestDefinition.of("storage_fluid_drain_disabled_side", 20, StorageTests::fluidDrainDisabledSide));
        tests.add(GameTestDefinition.of("storage_fluid_electrolyzer_drains", 20, StorageTests::fluidElectrolyzerDrains));
        tests.add(GameTestDefinition.of("storage_fluid_drain_amount_refuses_disabled_side", 20, StorageTests::fluidDrainAmountRefusesDisabledSide));
        tests.add(GameTestDefinition.of("storage_fluid_zero_requests", 20, StorageTests::fluidZeroRequests));
        tests.add(GameTestDefinition.of("storage_fluid_fill_last_millibucket", 20, StorageTests::fluidFillLastMillibucket));
        tests.add(GameTestDefinition.of("storage_fluid_fill_largest_offer", 20, StorageTests::fluidFillLargestOffer));
        tests.add(GameTestDefinition.of("storage_fluid_fill_named_tank", 20, StorageTests::fluidFillNamedTank));
        tests.add(GameTestDefinition.of("storage_fluid_out_of_range_transfers", 20, StorageTests::fluidOutOfRangeTransfers));
        tests.add(GameTestDefinition.of("storage_fluid_out_of_range_tank_index", 20, StorageTests::fluidOutOfRangeTankIndex));
        tests.add(GameTestDefinition.of("storage_fluid_drain_spans_tanks", 20, StorageTests::fluidDrainSpansTanks));

        tests.add(GameTestDefinition.optional("storage_fluid_fill_refuses_disabled_side", 20, StorageTests::fluidFillRefusesDisabledSide));
        tests.add(GameTestDefinition.optional("storage_fluid_empty_machine_refuses_wrong_fluid", 20, StorageTests::fluidEmptyMachineRefusesWrongFluid));
        tests.add(GameTestDefinition.optional("storage_fluid_mixer_second_ingredient_first", 20, StorageTests::fluidMixerSecondIngredientFirst));
        tests.add(GameTestDefinition.optional("storage_fluid_combustion_oxidizer_routing", 20, StorageTests::fluidCombustionOxidizerRouting));
    }

    /**
     * T2.1 and T2.2: every machine keeps its contents and side settings through save and load, and
     * saves them under 26.2's keys.
     */
    private static void roundTrip(GameTestHelper helper) {
        HolderLookup.Provider registries = helper.getLevel().registryAccess();
        List<Block> machines = MachineKit.machineBlocks();
        for (int index = 0; index < machines.size(); index++) {
            Block block = machines.get(index);
            BlockPos pos = new BlockPos(1 + index % 7, 1 + index / 49, 1 + index / 7 % 7);
            VETileEntity tile = MachineKit.place(helper, pos, block);
            MachineKit.fill(tile);
            String expected = MachineKit.describe(tile);

            CompoundTag saved = tile.saveWithFullMetadata(registries);
            assertSavedShape(helper, MachineKit.name(block), tile, saved);

            BlockEntity loaded = BlockEntity.loadStatic(helper.absolutePos(pos), tile.getBlockState(), saved, registries);
            helper.assertTrue(loaded instanceof VETileEntity, MachineKit.name(block) + " did not load as a machine");
            helper.assertValueEqual(MachineKit.describe((VETileEntity) loaded), expected, MachineKit.name(block) + " after save and load");
        }
        GameTestLog.pass(helper, "storage_round_trip", machines.size() + " machines");
    }

    private static void assertSavedShape(GameTestHelper helper, String name, VETileEntity tile, CompoundTag saved) {
        VEItemStackHandler inventory = tile.getInventory();
        if (inventory != null) {
            CompoundTag inv = saved.getCompoundOrEmpty("inv");
            helper.assertValueEqual(inv.getIntOr("Size", -1), inventory.size(), name + " inv.Size");
            ListTag items = inv.getListOrEmpty("Items");
            helper.assertValueEqual(items.size(), inventory.size(), name + " inv.Items entries");
            for (int entry = 0; entry < items.size(); entry++) {
                helper.assertTrue(items.getCompoundOrEmpty(entry).contains("Slot"), name + " inv.Items entry without Slot");
            }
        }
        VEEnergyStorage energy = tile.getEnergy();
        if (energy != null) {
            CompoundTag energyTag = saved.getCompoundOrEmpty("energy");
            for (String key : List.of("energy", "energy_production", "energy_consumption", "upgrade_slot")) {
                helper.assertTrue(energyTag.getInt(key).isPresent(), name + " energy." + key + " missing");
            }
        }
        for (VERelationalTank tank : tile.getRelationalTanks()) {
            helper.assertTrue(saved.getCompoundOrEmpty(tank.getTankName()).contains("Fluid"), name + " " + tank.getTankName() + ".Fluid missing");
        }
    }

    /**
     * T2.4 and T2.5: a hopper above a Crusher fills its input slot only while that slot's side is
     * enabled.
     */
    private static void hopperIntoCrusher(GameTestHelper helper, boolean enabled) {
        VETileEntity crusher = MachineKit.place(helper, MACHINE, VEBlocks.CRUSHER.block().get());
        crusher.updatePacketFromGui(enabled, 0);
        BlockPos hopperPos = MACHINE.above();
        helper.setBlock(hopperPos, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN));
        helper.getBlockEntity(hopperPos, HopperBlockEntity.class).setItem(0, new ItemStack(Items.IRON_ORE, 4));

        if (enabled) {
            helper.succeedWhen(() -> {
                helper.assertTrue(crusher.getInventory().getStack(0).is(Items.IRON_ORE), "iron ore not in the crusher input");
                GameTestLog.note("storage_hopper_enabled_side", "");
            });
        } else {
            helper.runAfterDelay(60, () -> {
                helper.assertTrue(crusher.getInventory().getStack(0).isEmpty(), "a disabled input side accepted items");
                GameTestLog.pass(helper, "storage_hopper_disabled_side", "");
            });
        }
    }

    /**
     * T2.6: an output face refuses insertion.
     */
    private static void outputRefusesInsert(GameTestHelper helper) {
        VETileEntity crusher = MachineKit.place(helper, MACHINE, VEBlocks.CRUSHER.block().get());
        crusher.updatePacketFromGui(true, 1);
        ResourceHandler<ItemResource> output = MachineKit.capability(helper, Capabilities.Item.BLOCK, MACHINE, Direction.DOWN);
        helper.assertTrue(output != null, "no item capability on the output face");
        int inserted;
        try (Transaction transaction = Transaction.openRoot()) {
            inserted = output.insert(ItemResource.of(Items.COBBLESTONE), 16, transaction);
            transaction.commit();
        }
        helper.assertValueEqual(inserted, 0, "items inserted into the output face");
        GameTestLog.pass(helper, "storage_output_refuses_insert", "");
    }

    /**
     * T2.7: an enabled output face can be emptied, and the machine only hears of it on commit.
     */
    private static void outputExtract(GameTestHelper helper) {
        VETileEntity crusher = MachineKit.place(helper, MACHINE, VEBlocks.CRUSHER.block().get());
        crusher.updatePacketFromGui(true, 1);
        crusher.getInventory().setStack(1, new ItemStack(Items.GRAVEL, 64));
        BasicProcessor processor = (BasicProcessor) crusher.getRecipeProcessor();
        processor.setRecipeDirty(false);

        ResourceHandler<ItemResource> output = MachineKit.capability(helper, Capabilities.Item.BLOCK, MACHINE, Direction.DOWN);
        int extracted;
        try (Transaction transaction = Transaction.openRoot()) {
            extracted = output.extract(ItemResource.of(Items.GRAVEL), 40, transaction);
            helper.assertFalse(processor.isRecipeDirty(), "the machine was notified before the transfer committed");
            transaction.commit();
        }
        helper.assertValueEqual(extracted, 40, "gravel extracted");
        helper.assertValueEqual(crusher.getInventory().getStack(1).getCount(), 24, "gravel left in the output slot");
        helper.assertTrue(processor.isRecipeDirty(), "the machine was not notified after the transfer committed");
        GameTestLog.pass(helper, "storage_output_extract", "");
    }

    /**
     * T2.8: a Distillation Unit's input tank takes Crude Oil up to its capacity and refuses Lava.
     */
    private static void fluidFillRules(GameTestHelper helper) {
        VETileEntity unit = MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        helper.runAfterDelay(2, () -> {
            ResourceHandler<FluidResource> fluids = MachineKit.capability(helper, Capabilities.Fluid.BLOCK, MACHINE, Direction.DOWN);
            helper.assertTrue(fluids != null, "no fluid capability");
            int crudeOil;
            int lava;
            try (Transaction transaction = Transaction.openRoot()) {
                crudeOil = fluids.insert(FluidResource.of(VEFluids.CRUDE_OIL_REG.get()), 5000, transaction);
                lava = fluids.insert(FluidResource.of(Fluids.LAVA), 1000, transaction);
                transaction.commit();
            }
            helper.assertValueEqual(crudeOil, VETileEntity.DEFAULT_TANK_CAPACITY, "crude oil accepted");
            helper.assertValueEqual(lava, 0, "lava accepted");
            helper.assertValueEqual(unit.getFluidStackFromTank(0).getAmount(), VETileEntity.DEFAULT_TANK_CAPACITY, "crude oil in the input tank");
            GameTestLog.pass(helper, "storage_fluid_fill_rules", "");
        });
    }

    /**
     * T2.9: an enabled output tank drains, and an input tank drains only while extraction from input
     * tanks is allowed.
     */
    private static void fluidDrainRules(GameTestHelper helper) {
        VETileEntity unit = MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        VERelationalTank input = unit.getRelationalTank(0);
        VERelationalTank output = unit.getRelationalTank(1);
        input.getTank().setFluid(new FluidStack(VEFluids.CRUDE_OIL_REG.get(), 1000));
        output.getTank().setFluid(new FluidStack(VEFluids.NAPHTHA_REG.get(), 1000));
        unit.updateTankPacketFromGui(true, input.getSlotNum());
        unit.updateTankPacketFromGui(true, output.getSlotNum());

        boolean allowed = Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.get();
        try {
            ResourceHandler<FluidResource> fluids = MachineKit.capability(helper, Capabilities.Fluid.BLOCK, MACHINE, Direction.DOWN);
            helper.assertValueEqual(drain(fluids, VEFluids.NAPHTHA_REG.get(), 500), 500, "naphtha drained from the output tank");

            Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.set(false);
            helper.assertValueEqual(drain(fluids, VEFluids.CRUDE_OIL_REG.get(), 500), 0, "crude oil drained from the input tank while not allowed");

            Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.set(true);
            helper.assertValueEqual(drain(fluids, VEFluids.CRUDE_OIL_REG.get(), 500), 500, "crude oil drained from the input tank while allowed");
        } finally {
            Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.set(allowed);
        }
        GameTestLog.pass(helper, "storage_fluid_drain_rules", "");
    }

    private static int drain(ResourceHandler<FluidResource> fluids, Fluid fluid, int amount) {
        try (Transaction transaction = Transaction.openRoot()) {
            int drained = fluids.extract(FluidResource.of(fluid), amount, transaction);
            transaction.commit();
            return drained;
        }
    }

    /**
     * T2.10: one energy insert is limited by the machine's transfer rate, and repeated inserts never
     * exceed its capacity.
     */
    private static void energyInsertLimit(GameTestHelper helper) {
        VETileEntity furnace = MachineKit.place(helper, MACHINE, VEBlocks.ELECTRIC_FURNACE.block().get());
        EnergyHandler energy = MachineKit.capability(helper, Capabilities.Energy.BLOCK, MACHINE, Direction.UP);
        helper.assertTrue(energy != null, "no energy capability");
        int first = insertEnergy(energy, 1_000_000);
        helper.assertValueEqual(first, Math.min(Config.ELECTRIC_FURNACE_TRANSFER.get(), furnace.getEnergy().getCapacity()), "energy accepted in one insert");
        helper.onEachTick(() -> {
            insertEnergy(energy, 1_000_000);
            helper.assertTrue(furnace.getEnergy().getAmountAsInt() <= furnace.getEnergy().getCapacity(), "energy above capacity");
        });
        helper.runAfterDelay(furnace.getEnergy().getCapacity() / Math.max(first, 1) + 5, () -> {
            helper.assertValueEqual(furnace.getEnergy().getAmountAsInt(), furnace.getEnergy().getCapacity(), "energy after repeated inserts");
            GameTestLog.pass(helper, "storage_energy_insert_limit", "");
        });
    }

    private static int insertEnergy(EnergyHandler energy, int amount) {
        try (Transaction transaction = Transaction.openRoot()) {
            int inserted = energy.insert(amount, transaction);
            transaction.commit();
            return inserted;
        }
    }

    /**
     * T2.11: inserts in a transaction that is never committed leave the machine as it was.
     */
    private static void transactionAbort(GameTestHelper helper) {
        VETileEntity unit = MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        String before = MachineKit.describe(unit);
        try (Transaction transaction = Transaction.openRoot()) {
            insertEverything(helper, transaction);
        }
        helper.assertValueEqual(MachineKit.describe(unit), before, "machine after an aborted transaction");
        GameTestLog.pass(helper, "storage_transaction_abort", "");
    }

    /**
     * T2.12: a committed inner transaction is still undone when its outer transaction aborts.
     */
    private static void nestedTransactionAbort(GameTestHelper helper) {
        VETileEntity unit = MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        String before = MachineKit.describe(unit);
        try (Transaction outer = Transaction.openRoot()) {
            try (Transaction inner = Transaction.open(outer)) {
                insertEverything(helper, inner);
                inner.commit();
            }
        }
        helper.assertValueEqual(MachineKit.describe(unit), before, "machine after an aborted outer transaction");
        GameTestLog.pass(helper, "storage_nested_transaction_abort", "");
    }

    private static void insertEverything(GameTestHelper helper, Transaction transaction) {
        ResourceHandler<ItemResource> items = MachineKit.capability(helper, Capabilities.Item.BLOCK, MACHINE, Direction.UP);
        ResourceHandler<FluidResource> fluids = MachineKit.capability(helper, Capabilities.Fluid.BLOCK, MACHINE, Direction.DOWN);
        EnergyHandler energy = MachineKit.capability(helper, Capabilities.Energy.BLOCK, MACHINE, Direction.UP);
        items.insert(ItemResource.of(Items.BUCKET), 1, transaction);
        fluids.insert(FluidResource.of(VEFluids.CRUDE_OIL_REG.get()), 1000, transaction);
        energy.insert(1000, transaction);
    }

    /**
     * T2.13: a full bucket in a bucket input slot empties into its tank, and an empty bucket fills from
     * an output tank.
     */
    private static void bucketSlots(GameTestHelper helper) {
        VETileEntity unit = MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        VEItemStackHandler inventory = unit.getInventory();
        inventory.setStack(0, new ItemStack(VEFluids.CRUDE_OIL_REG.get().getBucket()));
        unit.getRelationalTank(1).getTank().setFluid(new FluidStack(VEFluids.NAPHTHA_REG.get(), VETileEntity.DEFAULT_TANK_CAPACITY));
        inventory.setStack(2, new ItemStack(Items.BUCKET));
        helper.succeedWhen(() -> {
            helper.assertValueEqual(unit.getFluidStackFromTank(0).getAmount(), 1000, "crude oil in the input tank");
            helper.assertTrue(inventory.getStack(1).is(Items.BUCKET), "no empty bucket in the input tank's output slot");
            helper.assertValueEqual(unit.getFluidStackFromTank(1).getAmount(), VETileEntity.DEFAULT_TANK_CAPACITY - 1000, "naphtha left in the output tank");
            helper.assertTrue(inventory.getStack(3).is(VEFluids.NAPHTHA_REG.get().getBucket()), "no naphtha bucket");
            GameTestLog.note("storage_bucket_slots", "");
        });
    }

    /**
     * T2.14: a Battery Box charges a battery, moving the same energy out of its own store.
     */
    private static void batteryBoxCharges(GameTestHelper helper) {
        VETileEntity box = MachineKit.place(helper, MACHINE, VEBlocks.BATTERY_BOX.block().get());
        box.getEnergy().set(box.getEnergy().getCapacity());
        box.getInventory().setStack(0, new ItemStack(VEItems.MERCURY_BATTERY.get()));
        int boxBefore = box.getEnergy().getAmountAsInt();
        helper.runAfterDelay(20, () -> {
            ItemStack battery = box.getInventory().getStack(0).isEmpty() ? box.getInventory().getStack(1) : box.getInventory().getStack(0);
            int batteryEnergy = VEItemCapabilities.getEnergyHandler(battery).getAmountAsInt();
            helper.assertTrue(batteryEnergy > 0, "the battery was not charged");
            helper.assertValueEqual(boxBefore - box.getEnergy().getAmountAsInt(), batteryEnergy, "energy moved from the box");
            GameTestLog.pass(helper, "storage_battery_box_charges", batteryEnergy + " FE");
        });
    }

    /**
     * T2.15: a Crusher stalled on a full output slot finishes its recipe once the slot is emptied
     * through the capability.
     */
    private static void stalledRecipeResumes(GameTestHelper helper) {
        VERecipe recipe = RecipeKit.withUniqueInput(CrusherRecipe.RECIPE_TYPE);
        ItemStack input = RecipeKit.firstInput(recipe, 0);
        ItemStack result = recipe.getResult(0);

        VETileEntity crusher = MachineKit.place(helper, MACHINE, VEBlocks.CRUSHER.block().get());
        crusher.updatePacketFromGui(true, 1);
        crusher.getEnergy().set(crusher.getEnergy().getCapacity());
        crusher.getInventory().setStack(0, input);
        crusher.getInventory().setStack(1, result.copyWithCount(result.getMaxStackSize()));
        helper.onEachTick(() -> crusher.getEnergy().set(crusher.getEnergy().getCapacity()));

        helper.startSequence()
                .thenWaitUntil(() -> helper.assertTrue(crusher.getData(VEAttachments.IS_RECIPE_AWAITING_COMPLETE),
                        "the crusher is not stalled yet: " + crusher.getData(VEAttachments.COUNTER_LENGTH)))
                .thenExecute(() -> {
                    ResourceHandler<ItemResource> output = MachineKit.capability(helper, Capabilities.Item.BLOCK, MACHINE, Direction.DOWN);
                    try (Transaction transaction = Transaction.openRoot()) {
                        output.extract(ItemResource.of(result), 32, transaction);
                        transaction.commit();
                    }
                })
                .thenExecuteAfter(3, () -> {
                    helper.assertTrue(crusher.getInventory().getStack(1).getCount() > result.getMaxStackSize() - 32, "the crusher did not resume");
                    GameTestLog.note("storage_stalled_recipe_resumes", "");
                })
                .thenSucceed();
    }

    /**
     * T2.16: the owner operations clamp to what is there and what fits.
     */
    private static void ownerOperations(GameTestHelper helper) {
        VETileEntity unit = MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        VEItemStackHandler inventory = unit.getInventory();
        inventory.setStack(6, new ItemStack(Items.GRAVEL, 10));
        helper.assertValueEqual(inventory.take(6, 64).getCount(), 10, "items taken from a slot of 10");
        helper.assertTrue(inventory.getStack(6).isEmpty(), "slot not empty after taking everything");

        inventory.setStack(6, new ItemStack(Items.GRAVEL, 60));
        helper.assertValueEqual(inventory.add(6, new ItemStack(Items.GRAVEL, 10)).getCount(), 6, "items left over when adding 10 to 60");
        helper.assertValueEqual(inventory.getStack(6).getCount(), 64, "slot count after adding");
        helper.assertFalse(inventory.fits(6, new ItemStack(Items.GRAVEL)), "a full slot reports room");
        helper.assertFalse(inventory.fits(6, new ItemStack(Items.SAND)), "a different item fits");

        VEFluidTank tank = unit.getRelationalTank(0).getTank();
        helper.assertValueEqual(tank.fill(new FluidStack(Fluids.WATER, 10_000)), VETileEntity.DEFAULT_TANK_CAPACITY, "fluid filled past capacity");
        helper.assertValueEqual(tank.fill(new FluidStack(Fluids.LAVA, 10)), 0, "a different fluid filled a non-empty tank");
        tank.setFluid(FluidStack.EMPTY);
        helper.assertTrue(tank.drain(1000).isEmpty(), "fluid drained from an empty tank");

        VEEnergyStorage energy = unit.getEnergy();
        energy.set(10);
        energy.consumeEnergy(100);
        helper.assertValueEqual(energy.getAmountAsInt(), 0, "energy after consuming more than stored");
        energy.addEnergy(Integer.MAX_VALUE / 2);
        helper.assertValueEqual(energy.getAmountAsInt(), energy.getCapacity(), "energy after adding more than fits");
        GameTestLog.pass(helper, "storage_owner_operations", "");
    }

    /**
     * A drain without a tank index takes the fluid from every tank on the face that holds it.
     */
    private static void fluidMultiTankExtract(GameTestHelper helper) {
        VETileEntity unit = MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        VEFluidTank firstTank = new VEFluidTank(1000);
        VEFluidTank secondTank = new VEFluidTank(1000);
        firstTank.setFluid(new FluidStack(Fluids.WATER, 400));
        secondTank.setFluid(new FluidStack(Fluids.WATER, 1000));
        MultiFluidSlotWrapper fluids = new MultiFluidSlotWrapper(
                List.of(multiTank(firstTank), multiTank(secondTank)), unit);

        helper.assertValueEqual(drain(fluids, Fluids.WATER, 1200), 1200, "water extracted across both tanks");
        helper.assertValueEqual(firstTank.getFluidAmount(), 0, "water in the first tank after extract");
        helper.assertValueEqual(secondTank.getFluidAmount(), 200, "water in the second tank after extract");
        GameTestLog.pass(helper, "storage_fluid_multi_tank_extract", "");
    }

    /**
     * A fill without a tank index stays in a Fluid Mixer's first input tank once it holds that fluid,
     * so the second ingredient can still reach the second input tank.
     */
    private static void fluidMixerFillRouting(GameTestHelper helper) {
        VETileEntity mixer = MachineKit.place(helper, MACHINE, VEBlocks.FLUID_MIXER.block().get());
        helper.runAfterDelay(2, () -> {
            ResourceHandler<FluidResource> fluids = MachineKit.capability(helper, Capabilities.Fluid.BLOCK, MACHINE, Direction.DOWN);
            helper.assertTrue(fluids != null, "no fluid capability");
            Fluid hydrogen = VEFluids.HYDROGEN_REG.get();

            helper.assertValueEqual(fill(fluids, hydrogen, 5000), VETileEntity.DEFAULT_TANK_CAPACITY, "hydrogen accepted");
            helper.assertValueEqual(mixer.getFluidStackFromTank(0).getAmount(), VETileEntity.DEFAULT_TANK_CAPACITY, "hydrogen in the first input tank");
            helper.assertTrue(mixer.getFluidStackFromTank(1).isEmpty(), "hydrogen overflowed into the second input tank");

            helper.assertValueEqual(fill(fluids, hydrogen, 1000), 0, "hydrogen accepted into a full first input tank");
            helper.assertTrue(mixer.getFluidStackFromTank(1).isEmpty(), "hydrogen overflowed into the second input tank");

            helper.assertValueEqual(fill(fluids, VEFluids.NITROGEN_REG.get(), 1000), 1000, "nitrogen accepted");
            helper.assertValueEqual(mixer.getFluidStackFromTank(1).getAmount(), 1000, "nitrogen in the second input tank");
            helper.assertValueEqual(mixer.getFluidStackFromTank(0).getAmount(), VETileEntity.DEFAULT_TANK_CAPACITY, "hydrogen in the first input tank after nitrogen");
            GameTestLog.pass(helper, "storage_fluid_mixer_fill_routing", "");
        });
    }

    /**
     * An output tank whose side is disabled refuses a drain naming its fluid.
     */
    private static void fluidDrainDisabledSide(GameTestHelper helper) {
        VETileEntity unit = MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        VERelationalTank output = unit.getRelationalTank(1);
        output.getTank().setFluid(new FluidStack(VEFluids.NAPHTHA_REG.get(), 1000));
        unit.updateTankPacketFromGui(false, output.getSlotNum());
        ResourceHandler<FluidResource> fluids = MachineKit.capability(helper, Capabilities.Fluid.BLOCK, MACHINE, Direction.DOWN);

        GameTestLog.assertEqual(helper, drain(fluids, VEFluids.NAPHTHA_REG.get(), 500), 0, "naphtha drained through a disabled side");
        GameTestLog.assertEqual(helper, unit.getFluidStackFromTank(1).getAmount(), 1000, "naphtha in the output tank");
        GameTestLog.pass(helper, "storage_fluid_drain_disabled_side", "");
    }

    /**
     * A Fluid Electrolyzer's two output tanks drain by the fluid named, and a drain naming only an
     * amount skips the protected input tank.
     */
    private static void fluidElectrolyzerDrains(GameTestHelper helper) {
        VETileEntity electrolyzer = MachineKit.place(helper, MACHINE, VEBlocks.FLUID_ELECTROLYZER.block().get());
        Fluid oxygen = VEFluids.OXYGEN_REG.get();
        Fluid hydrogen = VEFluids.HYDROGEN_REG.get();
        for (VERelationalTank tank : electrolyzer.getRelationalTanks()) {
            electrolyzer.updateTankPacketFromGui(true, tank.getSlotNum());
        }
        electrolyzer.getRelationalTank(1).getTank().setFluid(new FluidStack(oxygen, 1000));
        electrolyzer.getRelationalTank(2).getTank().setFluid(new FluidStack(hydrogen, 1000));
        ResourceHandler<FluidResource> fluids = MachineKit.capability(helper, Capabilities.Fluid.BLOCK, MACHINE, Direction.DOWN);

        GameTestLog.assertEqual(helper, drain(fluids, hydrogen, 600), 600, "hydrogen drained");
        GameTestLog.assertEqual(helper, electrolyzer.getFluidStackFromTank(2).getAmount(), 400, "hydrogen left in the second output tank");
        GameTestLog.assertEqual(helper, electrolyzer.getFluidStackFromTank(1).getAmount(), 1000, "oxygen after draining hydrogen");

        GameTestLog.assertEqual(helper, drain(fluids, oxygen, 1500), 1000, "oxygen drained when asking for more than the tank holds");
        helper.assertTrue(electrolyzer.getFluidStackFromTank(1).isEmpty(), "oxygen left after draining the whole tank");

        electrolyzer.getRelationalTank(0).getTank().setFluid(new FluidStack(Fluids.WATER, 1000));
        electrolyzer.getRelationalTank(1).getTank().setFluid(new FluidStack(oxygen, 1000));
        withInputExtraction(false, () -> {
            ResourceStack<FluidResource> drained = drainAny(fluids, 500);
            helper.assertTrue(drained != null && drained.resource().is(oxygen), "a drain naming only an amount returned " + drained);
            GameTestLog.assertEqual(helper, drained.amount(), 500, "oxygen drained by amount");
        });
        GameTestLog.assertEqual(helper, electrolyzer.getFluidStackFromTank(0).getAmount(), 1000, "water in the protected input tank");
        GameTestLog.assertEqual(helper, electrolyzer.getFluidStackFromTank(2).getAmount(), 400, "hydrogen after a drain by amount");
        GameTestLog.pass(helper, "storage_fluid_electrolyzer_drains", "");
    }

    /**
     * A drain naming only an amount takes nothing from an output tank whose side is disabled.
     */
    private static void fluidDrainAmountRefusesDisabledSide(GameTestHelper helper) {
        VETileEntity electrolyzer = MachineKit.place(helper, MACHINE, VEBlocks.FLUID_ELECTROLYZER.block().get());
        VERelationalTank output = electrolyzer.getRelationalTank(1);
        output.getTank().setFluid(new FluidStack(VEFluids.OXYGEN_REG.get(), 1000));
        electrolyzer.updateTankPacketFromGui(false, output.getSlotNum());
        ResourceHandler<FluidResource> fluids = MachineKit.capability(helper, Capabilities.Fluid.BLOCK, MACHINE, Direction.DOWN);
        withInputExtraction(false, () -> {
            ResourceStack<FluidResource> drained = drainAny(fluids, 500);
            helper.assertTrue(drained == null, "a drain by amount took " + drained + " through a disabled side");
        });
        GameTestLog.assertEqual(helper, electrolyzer.getFluidStackFromTank(1).getAmount(), 1000, "oxygen in the output tank");
        GameTestLog.pass(helper, "storage_fluid_drain_amount_refuses_disabled_side", "");
    }

    /**
     * Requests for 0 mB move nothing.
     */
    private static void fluidZeroRequests(GameTestHelper helper) {
        VETileEntity unit = MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        Fluid crudeOil = VEFluids.CRUDE_OIL_REG.get();
        unit.getRelationalTank(0).getTank().setFluid(new FluidStack(crudeOil, 1000));
        unit.getRelationalTank(1).getTank().setFluid(new FluidStack(VEFluids.NAPHTHA_REG.get(), 1000));
        for (VERelationalTank tank : unit.getRelationalTanks()) {
            unit.updateTankPacketFromGui(true, tank.getSlotNum());
        }
        ResourceHandler<FluidResource> fluids = MachineKit.capability(helper, Capabilities.Fluid.BLOCK, MACHINE, Direction.DOWN);

        GameTestLog.assertEqual(helper, fill(fluids, crudeOil, 0), 0, "0 mB of crude oil accepted");
        GameTestLog.assertEqual(helper, drain(fluids, VEFluids.NAPHTHA_REG.get(), 0), 0, "naphtha returned by a drain of 0 mB");
        helper.assertTrue(drainAny(fluids, 0) == null, "a drain of 0 mB naming only an amount returned fluid");
        GameTestLog.assertEqual(helper, unit.getFluidStackFromTank(0).getAmount(), 1000, "crude oil in the input tank");
        GameTestLog.assertEqual(helper, unit.getFluidStackFromTank(1).getAmount(), 1000, "naphtha in the output tank");
        GameTestLog.pass(helper, "storage_fluid_zero_requests", "");
    }

    /**
     * A Distillation Unit's input tank 1 mB short of full takes 1 mB of a 2 mB offer.
     */
    private static void fluidFillLastMillibucket(GameTestHelper helper) {
        VETileEntity unit = MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        Fluid crudeOil = VEFluids.CRUDE_OIL_REG.get();
        unit.getRelationalTank(0).getTank().setFluid(new FluidStack(crudeOil, VETileEntity.DEFAULT_TANK_CAPACITY - 1));
        helper.runAfterDelay(2, () -> {
            ResourceHandler<FluidResource> fluids = MachineKit.capability(helper, Capabilities.Fluid.BLOCK, MACHINE, Direction.DOWN);
            GameTestLog.assertEqual(helper, fill(fluids, crudeOil, 2), 1, "crude oil accepted into a tank 1 mB short of full");
            GameTestLog.assertEqual(helper, unit.getFluidStackFromTank(0).getAmount(), VETileEntity.DEFAULT_TANK_CAPACITY, "crude oil in the input tank");
            GameTestLog.pass(helper, "storage_fluid_fill_last_millibucket", "");
        });
    }

    /**
     * An offer of the largest possible amount fills an empty Distillation Unit's input tank only to
     * its capacity.
     */
    private static void fluidFillLargestOffer(GameTestHelper helper) {
        VETileEntity unit = MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        helper.runAfterDelay(2, () -> {
            ResourceHandler<FluidResource> fluids = MachineKit.capability(helper, Capabilities.Fluid.BLOCK, MACHINE, Direction.DOWN);
            GameTestLog.assertEqual(helper, fill(fluids, VEFluids.CRUDE_OIL_REG.get(), Integer.MAX_VALUE), VETileEntity.DEFAULT_TANK_CAPACITY, "crude oil accepted from the largest offer");
            GameTestLog.assertEqual(helper, unit.getFluidStackFromTank(0).getAmount(), VETileEntity.DEFAULT_TANK_CAPACITY, "crude oil in the input tank");
            GameTestLog.pass(helper, "storage_fluid_fill_largest_offer", "");
        });
    }

    /**
     * A fill naming a Fluid Mixer's second input tank fills only that tank, and a fill naming its
     * output tank is refused.
     */
    private static void fluidFillNamedTank(GameTestHelper helper) {
        VETileEntity mixer = MachineKit.place(helper, MACHINE, VEBlocks.FLUID_MIXER.block().get());
        helper.runAfterDelay(2, () -> {
            ResourceHandler<FluidResource> fluids = MachineKit.capability(helper, Capabilities.Fluid.BLOCK, MACHINE, Direction.DOWN);
            GameTestLog.assertEqual(helper, fill(fluids, 1, VEFluids.NITROGEN_REG.get(), 1000), 1000, "nitrogen accepted into the second input tank");
            GameTestLog.assertEqual(helper, mixer.getFluidStackFromTank(1).getAmount(), 1000, "nitrogen in the second input tank");
            helper.assertTrue(mixer.getFluidStackFromTank(0).isEmpty(), "a fill naming the second input tank changed the first");

            GameTestLog.assertEqual(helper, fill(fluids, 2, VEFluids.AMMONIA_REG.get(), 1000), 0, "ammonia accepted into the output tank");
            helper.assertTrue(mixer.getFluidStackFromTank(2).isEmpty(), "a fill naming the output tank changed it");
            GameTestLog.pass(helper, "storage_fluid_fill_named_tank", "");
        });
    }

    /**
     * Fills and drains naming a tank before the first or after the last on a face move nothing.
     */
    private static void fluidOutOfRangeTransfers(GameTestHelper helper) {
        VETileEntity unit = MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        Fluid crudeOil = VEFluids.CRUDE_OIL_REG.get();
        unit.getRelationalTank(0).getTank().setFluid(new FluidStack(crudeOil, 1000));
        unit.updateTankPacketFromGui(true, unit.getRelationalTank(0).getSlotNum());
        helper.runAfterDelay(2, () -> {
            ResourceHandler<FluidResource> fluids = MachineKit.capability(helper, Capabilities.Fluid.BLOCK, MACHINE, Direction.DOWN);
            for (int tank : new int[]{-1, fluids.size()}) {
                GameTestLog.assertEqual(helper, fill(fluids, tank, crudeOil, 1000), 0, "crude oil accepted into tank " + tank);
                GameTestLog.assertEqual(helper, drain(fluids, tank, crudeOil, 1000), 0, "crude oil drained from tank " + tank);
            }
            GameTestLog.assertEqual(helper, unit.getFluidStackFromTank(0).getAmount(), 1000, "crude oil in the input tank");
            GameTestLog.pass(helper, "storage_fluid_out_of_range_transfers", "");
        });
    }

    /**
     * Asking whether a fluid is valid for a tank index the face does not have answers false.
     */
    private static void fluidOutOfRangeTankIndex(GameTestHelper helper) {
        MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        ResourceHandler<FluidResource> fluids = MachineKit.capability(helper, Capabilities.Fluid.BLOCK, MACHINE, Direction.DOWN);
        FluidResource crudeOil = FluidResource.of(VEFluids.CRUDE_OIL_REG.get());
        for (int tank : new int[]{-1, fluids.size()}) {
            helper.assertFalse(fluids.isValid(tank, crudeOil), "tank " + tank + " reported crude oil as valid");
        }
        GameTestLog.pass(helper, "storage_fluid_out_of_range_tank_index", "");
    }

    /**
     * A drain naming a fluid held in both of a Fluid Electrolyzer's output tanks takes from each in
     * tank order until the requested amount is met.
     */
    private static void fluidDrainSpansTanks(GameTestHelper helper) {
        VETileEntity electrolyzer = MachineKit.place(helper, MACHINE, VEBlocks.FLUID_ELECTROLYZER.block().get());
        for (int tank = 1; tank <= 2; tank++) {
            electrolyzer.updateTankPacketFromGui(true, electrolyzer.getRelationalTank(tank).getSlotNum());
        }
        electrolyzer.getRelationalTank(1).getTank().setFluid(new FluidStack(Fluids.WATER, 400));
        electrolyzer.getRelationalTank(2).getTank().setFluid(new FluidStack(Fluids.WATER, 1000));
        ResourceHandler<FluidResource> fluids = MachineKit.capability(helper, Capabilities.Fluid.BLOCK, MACHINE, Direction.DOWN);

        GameTestLog.assertEqual(helper, drain(fluids, Fluids.WATER, 1200), 1200, "water drained across both output tanks");
        helper.assertTrue(electrolyzer.getFluidStackFromTank(1).isEmpty(), "water left in the first output tank");
        GameTestLog.assertEqual(helper, electrolyzer.getFluidStackFromTank(2).getAmount(), 200, "water left in the second output tank");
        GameTestLog.pass(helper, "storage_fluid_drain_spans_tanks", "");
    }

    /**
     * Optional: a disabled input side refuses fills, as a disabled item slot refuses hoppers.
     */
    private static void fluidFillRefusesDisabledSide(GameTestHelper helper) {
        String name = "storage_fluid_fill_refuses_disabled_side";
        VETileEntity unit = MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        unit.updateTankPacketFromGui(false, unit.getRelationalTank(0).getSlotNum());
        helper.runAfterDelay(2, () -> {
            ResourceHandler<FluidResource> fluids = MachineKit.capability(helper, Capabilities.Fluid.BLOCK, MACHINE, Direction.DOWN);
            int accepted = fill(fluids, VEFluids.CRUDE_OIL_REG.get(), 1000);
            GameTestLog.value(name, "accepted", accepted);
            GameTestLog.assertEqual(helper, accepted, 0, "crude oil accepted through a disabled side");
            GameTestLog.pass(helper, name, "");
        });
    }

    /**
     * Optional: an empty Distillation Unit refuses Lava, which no Distillation Unit recipe takes.
     */
    private static void fluidEmptyMachineRefusesWrongFluid(GameTestHelper helper) {
        String name = "storage_fluid_empty_machine_refuses_wrong_fluid";
        MachineKit.place(helper, MACHINE, VEBlocks.DISTILLATION_UNIT.block().get());
        helper.runAfterDelay(2, () -> {
            ResourceHandler<FluidResource> fluids = MachineKit.capability(helper, Capabilities.Fluid.BLOCK, MACHINE, Direction.DOWN);
            int accepted = fill(fluids, Fluids.LAVA, 1000);
            GameTestLog.value(name, "accepted", accepted);
            GameTestLog.assertEqual(helper, accepted, 0, "lava accepted into an empty input tank");
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
        helper.runAfterDelay(2, () -> {
            fill(MachineKit.capability(helper, Capabilities.Fluid.BLOCK, MACHINE, Direction.DOWN), VEFluids.NITROGEN_REG.get(), 1000);
            GameTestLog.value(name, "first_input", mixer.getFluidStackFromTank(0).getAmount() + " " + mixer.getFluidStackFromTank(0).getFluid());
            GameTestLog.value(name, "second_input", mixer.getFluidStackFromTank(1).getAmount() + " " + mixer.getFluidStackFromTank(1).getFluid());
            helper.assertTrue(mixer.getFluidStackFromTank(0).isEmpty(), "nitrogen went into the first input tank");
            GameTestLog.assertEqual(helper, mixer.getFluidStackFromTank(1).getAmount(), 1000, "nitrogen in the second input tank");
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
        helper.runAfterDelay(2, () -> {
            fill(MachineKit.capability(helper, Capabilities.Fluid.BLOCK, MACHINE, Direction.DOWN), VEFluids.OXYGEN_REG.get(), 1000);
            GameTestLog.value(name, "fuel_tank", generator.getFluidStackFromTank(0).getAmount() + " " + generator.getFluidStackFromTank(0).getFluid());
            GameTestLog.value(name, "oxidizer_tank", generator.getFluidStackFromTank(1).getAmount() + " " + generator.getFluidStackFromTank(1).getFluid());
            helper.assertTrue(generator.getFluidStackFromTank(0).isEmpty(), "oxygen went into the fuel tank");
            GameTestLog.assertEqual(helper, generator.getFluidStackFromTank(1).getAmount(), 1000, "oxygen in the oxidizer tank");
            GameTestLog.pass(helper, name, "");
        });
    }

    private static int fill(ResourceHandler<FluidResource> fluids, Fluid fluid, int amount) {
        try (Transaction transaction = Transaction.openRoot()) {
            int filled = fluids.insert(FluidResource.of(fluid), amount, transaction);
            transaction.commit();
            return filled;
        }
    }

    private static int fill(ResourceHandler<FluidResource> fluids, int tank, Fluid fluid, int amount) {
        try (Transaction transaction = Transaction.openRoot()) {
            int filled = fluids.insert(tank, FluidResource.of(fluid), amount, transaction);
            transaction.commit();
            return filled;
        }
    }

    private static int drain(ResourceHandler<FluidResource> fluids, int tank, Fluid fluid, int amount) {
        try (Transaction transaction = Transaction.openRoot()) {
            int drained = fluids.extract(tank, FluidResource.of(fluid), amount, transaction);
            transaction.commit();
            return drained;
        }
    }

    /**
     * Drains the first fluid the face lets go of, as a pipe pulling any fluid does.
     */
    @Nullable
    private static ResourceStack<FluidResource> drainAny(ResourceHandler<FluidResource> fluids, int amount) {
        try (Transaction transaction = Transaction.openRoot()) {
            ResourceStack<FluidResource> drained = ResourceHandlerUtil.extractFirst(fluids, resource -> true, amount, transaction);
            transaction.commit();
            return drained;
        }
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

    private static VERelationalTank multiTank(VEFluidTank tank) {
        VERelationalTank relationalTank = new VERelationalTank(tank, 0, 0, TankType.BOTH, "test:enabled");
        relationalTank.setAllowAny(true);
        relationalTank.setIgnoreDirection(true);
        return relationalTank;
    }
}
