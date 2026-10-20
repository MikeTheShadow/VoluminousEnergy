package com.veteam.voluminousenergy.gametest;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.VERelationalTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Save compatibility with 26.2 (test plan T2.3 and the layout the T4 harness fills machines from).
 */
public class CompatTests {
    private static final String FIXTURES = "autotest_fixtures/26.2";

    private CompatTests() {
    }

    static void register(List<GameTestDefinition> tests) {
        tests.add(GameTestDefinition.large("compat_layout", 20, CompatTests::layout));
        tests.add(GameTestDefinition.large("storage_26_2_fixtures", 20, CompatTests::fixtures));
    }

    /**
     * Logs, for every machine, what it stores and under which keys, as one JSON object per machine.
     */
    private static void layout(GameTestHelper helper) {
        List<Block> machines = MachineKit.machineBlocks();
        for (int index = 0; index < machines.size(); index++) {
            Block block = machines.get(index);
            VETileEntity tile = MachineKit.place(helper, new BlockPos(1 + index % 7, 1 + index / 49, 1 + index / 7 % 7), block);
            StringJoiner tanks = new StringJoiner(",", "[", "]");
            for (VERelationalTank tank : tile.getRelationalTanks()) {
                tanks.add("{\"name\":\"" + tank.getTankName() + "\",\"prefix\":\"" + tank.getNBTPrefix() + "\"}");
            }
            StringJoiner managers = new StringJoiner(",", "[", "]");
            for (VESlotManager manager : tile.getSlotManagers()) {
                managers.add("\"" + manager.getNbtName() + "\"");
            }
            int size = tile.getInventory() == null ? 0 : tile.getInventory().size();
            int capacity = tile.getEnergy() == null ? 0 : tile.getEnergy().getCapacity();
            GameTestLog.value("compat_layout", MachineKit.name(block),
                    "{\"size\":" + size + ",\"capacity\":" + capacity + ",\"tanks\":" + tanks + ",\"managers\":" + managers + "}");
        }
        GameTestLog.pass(helper, "compat_layout", machines.size() + " machines");
    }

    /**
     * T2.3: machine data saved by the 26.2 build loads into 26.3 machines with the contents the T4
     * harness gave them: Cobblestone in slot i numbering i + 1, Water in tank i holding 100 (i + 1)
     * mB, and the saved energy.
     */
    private static void fixtures(GameTestHelper helper) {
        Map<Identifier, Resource> resources = helper.getLevel().getServer().getResourceManager()
                .listResources(FIXTURES, path -> path.getPath().endsWith(".snbt"));
        if (resources.isEmpty()) {
            GameTestLog.value("storage_26_2_fixtures", "fixtures", 0);
            GameTestLog.pass(helper, "storage_26_2_fixtures", "no fixtures captured yet; T4 captures them");
            return;
        }
        List<String> checked = new ArrayList<>();
        int index = 0;
        for (Map.Entry<Identifier, Resource> fixture : resources.entrySet()) {
            String path = fixture.getKey().getPath();
            String name = path.substring(path.lastIndexOf('/') + 1, path.length() - ".snbt".length());
            Block block = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(VoluminousEnergy.MODID, name));
            CompoundTag saved = read(helper, fixture.getValue());
            BlockPos pos = new BlockPos(1 + index % 7, 1 + index / 49, 1 + index / 7 % 7);
            helper.setBlock(pos, block.defaultBlockState());
            BlockEntity loaded = BlockEntity.loadStatic(helper.absolutePos(pos), block.defaultBlockState(), saved, helper.getLevel().registryAccess());
            helper.assertTrue(loaded instanceof VETileEntity, name + " did not load as a machine");
            VETileEntity tile = (VETileEntity) loaded;
            if (tile.getInventory() != null) {
                for (int slot = 0; slot < tile.getInventory().size(); slot++) {
                    ItemStack stack = tile.getInventory().getStack(slot);
                    helper.assertTrue(stack.is(Items.COBBLESTONE) && stack.getCount() == slot + 1, name + " slot " + slot + " holds " + stack);
                }
            }
            List<VERelationalTank> tanks = tile.getRelationalTanks();
            for (int tank = 0; tank < tanks.size(); tank++) {
                helper.assertTrue(tanks.get(tank).getTank().getFluid().is(Fluids.WATER) && tanks.get(tank).getTank().getFluidAmount() == 100 * (tank + 1),
                        name + " tank " + tank + " holds " + tanks.get(tank).getTank().getFluid());
            }
            if (tile.getEnergy() != null) {
                helper.assertValueEqual(tile.getEnergy().getAmountAsInt(), saved.getCompoundOrEmpty("energy").getIntOr("energy", -1), name + " energy");
            }
            checked.add(name);
            ++index;
        }
        GameTestLog.value("storage_26_2_fixtures", "fixtures", checked.size());
        GameTestLog.pass(helper, "storage_26_2_fixtures", checked.size() + " machines");
    }

    private static CompoundTag read(GameTestHelper helper, Resource resource) {
        try (Reader reader = resource.openAsReader()) {
            StringBuilder text = new StringBuilder();
            char[] buffer = new char[8192];
            for (int read = reader.read(buffer); read >= 0; read = reader.read(buffer)) {
                text.append(buffer, 0, read);
            }
            return TagParser.parseCompoundFully(text.toString());
        } catch (IOException | CommandSyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
