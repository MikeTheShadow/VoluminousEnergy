package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.VEContainers;
import com.veteam.voluminousenergy.recipe.StirlingGeneratorRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class StirlingGeneratorTile extends VETileEntity {
    List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(new VESlotManager(0, Direction.UP, true, SlotType.INPUT));
    }};

    private int energyRate;
    private final ItemStackHandler inventory = this.createHandler();

    public StirlingGeneratorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.STIRLING_GENERATOR_TILE.get(), pos, state, null);
        this.energyCap = this.getCapability(ForgeCapabilities.ENERGY);
        this.maxPower = Config.STIRLING_GENERATOR_MAX_POWER.get();
    }

    StirlingGeneratorRecipe recipe;

    @NotNull LazyOptional<IEnergyStorage> energyCap;

    private final int maxPower;

    @Override
    public void tick() {
        updateClients();
        validateRecipe();
        sendOutPower();
    }

    @Override
    public void validateRecipe() {
        if (!this.isRecipeDirty) {
            return;
        }
        ItemStack input = this.inventory.getStackInSlot(0).copy();
        recipe = RecipeUtil.getStirlingGeneratorRecipe(level, input.copy());
    }

    public static int receiveEnergy(BlockEntity tileEntity, Direction from, int maxReceive) {
        return tileEntity.getCapability(ForgeCapabilities.ENERGY, from).map(handler ->
                handler.receiveEnergy(maxReceive, false)).orElse(0);
    }

    void sendOutPower() {
        for (Direction dir : Direction.values()) {
            BlockEntity tileEntity = level.getBlockEntity(getBlockPos().relative(dir));
            Direction opposite = dir.getOpposite();
            if (tileEntity != null) {
                // If less energy stored then max transfer send the all the energy stored rather than the max transfer amount
                int smallest = Math.min(Config.STIRLING_GENERATOR_SEND.get(), energy.getEnergyStored());
                int received = receiveEnergy(tileEntity, opposite, smallest);
                energy.consumeEnergy(received);
                if (energy.getEnergyStored() <= 0) {
                    break;
                }
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        energyRate = tag.getInt("energy_rate");
        super.load(tag);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        tag.putInt("energy_rate", energyRate);
        super.saveAdditional(tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
        super.onDataPacket(net, pkt);
    }

    private ItemStackHandler createHandler() {
        StirlingGeneratorTile tile = this;
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                tile.markRecipeDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                ItemStack referenceStack = stack.copy();
                referenceStack.setCount(64);
                return VERecipe.getCachedRecipes(StirlingGeneratorRecipe.RECIPE_TYPE).stream().anyMatch(r -> r.getIngredient(0).test(referenceStack));
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (!isItemValid(slot, stack)) return stack;
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        return VEContainers.STIRLING_GENERATOR_FACTORY.create(i, level, worldPosition, playerInventory, playerEntity);
    }

    @Nullable
    @Override
    public ItemStackHandler getInventoryHandler() {
        return inventory;
    }

    @NotNull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return slotManagers;
    }

    public int progressBurnCounterPX(int px) {
        if (counter == 0) {
            return 0;
        } else {
            return (px * (((counter * 100) / length))) / 100;
        }
    }
}
