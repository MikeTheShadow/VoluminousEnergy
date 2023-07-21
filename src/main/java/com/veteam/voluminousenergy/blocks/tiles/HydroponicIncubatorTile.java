package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.HydroponicIncubatorContainer;
import com.veteam.voluminousenergy.recipe.HydroponicIncubatorRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class HydroponicIncubatorTile extends VEFluidTileEntity implements IVEPoweredTileEntity, IVECountable {

    private final ItemStackHandler inventory = createHandler(8);

    List<VESlotManager> slotManagers = new ArrayList<>() {
        {
            add(new VESlotManager(0, Direction.UP, true, SlotType.FLUID_INPUT,1,0));
            add(new VESlotManager(1, Direction.DOWN, true, SlotType.FLUID_OUTPUT));
            add(new VESlotManager(2,0, Direction.NORTH, true, SlotType.INPUT));
            add(new VESlotManager(3,0, Direction.NORTH, true, SlotType.OUTPUT));
            add(new VESlotManager(4,1, Direction.NORTH, true, SlotType.OUTPUT));
            add(new VESlotManager(5,2, Direction.NORTH, true, SlotType.OUTPUT));
            add(new VESlotManager(6,3, Direction.NORTH, true, SlotType.OUTPUT));
        }
    };

    RelationalTank inputTank = new RelationalTank(new FluidTank(TANK_CAPACITY), 0,0, TankType.INPUT, "inputTank:input_tank_gui");

    List<RelationalTank> fluidManagers = new ArrayList<>() {
        {
            add(inputTank);
        }
    };

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @Override
    public @Nonnull List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }

    public HydroponicIncubatorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.HYDROPONIC_INCUBATOR_TILE.get(), pos, state,HydroponicIncubatorRecipe.RECIPE_TYPE);
        inputTank.setAllowAny(true);
    }

    @Override
    public @Nonnull CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag);
        return compoundTag;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new HydroponicIncubatorContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    public FluidStack getFluidStackFromTank(int num) {
        if (num == 0) {
            return inputTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    public RelationalTank getInputTank() {
        return this.inputTank;
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return this.fluidManagers;
    }

    @Override
    public int getMaxPower() {
        return Config.HYDROPONIC_INCUBATOR_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.HYDROPONIC_INCUBATOR_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.HYDROPONIC_INCUBATOR_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 7;
    }
}
