package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.ElectrolyzerContainer;
import com.veteam.voluminousenergy.recipe.ElectrolyzerRecipe;
import com.veteam.voluminousenergy.recipe.RecipeCache;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

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
        return new ElectrolyzerContainer(i, level, worldPosition, playerInventory, playerEntity);
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
