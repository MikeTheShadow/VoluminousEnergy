package com.veteam.voluminousenergy.blocks.tiles;


import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.blocks.containers.PrimitiveStirlingGeneratorContainer;
import com.veteam.voluminousenergy.recipe.RecipeCache;
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

public class PrimitiveStirlingGeneratorTile extends VETileEntity implements IVEPowerGenerator, IVECountable {

    public VESlotManager slotManager = new VESlotManager(0, Direction.UP, true, SlotType.INPUT);

    List<VESlotManager> slotManagers = new ArrayList<>() {{
        add(slotManager);
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
                energy.ifPresent(e -> e.addEnergy(generateAmount)); //Amount of energy to add per tick
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

    public static int receiveEnergy(BlockEntity tileEntity, Direction from, int maxReceive) {
        return tileEntity.getCapability(ForgeCapabilities.ENERGY, from).map(handler ->
                handler.receiveEnergy(maxReceive, false)).orElse(0);
    }

    private void sendOutPower() {
        energy.ifPresent(energy -> {
            for (Direction dir : Direction.values()) {
                BlockEntity tileEntity = level.getBlockEntity(getBlockPos().relative(dir));
                Direction opposite = dir.getOpposite();
                if (tileEntity != null) {
                    // If less energy stored then max transfer send the all the energy stored rather than the max transfer amount
                    int smallest = Math.min(Config.PRIMITIVE_STIRLING_GENERATOR_SEND.get(), energy.getEnergyStored());
                    int received = receiveEnergy(tileEntity, opposite, smallest);
                    energy.consumeEnergy(received);
                    if (energy.getEnergyStored() <= 0) {
                        break;
                    }
                }
            }
        });
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
                return RecipeCache.getRecipesWithoutLevelDangerous(StirlingGeneratorRecipe.RECIPE_TYPE).stream().anyMatch(r -> r.getIngredient(0).test(stack));
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if(isItemValid(slot,stack)) return stack;
                return super.insertItem(slot,stack,simulate);
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        return new PrimitiveStirlingGeneratorContainer(i, level, worldPosition, playerInventory, playerEntity);
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
    public int progressCounterPX(int px) {
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

    @Override
    public void updatePacketFromGui(boolean status, int slotId) {
        if (slotId == slotManager.getSlotNum()) slotManager.setStatus(status);
    }

    public void updatePacketFromGui(int direction, int slotId) {
        if (slotId == slotManager.getSlotNum()) slotManager.setDirection(direction);
    }

    @Override
    public int getMaxPower() {
        return Config.PRIMITIVE_STIRLING_GENERATOR_MAX_POWER.get();
    }

    @Override
    public int getPowerUsage() {
        return 0;
    }

    @Override
    public int getTransferRate() {
        return Config.PRIMITIVE_STIRLING_GENERATOR_SEND.get();
    }

    @Override
    public int getUpgradeSlotId() {
        return 0;
    }
}