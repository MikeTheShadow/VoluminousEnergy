package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.VEContainers;
import com.veteam.voluminousenergy.recipe.ElectrolyzerRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ElectrolyzerTile extends VETileEntity implements IVEPoweredTileEntity, IVECountable {

    List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(new VESlotManager(0,0, Direction.UP, true, SlotType.INPUT));
        add(new VESlotManager(1,1, Direction.WEST, true, SlotType.INPUT));
        add(new VESlotManager(2,0, Direction.DOWN, true, SlotType.OUTPUT));
        add(new VESlotManager(3,1, Direction.NORTH, true, SlotType.OUTPUT));
        add(new VESlotManager(4,2, Direction.SOUTH, true, SlotType.OUTPUT));
        add(new VESlotManager(5,3, Direction.EAST, true, SlotType.OUTPUT));
    }};

    public ElectrolyzerTile(BlockPos pos, BlockState state) {
        super(VEBlocks.ELECTROLYZER_TILE.get(), pos, state, ElectrolyzerRecipe.RECIPE_TYPE);
    }

    public ItemStackHandler inventory = createHandler(7);

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return VEContainers.ELECTROLYZER_FACTORY.create(i, level, worldPosition, playerInventory, playerEntity);
    }

    @Override
    public @Nonnull ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @NotNull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }


    @Override
    public int getMaxPower() {
        return Config.ELECTROLYZER_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.ELECTROLYZER_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.ELECTROLYZER_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 6;
    }
}
