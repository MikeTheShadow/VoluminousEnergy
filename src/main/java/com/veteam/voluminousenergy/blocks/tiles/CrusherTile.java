package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CrusherContainer;
import com.veteam.voluminousenergy.recipe.CrusherRecipe;
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

public class CrusherTile extends VETileEntity implements IVEPoweredTileEntity, IVECountable {

    public List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(new VESlotManager(0,0, Direction.UP, true, SlotType.INPUT));
        add(new VESlotManager(1,0, Direction.DOWN, true, SlotType.OUTPUT));
        add(new VESlotManager(2,1, Direction.NORTH, true, SlotType.OUTPUT));
    }};

    public CrusherTile(BlockPos pos, BlockState state) {
        super(VEBlocks.CRUSHER_TILE.get(), pos, state, CrusherRecipe.RECIPE_TYPE);
    }

    public ItemStackHandler inventory = createHandler(4);

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new CrusherContainer(i, level, worldPosition, playerInventory, playerEntity);
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
        return Config.CRUSHER_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.CRUSHER_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.CRUSHER_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 3;
    }
}
