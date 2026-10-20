package com.veteam.voluminousenergy.gametest;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.blocks.tiles.inventory.VEItemStackHandler;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.VERelationalTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Helpers shared by the machine gametests: placing machines, reading their contents as plain text
 * for comparison, and reaching their capabilities.
 */
public class MachineKit {

    private MachineKit() {
    }

    /**
     * Every block the mod registers as a machine, in registration order.
     */
    public static List<Block> machineBlocks() {
        List<Block> blocks = new ArrayList<>();
        for (VEBlocks.RegistryWithName entry : VEBlocks.REGISTERED_BLOCKS) {
            Block block = entry.block().get();
            if ((entry.hasInventory() || entry.hasEnergy() || entry.hasFluids()) && block instanceof EntityBlock) {
                blocks.add(block);
            }
        }
        return blocks;
    }

    public static VETileEntity place(GameTestHelper helper, BlockPos pos, Block block) {
        helper.setBlock(pos, block.defaultBlockState());
        return helper.getBlockEntity(pos, VETileEntity.class);
    }

    /**
     * A server player that has not logged in, so nothing is sent to its missing client.
     */
    public static ServerPlayer mockPlayer(GameTestHelper helper) {
        return (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
    }

    public static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }

    @Nullable
    public static <T> T capability(GameTestHelper helper, BlockCapability<T, @Nullable Direction> capability, BlockPos pos, @Nullable Direction side) {
        return helper.getLevel().getCapability(capability, helper.absolutePos(pos), side);
    }

    /**
     * Describes everything a machine saves, so two machines can be compared as strings.
     */
    public static String describe(VETileEntity tile) {
        StringBuilder text = new StringBuilder();
        VEItemStackHandler inventory = tile.getInventory();
        if (inventory != null) {
            for (int slot = 0; slot < inventory.size(); slot++) {
                ItemStack stack = inventory.getStack(slot);
                text.append("slot ").append(slot).append('=').append(stack.isEmpty() ? "empty" : stack.getCount() + " " + stack.getItem() + " " + stack.getComponentsPatch()).append('\n');
            }
        }
        for (VERelationalTank tank : tile.getRelationalTanks()) {
            FluidStack fluid = tank.getTank().getFluid();
            text.append("tank ").append(tank.getTankName()).append('=').append(fluid.isEmpty() ? "empty" : fluid.getAmount() + " " + fluid.getFluid())
                    .append(" side ").append(tank.getSideStatus()).append(' ').append(tank.getSideDirection()).append('\n');
        }
        VEEnergyStorage energy = tile.getEnergy();
        if (energy != null) {
            text.append("energy=").append(energy.getAmountAsInt()).append(" production=").append(energy.getProduction())
                    .append(" consumption=").append(energy.getConsumption()).append(" upgrade=").append(energy.getUpgradeSlotId()).append('\n');
        }
        for (VESlotManager manager : tile.getSlotManagers()) {
            text.append("manager ").append(manager.getSlotNum()).append(' ').append(manager.getStatus()).append(' ').append(manager.getDirection()).append('\n');
        }
        text.append("sends_out_power=").append(tile.sendsOutPower()).append('\n');
        return text.toString();
    }

    /**
     * Puts something different from a new machine's state into every slot, tank, side setting, and
     * the energy store.
     */
    public static void fill(VETileEntity tile) {
        VEItemStackHandler inventory = tile.getInventory();
        if (inventory != null) {
            for (int slot = 0; slot < inventory.size(); slot++) {
                inventory.setStack(slot, new ItemStack(Items.COBBLESTONE, slot + 1));
            }
        }
        List<VERelationalTank> tanks = tile.getRelationalTanks();
        for (int tank = 0; tank < tanks.size(); tank++) {
            tanks.get(tank).getTank().setFluid(new FluidStack(Fluids.WATER, 100 * (tank + 1)));
            tile.updateTankPacketFromGui(true, tanks.get(tank).getSlotNum());
            tile.updateTankPacketFromGui(Direction.EAST.get3DDataValue(), tanks.get(tank).getSlotNum());
        }
        VEEnergyStorage energy = tile.getEnergy();
        if (energy != null && energy.getCapacity() > 0) {
            energy.set(energy.getCapacity() / 3);
        }
        for (VESlotManager manager : tile.getSlotManagers()) {
            tile.updatePacketFromGui(!manager.getStatus(), manager.getSlotNum());
            tile.updatePacketFromGui(Direction.WEST.get3DDataValue(), manager.getSlotNum());
        }
    }
}
