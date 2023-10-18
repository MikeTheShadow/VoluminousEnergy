package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.BlastFurnaceContainer;
import com.veteam.voluminousenergy.recipe.IndustrialBlastingRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlastFurnaceTile extends VEMultiBlockTileEntity implements IVEPoweredTileEntity, IVECountable {

    List<VESlotManager> slotManagers = new ArrayList<>() {
        {
            add(new VESlotManager(0, Direction.UP, false, SlotType.FLUID_INPUT, 1, 0));
            add(new VESlotManager(1, Direction.DOWN, false, SlotType.FLUID_OUTPUT));
            add(new VESlotManager(2,0, Direction.EAST, false, SlotType.INPUT));
            add(new VESlotManager(3,1, Direction.WEST, false, SlotType.INPUT));
            add(new VESlotManager(4,0, Direction.NORTH, false, SlotType.OUTPUT));
        }
    };

    RelationalTank[] fluidManagers = new RelationalTank[]{
            new RelationalTank(new FluidTank(TANK_CAPACITY), 0,0, TankType.INPUT, "heatTank:heat_tank_gui")
    };

    public ItemStackHandler inventory = createHandler(6);

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @Override
    public @Nonnull List<VESlotManager> getSlotManagers() {
        return this.slotManagers;
    }

    public BlastFurnaceTile(BlockPos pos, BlockState state) {
        super(VEBlocks.BLAST_FURNACE_TILE.get(), pos, state, IndustrialBlastingRecipe.RECIPE_TYPE);
        fluidManagers[0].setAllowAny(true);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new BlastFurnaceContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    public FluidStack getFluidStackFromTank(int num) {
        if (num == 0) {
            return fluidManagers[num].getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public boolean getMultiblockValidity() {
        return validity;
    }

    public int getTemperatureKelvin() {
        return fluidManagers[0].getTank().getFluid().getRawFluid().getFluidType().getTemperature();
    }

    public int getTemperatureCelsius() {
        return getTemperatureKelvin() - 273;
    }

    public int getTemperatureFahrenheit() {
        return (int) ((getTemperatureKelvin() - 273) * 1.8) + 32;
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return List.of(fluidManagers);
    }

    @Override
    public int getMaxPower() {
        return Config.BLAST_FURNACE_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.BLAST_FURNACE_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.BLAST_FURNACE_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 5;
    }

    @Override
    public Block getCasingBlock() {
        return VEBlocks.TITANIUM_MACHINE_CASING_BLOCK.get();
    }
}