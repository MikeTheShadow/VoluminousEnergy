package com.veteam.voluminousenergy.gametest;

import com.veteam.voluminousenergy.items.VEItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Items: solid fuel burn times in vanilla furnaces (spec items/solid-fuels).
 */
public class ItemTests {
    private static final int FURNACES_PER_ROW = 5;

    private ItemTests() {
    }

    static void register(List<GameTestDefinition> tests) {
        tests.add(GameTestDefinition.of("item_solid_fuel_burn_times", 20, ItemTests::solidFuelBurnTimes));
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
        fuels.put(VEItems.CACTCHAR, 1600);
        fuels.put(VEItems.CANE_CHARCOAL, 1600);
        fuels.put(VEItems.TINY_CACTCHAR, 200);
        fuels.put(VEItems.TINY_CANE_CHARCOAL, 200);
        return fuels;
    }

    /**
     * Each solid fuel burns for its fixed time in a furnace and half of it in a blast furnace.
     */
    private static void solidFuelBurnTimes(GameTestHelper helper) {
        Map<BlockPos, Integer> expected = new LinkedHashMap<>();
        int column = 0;
        for (Map.Entry<Supplier<? extends Item>, Integer> fuel : fuels().entrySet()) {
            BlockPos furnacePos = new BlockPos(column % FURNACES_PER_ROW, 1, column / FURNACES_PER_ROW);
            BlockPos blastPos = furnacePos.above();
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
}
