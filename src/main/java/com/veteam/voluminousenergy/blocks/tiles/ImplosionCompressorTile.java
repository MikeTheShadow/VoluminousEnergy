package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.ImplosionCompressorContainer;
import com.veteam.voluminousenergy.recipe.ImplosionCompressorRecipe;
import com.veteam.voluminousenergy.recipe.RecipeCache;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import java.util.concurrent.atomic.AtomicReference;

public class ImplosionCompressorTile extends VETileEntity implements IVEPoweredTileEntity,IVECountable {

    public VESlotManager inputSlotManager = new VESlotManager(0,0,Direction.UP,true, SlotType.INPUT);
    public VESlotManager gunpowderSlotManager = new VESlotManager(1,1, Direction.EAST, true,SlotType.INPUT);
    public VESlotManager outputSlotManager = new VESlotManager(2,0, Direction.DOWN, true,SlotType.OUTPUT);

    public List<VESlotManager> slotManagers = new ArrayList<>() {{
       add(inputSlotManager);
       add(gunpowderSlotManager);
       add(outputSlotManager);
    }};

    public ImplosionCompressorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.IMPLOSION_COMPRESSOR_TILE.get(), pos, state,ImplosionCompressorRecipe.RECIPE_TYPE);
    }

    private final ItemStackHandler inventory = createHandler(4);

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity)
    {
        return new ImplosionCompressorContainer(i,level,worldPosition,playerInventory,playerEntity);
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
        return Config.IMPLOSION_COMPRESSOR_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return Config.IMPLOSION_COMPRESSOR_POWER_USAGE.get();
    }

    @Override
    public int getTransferRate() {
        return Config.IMPLOSION_COMPRESSOR_TRANSFER.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 3;
    }

    @Override
    public RecipeType<? extends Recipe<?>> getRecipeType() {
        return ImplosionCompressorRecipe.RECIPE_TYPE;
    }
}
