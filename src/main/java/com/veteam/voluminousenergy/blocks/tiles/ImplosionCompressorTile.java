package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.VEContainers;
import com.veteam.voluminousenergy.recipe.ImplosionCompressorRecipe;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ImplosionCompressorTile extends VETileEntity {

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

    private final ItemStackHandler inventory = new VEItemStackHandler(this,4);

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity)
    {
        return VEContainers.IMPLOSION_COMPRESSOR_FACTORY.create(i, level, worldPosition, playerInventory, playerEntity);
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
    public RecipeType<? extends Recipe<?>> getRecipeType() {
        return ImplosionCompressorRecipe.RECIPE_TYPE;
    }
}
