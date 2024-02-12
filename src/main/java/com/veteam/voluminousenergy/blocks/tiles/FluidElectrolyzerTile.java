package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.VEContainers;
import com.veteam.voluminousenergy.recipe.FluidElectrolyzerRecipe;
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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FluidElectrolyzerTile extends VEFluidTileEntity implements IVEPoweredTileEntity, IVECountable {


    public List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(new VESlotManager(0, Direction.UP, true, SlotType.FLUID_INPUT, 1, 0));
        add(new VESlotManager(1, Direction.DOWN, true, SlotType.FLUID_OUTPUT));
        add(new VESlotManager(2, Direction.EAST, true, SlotType.FLUID_INPUT, 3, 1));
        add(new VESlotManager(3, Direction.WEST, true, SlotType.FLUID_OUTPUT));
        add(new VESlotManager(4, Direction.NORTH, true, SlotType.FLUID_INPUT, 5, 2));
        add(new VESlotManager(5, Direction.SOUTH, true, SlotType.FLUID_OUTPUT));
    }};

    public List<RelationalTank> fluidManagers = new ArrayList<>() {{
        add(new RelationalTank(new FluidTank(TANK_CAPACITY), 0, 0, TankType.INPUT, "inputTank:input_tank_gui"));
        add(new RelationalTank(new FluidTank(TANK_CAPACITY), 1, 0, TankType.OUTPUT, "outputTank0:output_tank_0_gui"));
        add(new RelationalTank(new FluidTank(TANK_CAPACITY), 2, 1, TankType.OUTPUT, "outputTank1:output_tank_1_gui"));
    }};

    public FluidElectrolyzerTile(BlockPos pos, BlockState state) {
        super(VEBlocks.FLUID_ELECTROLYZER_TILE.get(), pos, state, FluidElectrolyzerRecipe.RECIPE_TYPE);
    }

    public ItemStackHandler inventory = createHandler(7);

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @Override
    public int getUpgradeSlotId() {
        return 6;
    }

    @Nonnull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return VEContainers.FLUID_ELECTROLYZER_FACTORY.create(i, level, worldPosition, playerInventory, playerEntity);
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return fluidManagers;
    }

    @Override
    public int getMaxPower() {
        return Config.FLUID_ELECTROLYZER_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.FLUID_ELECTROLYZER_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.FLUID_ELECTROLYZER_TRANSFER.get();
    }

    @Override
    public RecipeType<? extends Recipe<?>> getRecipeType() {
        return FluidElectrolyzerRecipe.RECIPE_TYPE;
    }
}
