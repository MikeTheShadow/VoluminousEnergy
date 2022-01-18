package com.veteam.voluminousenergy.tools.sidemanager;

import com.veteam.voluminousenergy.util.SlotType;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class VESlotManager {
    private final int slot;
    private AtomicReference<Direction> side = new AtomicReference<>();
    private AtomicBoolean enabled = new AtomicBoolean();
    private final String translationKey;
    private final SlotType slotType;

    public VESlotManager(int slotNum, Direction direction, boolean status, String translationKey,SlotType slotType){
        this.side.set(direction);
        this.slot = slotNum;
        this.enabled.set(status);
        this.translationKey = translationKey;
        this.slotType = slotType;
    }

    public void setStatus(boolean bool){
        //VoluminousEnergy.LOGGER.debug("STATUS UPDATED TO: " + bool);
        this.enabled.set(bool);
    }

    public void setDirection(Direction direction){
        //VoluminousEnergy.LOGGER.debug("DIRECTION UPDATED TO: " + direction);
        this.side.set(direction);
    }

    public void setDirection(int direction){
        this.side.set(directionFromInt(direction));
        //VoluminousEnergy.LOGGER.debug("DIRECTION UPDATED TO: " + directionFromInt(direction));
    }

    public Direction getDirection(){
        return this.side.get();
    }

    public boolean getStatus(){
        return this.enabled.get();
    }

    public int getSlotNum(){
        return slot;
    }

    public String getTranslationKey(){
        return translationKey;
    }

    public void write(CompoundTag nbt, String prefix){
        //VoluminousEnergy.LOGGER.debug("Writing to NBT.");
        //VoluminousEnergy.LOGGER.debug("Writing: " + prefix + "_enabled as: " + enabled);
        //VoluminousEnergy.LOGGER.debug("Writing: " + prefix + "_direction as: " + getDirection().getIndex() + " Meaning: " + getDirection());
        nbt.putBoolean(prefix + "_enabled", getStatus());
        nbt.putInt(prefix+"_direction", getDirection().get3DDataValue());
    }

    public void read(CompoundTag nbt, String prefix){
        setStatus(nbt.getBoolean(prefix + "_enabled"));
        int sideInt = nbt.getInt(prefix + "_direction");

        //VoluminousEnergy.LOGGER.debug("Read the following: enabled: " + enabled + " sideInt: " + sideInt);

        setDirection(directionFromInt(sideInt));
        //VoluminousEnergy.LOGGER.debug("Direction set to: " + this.getDirection());
    }

    public SlotType getSlotType() {
        return slotType;
    }

    public Direction directionFromInt(int sideInt){
        // 1 = top, 0 = bottom, 2 = north, 3 = south, 4 = west, 5 = east
        return switch (sideInt){
            case 0 -> Direction.DOWN;
            case 1 -> Direction.UP;
            case 2 -> Direction.NORTH;
            case 3 -> Direction.SOUTH;
            case 4 -> Direction.WEST;
            case 5 -> Direction.EAST;
            default -> Direction.UP;
        };
    }
}
