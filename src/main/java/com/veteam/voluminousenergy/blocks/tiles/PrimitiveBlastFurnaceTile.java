package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveBlastFurnaceContainer;
import com.veteam.voluminousenergy.recipe.PrimitiveBlastFurnaceRecipe;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


public class PrimitiveBlastFurnaceTile extends VETileEntity implements IVECountable,IVEPoweredTileEntity {

    public VESlotManager inputSm = new VESlotManager(0,0,Direction.UP,true, SlotType.INPUT);
    public VESlotManager outputSm = new VESlotManager(1,0, Direction.DOWN,true,SlotType.OUTPUT);

    List<VESlotManager> slotManagers = new ArrayList<>() {{
       add(inputSm);
       add(outputSm);
    }};

    public PrimitiveBlastFurnaceTile(BlockPos pos, BlockState state) {
        super(VEBlocks.PRIMITIVE_BLAST_FURNACE_TILE.get(), pos, state,PrimitiveBlastFurnaceRecipe.RECIPE_TYPE);
    }

    private final ItemStackHandler inventory = createHandler(3);


    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
        super.onDataPacket(net, pkt);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return new PrimitiveBlastFurnaceContainer(i,level,worldPosition,playerInventory,playerEntity);
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

    @Nullable
    @Override
    public LazyOptional<VEEnergyStorage> getEnergy() {
        return null;
    }

    public int getCounter(){ return counter; }

    @Override
    public RecipeType<? extends Recipe<?>> getRecipeType() {
        return PrimitiveBlastFurnaceRecipe.RECIPE_TYPE;
    }


    @Override
    public int getMaxPower() {
        return 0;
    }

    @Override
    public int getPowerUsage() {
        return 0;
    }

    @Override
    public int getTransferRate() {
        return 0;
    }

    @Override
    public int getUpgradeSlotId() {
        return 2;
    }
}