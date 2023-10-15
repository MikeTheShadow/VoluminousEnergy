package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.DistillationUnitContainer;
import com.veteam.voluminousenergy.recipe.DistillationRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
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
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DistillationUnitTile extends VEMultiBlockTileEntity implements IVEPoweredTileEntity, IVECountable {
    public List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(new VESlotManager(0, Direction.UP, false, SlotType.FLUID_INPUT,1,0));
        add(new VESlotManager(1, Direction.DOWN, false, SlotType.FLUID_OUTPUT));
        add(new VESlotManager(2, Direction.UP, false, SlotType.FLUID_INPUT,3,1));
        add(new VESlotManager(3, Direction.DOWN, false, SlotType.FLUID_OUTPUT));
        add(new VESlotManager(4, Direction.UP, false, SlotType.FLUID_INPUT,5,2));
        add(new VESlotManager(5, Direction.DOWN, false, SlotType.FLUID_OUTPUT));
        add(new VESlotManager(6, 0, Direction.DOWN, false, SlotType.OUTPUT));
    }};

    public List<RelationalTank> fluidManagers = new ArrayList<>() {{
        add(new RelationalTank(new FluidTank(TANK_CAPACITY), 0, 0, TankType.INPUT, "inputTank:input_tank_gui"));
        add(new RelationalTank(new FluidTank(TANK_CAPACITY), 1, 0, TankType.OUTPUT, "outputTank0:output_tank_0_gui"));
        add(new RelationalTank(new FluidTank(TANK_CAPACITY), 2, 1, TankType.OUTPUT, "outputTank1:output_tank_1_gui"));
    }};

    private byte tick = 19;

    public ItemStackHandler inventory = createHandler(8);

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
        return new DistillationUnitContainer(i, level, worldPosition, playerInventory, playerEntity);
    }

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @Override
    public @NotNull List<RelationalTank> getRelationalTanks() {
        return fluidManagers;
    }

    public boolean getMultiblockValidity() {
        return validity;
    }

    @Override
    public int getMaxPower() {
        return Config.DISTILLATION_UNIT_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.DISTILLATION_UNIT_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.DISTILLATION_UNIT_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 7;
    }
}