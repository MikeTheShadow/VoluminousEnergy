package com.veteam.voluminousenergy.tools.sidemanager;

import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.VEIOPort;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class VESlotManager implements VEIOPort {
    private final int slot;
    private final AtomicReference<Direction> side = new AtomicReference<>();
    private final SlotType slotType;
    private final String nbtName;
    private final Set<Item> allowedItems = new HashSet<>();

    public VESlotManager(int slotNum, @Nullable Direction direction, SlotType slotType) {
        this.side.set(direction);
        this.slot = slotNum;
        this.slotType = slotType;
        this.nbtName = slotType.getNBTName(slotNum);
    }

    public void setDirection(@Nullable Direction direction) {
        this.side.set(direction);
    }

    public void setDirection(int direction) {
        this.side.set(directionFromInt(direction));
    }

    public @Nullable Direction getDirection() {
        return this.side.get();
    }

    public int getSlotNum() {
        return slot;
    }

    public String getHoverName() {
        return slotType.getHoverName();
    }

    public String getNbtName() {
        return nbtName;
    }

    public void write(CompoundTag nbt) {
        nbt.putInt(nbtName + "_direction", isAssigned() ? getDirection().get3DDataValue() : -1);
    }

    public void read(CompoundTag nbt) {
        int sideInt = nbt.getInt(nbtName + "_direction");
        setDirection(directionFromInt(sideInt));
    }

    @Override
    public boolean isFluid() {
        return false;
    }

    @Override
    public boolean canPush() {
        return slotType == SlotType.OUTPUT;
    }

    @Override
    public boolean canPull() {
        return slotType == SlotType.INPUT;
    }

    public SlotType getSlotType() {
        return slotType;
    }

    public @Nullable Direction directionFromInt(int sideInt) {
        // 1 = top, 0 = bottom, 2 = north, 3 = south, 4 = west, 5 = east
        return switch (sideInt) {
            case -1 -> null;
            case 0 -> Direction.DOWN;
            case 1 -> Direction.UP;
            case 2 -> Direction.NORTH;
            case 3 -> Direction.SOUTH;
            case 4 -> Direction.WEST;
            case 5 -> Direction.EAST;
            default -> throw new NotImplementedException("Unknown side: " + sideInt);
        };
    }

    // This gives the raw item to be used in manipulation. If you wish to use it for something else .copy() it
    public ItemStack getItem(ItemStackHandler handler) {
        return handler.getStackInSlot(this.slot);
    }

    public void setItem(ItemStack stack, ItemStackHandler handler) {
        handler.setStackInSlot(this.slot, stack.copy());
    }

    public Set<Item> getAllowedItems() {
        return allowedItems;
    }

    public void addAllowedItem(Item item) {
        this.allowedItems.add(item);
    }
}
