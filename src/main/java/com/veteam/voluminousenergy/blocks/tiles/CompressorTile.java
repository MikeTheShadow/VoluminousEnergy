package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.CompressorContainer;
import com.veteam.voluminousenergy.recipe.CompressorRecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CompressorTile extends VETileEntity implements IVEPoweredTileEntity, IVECountable {

    public List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(new VESlotManager(0,0, Direction.UP, true, SlotType.INPUT));
        add(new VESlotManager(1,0, Direction.DOWN, true, SlotType.OUTPUT));
    }};

    public CompressorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.COMPRESSOR_TILE.get(), pos, state, null);
    }

    private final ItemStackHandler inventory = createHandler(3);

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new CompressorContainer(i, level, worldPosition, playerInventory, playerEntity);
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
        return Config.COMPRESSOR_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.COMPRESSOR_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.COMPRESSOR_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 2;
    }

    @Override
    public RecipeType<? extends Recipe<?>> getRecipeType() {
        return CompressorRecipe.RECIPE_TYPE;
    }
}
