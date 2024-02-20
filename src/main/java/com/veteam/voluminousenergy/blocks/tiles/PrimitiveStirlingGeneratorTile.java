package com.veteam.voluminousenergy.blocks.tiles;


import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.VEContainers;
import com.veteam.voluminousenergy.recipe.StirlingGeneratorRecipe;
import com.veteam.voluminousenergy.sounds.VESounds;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
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

public class PrimitiveStirlingGeneratorTile extends VETileEntity {

    List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(new VESlotManager(0, Direction.UP, true, SlotType.INPUT));
    }};

    private final ItemStackHandler inventory = this.createHandler();

    public PrimitiveStirlingGeneratorTile(BlockPos pos, BlockState state) {
        super(VEBlocks.PRIMITIVE_STIRLING_GENERATOR_TILE.get(), pos, state, null);
        this.energyCap = this.getCapability(ForgeCapabilities.ENERGY);
        this.maxPower = Config.PRIMITIVE_STIRLING_GENERATOR_MAX_POWER.get();
        this.generateAmount = Config.PRIMITIVE_STIRLING_GENERATOR_GENERATE.get();
    }

    @NotNull LazyOptional<IEnergyStorage> energyCap;

    private final int maxPower;
    private final int generateAmount;

    @Override
    public void tick() {
        updateClients();
        if (counter > 0) {
            counter--;
            if (energyCap.map(IEnergyStorage::getEnergyStored).orElse(0) < maxPower) {
                energy.addEnergy(generateAmount); //Amount of energy to add per tick
            }
            if (++sound_tick == 19) {
                sound_tick = 0;
                if (Config.PLAY_MACHINE_SOUNDS.get()) {
                    level.playSound(null, this.getBlockPos(), VESounds.GENERAL_MACHINE_NOISE, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
            setChanged();
        } else {
            ItemStack stack = inventory.getStackInSlot(0);
            StirlingGeneratorRecipe recipe = RecipeUtil.getStirlingGeneratorRecipe(level, stack.copy());

            if (recipe != null) {
                inventory.extractItem(0, 1, false);
                counter = recipe.getProcessTime();
                length = counter;
                setChanged();
            }
        }
        sendOutPower();
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                ItemStack referenceStack = stack.copy();
                referenceStack.setCount(64);
                return StirlingGeneratorRecipe.getCachedRecipes(StirlingGeneratorRecipe.RECIPE_TYPE).stream().anyMatch(r -> r.getIngredient(0).test(stack));
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
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        return VEContainers.PRIMITIVE_STIRLING_GENERATOR_FACTORY.create(i, level, worldPosition, playerInventory, playerEntity);
    }

    @Nonnull
    @Override
    public ItemStackHandler getInventoryHandler() {
        return this.inventory;
    }

    @Nonnull
    @Override
    public List<VESlotManager> getSlotManagers() {
        return this.slotManagers;
    }


    // TODO check if these methods are identical to super. If they are remove them
    public int progressBurnCounterPX(int px) {
        if (counter == 0) {
            return 0;
        } else {
            return (px * (((counter * 100) / length))) / 100;
        }
    }

    public int progressCounterPercent() {
        if (counter != 0 && length != 0) return (int) (100 - (((float) counter / (float) length) * 100));
        return 0;
    }

    public int getEnergyRate() {
        return 40;
    }
}