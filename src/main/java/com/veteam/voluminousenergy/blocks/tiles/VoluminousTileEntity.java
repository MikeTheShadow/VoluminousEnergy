package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.MultiFluidSlotWrapper;
import com.veteam.voluminousenergy.util.MultiSlotWrapper;
import com.veteam.voluminousenergy.util.RelationalTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.logging.Logger;

public abstract class VoluminousTileEntity extends BlockEntity implements MenuProvider {

    public VoluminousTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, VoluminousTileEntity voluminousTile) {
        voluminousTile.tick();
    }

    int counter = 0;
    int length = 0;

    public abstract void tick();

    /**
     * If a player is within 16 blocks send them an update packet
     */
    public void updateClients() {
        if (level == null) return;
        level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 1); // notifyBlockUpdate --> sendBlockUpdated
    }

    protected int calculateCounter(int processTime, ItemStack upgradeStack) {
        if (upgradeStack.getItem() == VEItems.QUARTZ_MULTIPLIER) {
            int count = upgradeStack.getCount();
            if (count == 4) {
                return 5;
            } else {
                return (-45 * upgradeStack.getCount()) + processTime;
            }
        }
        return processTime;
    }

    protected int consumptionMultiplier(int consumption, ItemStack upgradeStack) {
        if (upgradeStack.getItem() == VEItems.QUARTZ_MULTIPLIER) {
            int count = upgradeStack.getCount();
            if (count == 4) {
                return consumption * 16;
            } else if (count == 3) {
                return consumption * 8;
            } else if (count == 2) {
                return consumption * 4;
            } else if (count == 1) {
                return consumption * 2;
            }
        }
        return consumption;
    }

    // Simplified call to get the stored energy from the Energy Capability that a TE might have
    protected int getEnergyStored() {
        return this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    public void updatePacketFromGui(boolean status, int slotId) {
        for (VESlotManager slot : getSlotManagers()) {
            if (slotId == slot.getSlotNum()) {
                slot.setStatus(status);
                return;
            }
        }
    }

    public void updatePacketFromGui(int direction, int slotId) {
        for (VESlotManager slot : getSlotManagers()) {
            if (slotId == slot.getSlotNum()) {
                slot.setDirection(direction);
                return;
            }
        }
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag);
        return compoundTag;
    }

    @Override
    public void load(CompoundTag tag) {
        CompoundTag inv = tag.getCompound("inv");

        ItemStackHandler handler = getInventoryHandler();

        if(handler != null) {
            handler.deserializeNBT(inv);
        }

        LazyOptional<VEEnergyStorage> energy = getEnergy();
        if(energy != null) energy.ifPresent(h -> h.deserializeNBT(tag));

        if(tag.contains("counter")) counter = tag.getInt("counter");
        if(tag.contains("length")) length = tag.getInt("length");

        for(VESlotManager manager : getSlotManagers()) {
            manager.read(tag);
        }

        super.load(tag);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        ItemStackHandler handler = getInventoryHandler();
        if(handler != null) {
            CompoundTag compound = ((INBTSerializable<CompoundTag>)handler).serializeNBT();
            tag.put("inv",compound);
        }
        LazyOptional<VEEnergyStorage> energy = getEnergy();
        if(energy != null) energy.ifPresent(h -> h.serializeNBT(tag));

        for(VESlotManager manager : getSlotManagers()) {
            manager.write(tag);
        }

        if (counter > 0) tag.putInt("counter", counter);
        if (length > 0) tag.putInt("length", length);
    }

    /**
     * @param cap  Base capability
     * @param side Base Direction
     * @param <T>  T the type of capability
     * @return A LazyOptional of Optional of type T matching the capability passed into this
     */
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {

        ItemStackHandler inventory = getInventoryHandler();
        LazyOptional<VEEnergyStorage> energy = getEnergy();
        List<VESlotManager> itemManagers = getSlotManagers();

        if (inventory != null && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == null) return LazyOptional.of(() -> inventory).cast();
            Direction modifiedSide = normalizeDirection(side);
            List<VESlotManager> managerList = itemManagers
                    .stream()
                    .filter(manager -> manager.getStatus()
                            && manager.getDirection().get3DDataValue() == modifiedSide.get3DDataValue())
                    .toList();
            if (managerList.size() == 0) return super.getCapability(cap, side);
            MultiSlotWrapper slotWrapper = new MultiSlotWrapper(inventory, managerList);
            return LazyOptional.of(() -> slotWrapper).cast();
        } else if (cap == CapabilityEnergy.ENERGY && energy != null) {
            return energy.cast();
        } else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && side != null && this instanceof VEFluidTileEntity veFluidTileEntity) {
            Direction modifiedSide = normalizeDirection(side);
            List<RelationalTank> relationalTanks = veFluidTileEntity.getRelationalTanks().stream().filter(manager -> manager.getSideStatus() && manager.getSideDirection().get3DDataValue() == modifiedSide.get3DDataValue() || manager.isIgnoreDirection()).toList();
            if (relationalTanks.size() == 0) return super.getCapability(cap, side);
            MultiFluidSlotWrapper slotWrapper = new MultiFluidSlotWrapper(relationalTanks);
            return LazyOptional.of(() -> slotWrapper).cast();
        } else {
            return super.getCapability(cap, side);
        }
    }

    @Override
    public @Nonnull Component getDisplayName() {
        return new TextComponent(getType().getRegistryName().getPath());
    }

    /**
     * Make this null if the item does not have a menu
     * @param id the ID
     * @param playerInventory inventory of the player
     * @param player the player themselves
     * @return a new AbstractContainerMenu corresponding to this
     */
    @Nullable
    @Override
    public abstract AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player);

    public abstract @Nullable
    ItemStackHandler getInventoryHandler();

    /**
     * Important note. If the entity has no slot managers return a new ArrayList otherwise this will crash
     *
     * @return A not null List<VESlotManager> list
     */
    public abstract @Nonnull List<VESlotManager> getSlotManagers();

    public abstract @Nullable
    LazyOptional<VEEnergyStorage> getEnergy();

    public Direction normalizeDirection(Direction direction) {
        Direction currentDirection = this.getBlockState().getValue(BlockStateProperties.FACING);
        int directionInt = direction.get3DDataValue();
        if (directionInt == 0 || directionInt == 1) return direction;
        Direction rotated = currentDirection;
        for (int i = 0; i < 4; i++) {
            rotated = rotated.getClockWise();
            direction = direction.getClockWise();
            if (rotated.get3DDataValue() == 2) break;
        }
        return direction.getClockWise().getClockWise();
    }

}
