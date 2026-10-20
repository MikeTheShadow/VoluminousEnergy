package com.veteam.voluminousenergy.gametest;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.VEContainerFactory;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.items.tools.multitool.VEMultitoolItems;
import com.veteam.voluminousenergy.recipe.CrusherRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.util.VEDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.BitSet;
import java.util.List;

/**
 * Machine menus on the server, with a mock player (test plan T2.19 to T2.22).
 */
public class MenuTests {
    private static final BlockPos MACHINE = new BlockPos(2, 1, 2);

    private MenuTests() {
    }

    static void register(List<GameTestDefinition> tests) {
        tests.add(GameTestDefinition.large("menu_slots_match_machines", 20, MenuTests::slotsMatchMachines));
        tests.add(GameTestDefinition.of("menu_quick_move", 40, MenuTests::quickMove));
        tests.add(GameTestDefinition.of("menu_slot_write_back", 20, MenuTests::slotWriteBack));
        tests.add(GameTestDefinition.of("menu_tooling_station_listener", 20, MenuTests::toolingStationListener));
        tests.add(GameTestDefinition.of("menu_experience_listener", 20, MenuTests::experienceListener));
    }

    private static AbstractContainerMenu open(VETileEntity tile, ServerPlayer player) {
        return tile.createMenu(1, player.getInventory(), player);
    }

    /**
     * T2.19: every machine menu has one slot per inventory slot, each backed by the machine's own
     * inventory at its index, followed by the player's 36 slots.
     */
    private static void slotsMatchMachines(GameTestHelper helper) {
        ServerPlayer player = MachineKit.mockPlayer(helper);
        List<Block> machines = MachineKit.machineBlocks();
        int checked = 0;
        for (int index = 0; index < machines.size(); index++) {
            BlockPos pos = new BlockPos(1 + index % 7, 1, 1 + index / 7 % 7);
            VETileEntity tile = MachineKit.place(helper, pos, machines.get(index));
            String name = MachineKit.name(machines.get(index));
            AbstractContainerMenu menu = open(tile, player);
            if (menu == null || tile.getInventory() == null) {
                continue;
            }
            int size = tile.getInventory().size();
            helper.assertValueEqual(menu.slots.size(), size + 36, name + " menu slot count");
            BitSet indices = new BitSet();
            for (int slot = 0; slot < size; slot++) {
                Slot menuSlot = menu.getSlot(slot);
                helper.assertTrue(menuSlot instanceof VEContainerFactory.VESlot veSlot && veSlot.getResourceHandler() == tile.getInventory(),
                        name + " menu slot " + slot + " is not backed by the machine");
                indices.set(menuSlot.getSlotIndex());
            }
            helper.assertValueEqual(indices.cardinality(), size, name + " distinct inventory indices");
            ++checked;
        }
        GameTestLog.pass(helper, "menu_slots_match_machines", checked + " menus");
    }

    /**
     * T2.20: shift-clicking moves ore from the player into a Crusher's input, and a result from its
     * output back to the player.
     */
    private static void quickMove(GameTestHelper helper) {
        VETileEntity crusher = MachineKit.place(helper, MACHINE, VEBlocks.CRUSHER.block().get());
        ServerPlayer player = MachineKit.mockPlayer(helper);
        helper.runAfterDelay(2, () -> {
            AbstractContainerMenu menu = open(crusher, player);
            int machineSlots = crusher.getInventory().size();
            player.getInventory().setItem(10, new ItemStack(Items.IRON_ORE, 5));
            menu.quickMoveStack(player, machineSlots + 1);
            helper.assertValueEqual(crusher.getInventory().getStack(0).getCount(), 5, "iron ore moved into the crusher");
            helper.assertTrue(player.getInventory().getItem(10).isEmpty(), "iron ore left in the player inventory");

            crusher.getInventory().setStack(1, new ItemStack(Items.GRAVEL, 7));
            menu.quickMoveStack(player, 1);
            helper.assertTrue(crusher.getInventory().getStack(1).isEmpty(), "gravel left in the crusher output");
            helper.assertValueEqual(player.getInventory().countItem(Items.GRAVEL), 7, "gravel in the player inventory");
            GameTestLog.pass(helper, "menu_quick_move", "");
        });
    }

    /**
     * T2.21: a stack set through a menu slot lands in the machine.
     */
    private static void slotWriteBack(GameTestHelper helper) {
        VETileEntity crusher = MachineKit.place(helper, MACHINE, VEBlocks.CRUSHER.block().get());
        AbstractContainerMenu menu = open(crusher, MachineKit.mockPlayer(helper));
        menu.getSlot(0).set(new ItemStack(Items.IRON_ORE, 3));
        helper.assertValueEqual(crusher.getInventory().getStack(0).getCount(), 3, "iron ore in the crusher after a slot write");
        ItemStack shown = menu.getSlot(0).getItem();
        shown.shrink(1);
        menu.getSlot(0).setChanged();
        helper.assertValueEqual(crusher.getInventory().getStack(0).getCount(), 2, "iron ore after editing the slot's stack and marking it changed");
        GameTestLog.pass(helper, "menu_slot_write_back", "");
    }

    /**
     * T2.22: putting a multitool into the Tooling Station lays out its bits, and taking it out packs
     * them back into the tool.
     */
    private static void toolingStationListener(GameTestHelper helper) {
        VETileEntity station = MachineKit.place(helper, MACHINE, VEBlocks.TOOLING_STATION.block().get());
        ServerPlayer player = MachineKit.mockPlayer(helper);
        AbstractContainerMenu menu = open(station, player);
        ItemStack multitool = new ItemStack(VEMultitoolItems.MULTI_TOOL.get());
        multitool.set(VEDataComponents.ITEM_STACK_LIST_COMPONENT, List.of(new ItemStack(VEMultitoolItems.IRON_DRILL_BIT.get())));

        menu.getSlot(2).set(multitool);
        helper.assertTrue(station.getInventory().getStack(3).is(VEMultitoolItems.IRON_DRILL_BIT.get()), "the drill bit was not laid out in the station");

        Slot toolSlot = menu.getSlot(2);
        ItemStack taken = toolSlot.remove(1);
        toolSlot.onTake(player, taken);
        for (int slot = 3; slot < 7; slot++) {
            helper.assertTrue(station.getInventory().getStack(slot).isEmpty(), "bit slot " + slot + " not emptied when the multitool was taken");
        }
        List<ItemStack> bits = taken.getOrDefault(VEDataComponents.ITEM_STACK_LIST_COMPONENT, List.of());
        helper.assertValueEqual(bits.size(), 1, "bits packed into the taken multitool");
        GameTestLog.pass(helper, "menu_tooling_station_listener", "");
    }

    /**
     * T2.22: taking a result from a Crusher output awards the recipes the Crusher used.
     */
    private static void experienceListener(GameTestHelper helper) {
        VETileEntity crusher = MachineKit.place(helper, MACHINE, VEBlocks.CRUSHER.block().get());
        ServerPlayer player = MachineKit.mockPlayer(helper);
        VERecipe recipe = VERecipe.getCachedRecipes(CrusherRecipe.RECIPE_TYPE).getFirst();
        crusher.recordRecipeUsed(recipe);
        crusher.getInventory().setStack(1, recipe.getResult(0).copy());

        Slot output = open(crusher, player).getSlot(1);
        ItemStack taken = output.remove(64);
        output.onTake(player, taken);
        helper.assertTrue(crusher.getRecipesUsed().isEmpty(), "used recipes not awarded when the result was taken");
        GameTestLog.pass(helper, "menu_experience_listener", "");
    }
}
