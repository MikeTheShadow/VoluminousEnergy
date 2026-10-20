package com.veteam.voluminousenergy.gametest;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.fluids.VEFluids;
import com.veteam.voluminousenergy.items.AmmoniumNitrateBucket;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.items.batteries.VEEnergyItem;
import com.veteam.voluminousenergy.items.tools.multitool.VEMultitoolItems;
import com.veteam.voluminousenergy.items.tools.multitool.bits.BitItem;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.VEDataComponents;
import com.veteam.voluminousenergy.util.VEItemCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Items: solid fuels, batteries, the multitool, and the Ammonium Nitrate Bucket (test plan T2.23 to
 * T2.28).
 */
public class ItemTests {

    private ItemTests() {
    }

    static void register(List<GameTestDefinition> tests) {
        tests.add(GameTestDefinition.large("item_solid_fuel_burn_times", 20, ItemTests::solidFuelBurnTimes));
        tests.add(GameTestDefinition.of("item_hopper_fuels_furnace", 60, ItemTests::hopperFuelsFurnace));
        tests.add(GameTestDefinition.of("item_battery_discharge_limits", 60, ItemTests::batteryDischargeLimits));
        tests.add(GameTestDefinition.of("item_multitool_abilities", 20, ItemTests::multitoolAbilities));
        tests.add(GameTestDefinition.of("item_multitool_fluid_tank", 20, ItemTests::multitoolFluidTank));
        tests.add(GameTestDefinition.of("item_ammonium_nitrate_bucket", 20, ItemTests::ammoniumNitrateBucket));
    }

    private static Map<Supplier<? extends Item>, Integer> fuels() {
        Map<Supplier<? extends Item>, Integer> fuels = new LinkedHashMap<>();
        fuels.put(VEItems.PETCOKE, 4000);
        fuels.put(VEItems.COALCOKE, 3200);
        fuels.put(VEItems.ROSIN, 800);
        fuels.put(VEItems.ANIMAL_FAT, 200);
        fuels.put(VEItems.TINY_COAL, 200);
        fuels.put(VEItems.TINY_CHARCOAL, 200);
        fuels.put(VEItems.TINY_COAL_COKE, 200);
        fuels.put(VEItems.TINY_PETCOKE, 200);
        fuels.put(VEItems.TINY_ROSIN, 200);
        return fuels;
    }

    /**
     * T2.23: each fuel burns for its 26.2 time in a furnace and half of it in a blast furnace.
     */
    private static void solidFuelBurnTimes(GameTestHelper helper) {
        Map<BlockPos, Integer> expected = new LinkedHashMap<>();
        int column = 0;
        for (Map.Entry<Supplier<? extends Item>, Integer> fuel : fuels().entrySet()) {
            BlockPos furnacePos = new BlockPos(1 + column % 7, 1, 1 + column / 7 * 2);
            BlockPos blastPos = furnacePos.above(2);
            light(helper, furnacePos, Blocks.FURNACE, fuel.getKey().get());
            light(helper, blastPos, Blocks.BLAST_FURNACE, fuel.getKey().get());
            expected.put(furnacePos, fuel.getValue());
            expected.put(blastPos, fuel.getValue() / 2);
            ++column;
        }
        helper.runAfterDelay(3, () -> {
            for (Map.Entry<BlockPos, Integer> entry : expected.entrySet()) {
                AbstractFurnaceBlockEntity furnace = helper.getBlockEntity(entry.getKey(), AbstractFurnaceBlockEntity.class);
                helper.assertValueEqual(litTotalTime(furnace), entry.getValue(), helper.getBlockState(entry.getKey()).getBlock() + " burn ticks at " + entry.getKey());
            }
            GameTestLog.pass(helper, "item_solid_fuel_burn_times", expected.size() + " furnaces");
        });
    }

    private static void light(GameTestHelper helper, BlockPos pos, Block furnaceBlock, Item fuel) {
        helper.setBlock(pos, furnaceBlock);
        AbstractFurnaceBlockEntity furnace = helper.getBlockEntity(pos, AbstractFurnaceBlockEntity.class);
        furnace.setItem(0, new ItemStack(Items.RAW_IRON, 20));
        furnace.setItem(1, new ItemStack(fuel));
    }

    private static int litTotalTime(AbstractFurnaceBlockEntity furnace) {
        try {
            Field field = AbstractFurnaceBlockEntity.class.getDeclaredField("litTotalTime");
            field.setAccessible(true);
            return field.getInt(furnace);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * T2.24: a hopper feeding a furnace's side puts Coal Coke in the fuel slot.
     */
    private static void hopperFuelsFurnace(GameTestHelper helper) {
        BlockPos furnacePos = new BlockPos(2, 1, 2);
        helper.setBlock(furnacePos, Blocks.FURNACE);
        BlockPos hopperPos = furnacePos.east();
        helper.setBlock(hopperPos, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.WEST));
        helper.getBlockEntity(hopperPos, HopperBlockEntity.class).setItem(0, new ItemStack(VEItems.COALCOKE.get(), 4));
        helper.succeedWhen(() -> {
            helper.assertTrue(helper.getBlockEntity(furnacePos, AbstractFurnaceBlockEntity.class).getItem(1).is(VEItems.COALCOKE.get()),
                    "coal coke not in the fuel slot");
            GameTestLog.note("item_hopper_fuels_furnace", "");
        });
    }

    /**
     * T2.25: a Battery Box drains a battery without moving more than the transfer limits allow in a
     * tick.
     */
    private static void batteryDischargeLimits(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 1, 2);
        VETileEntity box = MachineKit.place(helper, pos, VEBlocks.BATTERY_BOX.block().get());
        box.setSendsOutPower(true);
        VEEnergyItem batteryItem = (VEEnergyItem) VEItems.MERCURY_BATTERY.get();
        ItemStack battery = new ItemStack(batteryItem);
        battery.set(VEDataComponents.ENERGY.get(), batteryItem.getMaxEnergy());
        box.getInventory().setStack(0, battery);
        int limit = Math.min(Config.BATTERY_BOX_TRANSFER.get(), batteryItem.getMaxTransfer());

        int[] last = {box.getEnergy().getAmountAsInt()};
        helper.onEachTick(() -> {
            int now = box.getEnergy().getAmountAsInt();
            helper.assertTrue(now - last[0] <= limit, "more than " + limit + " FE moved in one tick");
            helper.assertTrue(now <= box.getEnergy().getCapacity(), "box above capacity");
            last[0] = now;
        });
        helper.runAfterDelay(20, () -> {
            helper.assertTrue(box.getEnergy().getAmountAsInt() > 0, "the box was not charged by the battery");
            ItemStack drained = box.getInventory().getStack(0).isEmpty() ? box.getInventory().getStack(1) : box.getInventory().getStack(0);
            helper.assertValueEqual(VEItemCapabilities.getEnergyHandler(drained).getAmountAsInt() + box.getEnergy().getAmountAsInt(),
                    batteryItem.getMaxEnergy(), "energy conserved between battery and box");
            GameTestLog.pass(helper, "item_battery_discharge_limits", "limit " + limit + " FE per tick");
        });
    }

    /**
     * T2.26: chain bits can no longer strip and scooper bits only douse, as 26.3 removed the other
     * abilities, and the multitool still does not strip logs or make paths.
     */
    private static void multitoolAbilities(GameTestHelper helper) {
        BitItem chain = VEMultitoolItems.IRON_CHAIN_BIT.get();
        BitItem scooper = VEMultitoolItems.IRON_SCOOPER_BIT.get();
        BitItem trimmer = VEMultitoolItems.IRON_TRIMMER_BIT.get();
        for (ItemAbility ability : ItemAbilities.DEFAULT_SHEARS_ACTIONS) {
            helper.assertFalse(chain.getBitItemData().canPerformAction(ability), "chain bit performs " + ability);
            helper.assertTrue(trimmer.getBitItemData().canPerformAction(ability), "trimmer bit cannot perform " + ability);
        }
        helper.assertTrue(scooper.getBitItemData().canPerformAction(ItemAbilities.SHOVEL_DOUSE), "scooper bit cannot douse");

        ServerPlayer player = MachineKit.mockPlayer(helper);
        BlockPos logPos = new BlockPos(1, 1, 1);
        BlockPos grassPos = new BlockPos(3, 1, 1);
        helper.setBlock(logPos, Blocks.OAK_LOG);
        helper.setBlock(grassPos, Blocks.GRASS_BLOCK);
        useMultitool(helper, player, chain, logPos);
        useMultitool(helper, player, scooper, grassPos);
        helper.assertBlockPresent(Blocks.OAK_LOG, logPos);
        helper.assertBlockPresent(Blocks.GRASS_BLOCK, grassPos);
        GameTestLog.pass(helper, "item_multitool_abilities", "");
    }

    private static void useMultitool(GameTestHelper helper, ServerPlayer player, BitItem bit, BlockPos pos) {
        ItemStack multitool = new ItemStack(VEMultitoolItems.MULTI_TOOL.get());
        multitool.set(VEDataComponents.ITEM_STACK_LIST_COMPONENT, List.of(new ItemStack(bit)));
        player.setItemInHand(InteractionHand.MAIN_HAND, multitool);
        BlockPos absolute = helper.absolutePos(pos);
        multitool.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(absolute), Direction.UP, absolute, false)));
    }

    /**
     * T2.27: the multitool's tank fills and drains through the item fluid capability, and the
     * contents persist in the item.
     */
    private static void multitoolFluidTank(GameTestHelper helper) {
        ItemStack multitool = new ItemStack(VEMultitoolItems.MULTI_TOOL.get());
        ResourceHandler<FluidResource> tank = ItemAccess.forStack(multitool).getCapability(Capabilities.Fluid.ITEM);
        FluidResource gasoline = FluidResource.of(VEFluids.GASOLINE_REG.get());
        try (Transaction transaction = Transaction.openRoot()) {
            helper.assertValueEqual(tank.insert(gasoline, 1000, transaction), 1000, "gasoline inserted");
            transaction.commit();
        }
        try (Transaction transaction = Transaction.openRoot()) {
            helper.assertValueEqual(tank.extract(gasoline, 400, transaction), 400, "gasoline extracted");
            transaction.commit();
        }
        ResourceHandler<FluidResource> reread = ItemAccess.forStack(multitool.copy()).getCapability(Capabilities.Fluid.ITEM);
        helper.assertValueEqual(reread.getAmountAsInt(0), 600, "gasoline stored in the item");
        GameTestLog.pass(helper, "item_multitool_fluid_tank", "");
    }

    /**
     * T2.28: the Ammonium Nitrate Bucket grows a crop it is used on.
     */
    private static void ammoniumNitrateBucket(GameTestHelper helper) {
        ItemStack bucket = new ItemStack(VEFluids.AMMONIUM_NITRATE_SOLUTION_BUCKET_REG.get());
        // NeoForge gives the fluid capability only to plain BucketItems, on 26.2 as on 26.3.
        GameTestLog.value("item_ammonium_nitrate_bucket", "has_fluid_capability",
                ItemAccess.forStack(bucket).getCapability(Capabilities.Fluid.ITEM) != null);

        ServerPlayer player = MachineKit.mockPlayer(helper);
        BlockPos farmland = new BlockPos(2, 1, 2);
        BlockPos wheat = farmland.above();
        helper.setBlock(farmland, Blocks.FARMLAND);
        helper.setBlock(wheat, Blocks.WHEAT);
        // useOn also hands back an empty bucket through the player's connection, which a mock player lacks.
        AmmoniumNitrateBucket.applyFert(bucket, helper.getLevel(), helper.absolutePos(wheat), player);
        helper.assertTrue(helper.getBlockState(wheat).getValue(BlockStateProperties.AGE_7) > 0, "the wheat did not grow");
        GameTestLog.pass(helper, "item_ammonium_nitrate_bucket", "");
    }
}
