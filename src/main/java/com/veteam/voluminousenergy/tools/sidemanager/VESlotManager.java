package com.veteam.voluminousenergy.tools.sidemanager;

import com.veteam.voluminousenergy.util.SlotType;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.NotImplementedException;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class VESlotManager {
    private final int slot;
    private final AtomicReference<Direction> side = new AtomicReference<>();
    private final AtomicBoolean enabled = new AtomicBoolean();
    private final SlotType slotType;
    private final String nbtName;
    private int output = -1;
    private int tankId = -1;
    private int recipePos = -1;

    public VESlotManager(int slotNum, Direction direction, boolean status, SlotType slotType) {
        this.side.set(direction);
        this.slot = slotNum;
        this.enabled.set(status);
        this.slotType = slotType;
        this.nbtName = slotType.getNBTName(slotNum);
    }

    public VESlotManager(int slotNum, Direction direction, boolean status, SlotType slotType, int outputSlot, int tankId) {
        this.side.set(direction);
        this.slot = slotNum;
        this.enabled.set(status);
        this.slotType = slotType;
        this.nbtName = slotType.getNBTName(slotNum);
        this.output = outputSlot;
        this.tankId = tankId;
    }

    public VESlotManager(int slotNum, int recipePos, Direction direction, boolean status, SlotType slotType) {
        this.side.set(direction);
        this.slot = slotNum;
        this.enabled.set(status);
        this.slotType = slotType;
        this.nbtName = slotType.getNBTName(slotNum);
        this.recipePos = recipePos;
    }

    public void setStatus(boolean bool) {
        this.enabled.set(bool);
    }

    public void setDirection(Direction direction) {
        this.side.set(direction);
    }

    public void setDirection(int direction) {
        this.side.set(directionFromInt(direction));
    }

    public Direction getDirection() {
        return this.side.get();
    }

    public boolean getStatus() {
        return this.enabled.get();
    }

    public int getSlotNum() {
        return slot;
    }

    public String getTranslationKey() {
        return slotType.getTranslationKey();
    }

    public int getRecipePos() {
        return recipePos;
    }

    public String getNbtName() {
        return nbtName;
    }

    public void write(CompoundTag nbt) {
        nbt.putBoolean(nbtName + "_enabled", getStatus());
        nbt.putInt(nbtName + "_direction", getDirection().get3DDataValue());
    }

    public void read(CompoundTag nbt) {
        setStatus(nbt.getBoolean(nbtName + "_enabled"));
        int sideInt = nbt.getInt(nbtName + "_direction");
        setDirection(directionFromInt(sideInt));
    }

    public SlotType getSlotType() {
        return slotType;
    }

    public Direction directionFromInt(int sideInt) {
        // 1 = top, 0 = bottom, 2 = north, 3 = south, 4 = west, 5 = east
        return switch (sideInt) {
            case 0 -> Direction.DOWN;
            case 1 -> Direction.UP;
            case 2 -> Direction.NORTH;
            case 3 -> Direction.SOUTH;
            case 4 -> Direction.WEST;
            case 5 -> Direction.EAST;
            default -> throw new NotImplementedException("Unknown side: " + sideInt);
        };
    }

    public ItemStack getItem(ItemStackHandler handler) {
        return handler.getStackInSlot(this.slot);
    }

    public int getOutputSlotId() {
        return output;
    }

    public int getTankId() {
        return tankId;
    }
}
