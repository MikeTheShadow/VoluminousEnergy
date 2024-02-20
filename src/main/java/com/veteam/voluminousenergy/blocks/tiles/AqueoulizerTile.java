package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.VEContainers;
import com.veteam.voluminousenergy.recipe.AqueoulizerRecipe;
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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AqueoulizerTile extends VETileEntity {
    private final ItemStackHandler inventory = new VEItemStackHandler(this,6);

    public List<VERelationalTank> fluidManagers = new ArrayList<>() {{
        add(new VERelationalTank(new FluidTank(DEFAULT_TANK_CAPACITY), 0, 0,  TankType.INPUT, "inputTank:input_tank_gui"));
        add(new VERelationalTank(new FluidTank(DEFAULT_TANK_CAPACITY), 1, 0,  TankType.OUTPUT, "outputTank:output_tank_gui"));
    }};

    public List<VESlotManager> slotManagers = new ArrayList<>(){{
        add(new VESlotManager(0, Direction.UP, true, SlotType.FLUID_INPUT, 1, 0));
        add(new VESlotManager(1, Direction.DOWN, true, SlotType.FLUID_OUTPUT));
        add(new VESlotManager(2, Direction.NORTH, true, SlotType.FLUID_INPUT, 3, 1));
        add(new VESlotManager(3, Direction.SOUTH, true, SlotType.FLUID_OUTPUT));
        add(new VESlotManager(4, 0, Direction.EAST, true, SlotType.INPUT));
    }};

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    public AqueoulizerTile(BlockPos pos, BlockState state) {
        super(VEBlocks.AQUEOULIZER_TILE.get(), pos, state, AqueoulizerRecipe.RECIPE_TYPE);
    }

    @Nonnull
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return VEContainers.AQUEOULIZER_FACTORY.create(i, level, worldPosition, playerInventory, playerEntity);
    }

    @Override
    public @Nonnull List<VERelationalTank> getRelationalTanks() {
        return fluidManagers;
    }

    @Override
    public @Nonnull List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }
}
