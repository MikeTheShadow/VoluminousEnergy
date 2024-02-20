package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.VEContainers;
import com.veteam.voluminousenergy.recipe.DistillationRecipe;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.VERelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DistillationUnitTile extends VEMultiBlockTileEntity {
    public List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(new VESlotManager(0, Direction.UP, false, SlotType.FLUID_INPUT,1,0));
        add(new VESlotManager(1, Direction.DOWN, false, SlotType.FLUID_OUTPUT));
        add(new VESlotManager(2, Direction.UP, false, SlotType.FLUID_INPUT,3,1));
        add(new VESlotManager(3, Direction.DOWN, false, SlotType.FLUID_OUTPUT));
        add(new VESlotManager(4, Direction.UP, false, SlotType.FLUID_INPUT,5,2));
        add(new VESlotManager(5, Direction.DOWN, false, SlotType.FLUID_OUTPUT));
        add(new VESlotManager(6, 0, Direction.DOWN, false, SlotType.OUTPUT));
    }};

    public List<VERelationalTank> fluidManagers = new ArrayList<>() {{
        add(new VERelationalTank(new FluidTank(DEFAULT_TANK_CAPACITY), 0, 0, TankType.INPUT, "inputTank:input_tank_gui"));
        add(new VERelationalTank(new FluidTank(DEFAULT_TANK_CAPACITY), 1, 0, TankType.OUTPUT, "outputTank0:output_tank_0_gui"));
        add(new VERelationalTank(new FluidTank(DEFAULT_TANK_CAPACITY), 2, 1, TankType.OUTPUT, "outputTank1:output_tank_1_gui"));
    }};

    private byte tick = 19;

    public DistillationUnitTile(BlockPos pos, BlockState state) {
        super(VEBlocks.DISTILLATION_UNIT_TILE.get(), pos, state, DistillationRecipe.RECIPE_TYPE);
    }

    @Override
    public void tick() {
        updateClients();
        tick++;
        if (tick == 20) {
            tick = 0;
            validity = isMultiBlockValid(VEBlocks.ALUMINUM_MACHINE_CASING_BLOCK.get());
        }
        if (!(validity)) {
            return;
        }
        super.tick();
    }

    @NotNull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return VEContainers.DISTILLATION_UNIT_FACTORY.create(i, level, worldPosition, playerInventory, playerEntity);
    }

    @Nullable
    @Override
    public ItemStackHandler getInventoryHandler() {
        throw new NotImplementedException("Missing impl for Distillation tile inventory handler!");
    }

    @Override
    public @NotNull List<VERelationalTank> getRelationalTanks() {
        return fluidManagers;
    }

    public boolean getMultiblockValidity() {
        return validity;
    }
}