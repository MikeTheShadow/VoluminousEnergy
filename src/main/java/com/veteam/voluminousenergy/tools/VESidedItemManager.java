package com.veteam.voluminousenergy.tools;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class VESidedItemManager {
    private final int slot;
    private AtomicReference<Direction> side = new AtomicReference<>();
    private AtomicBoolean enabled = new AtomicBoolean();
    private final String translationKey;
    public VESidedItemManager(int slotNum, Direction direction, boolean status, String translationKey){
        this.side.set(direction);
        this.slot = slotNum;
        this.enabled.set(status);
        this.translationKey = translationKey;
    }

    public void setStatus(boolean bool){
        VoluminousEnergy.LOGGER.debug("STATUS UPDATED TO: " + bool);
        this.enabled.set(bool);
    }

    public void setDirection(Direction direction){
        VoluminousEnergy.LOGGER.debug("DIRECTION UPDATED TO: " + direction);
        this.side.set(direction);
    }

    public void setDirection(int direction){
        this.side.set(directionFromInt(direction));
        VoluminousEnergy.LOGGER.debug("DIRECTION UPDATED TO: " + directionFromInt(direction));
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

    public void write(CompoundNBT nbt, String prefix){
        VoluminousEnergy.LOGGER.debug("Writing to NBT.");
        VoluminousEnergy.LOGGER.debug("Writing: " + prefix + "_enabled as: " + enabled);
        VoluminousEnergy.LOGGER.debug("Writing: " + prefix + "_direction as: " + getDirection().getIndex() + " Meaning: " + getDirection());
        nbt.putBoolean(prefix + "_enabled", getStatus());
        nbt.putInt(prefix+"_direction", getDirection().getIndex());
    }

    public void read(CompoundNBT nbt, String prefix){
        setStatus(nbt.getBoolean(prefix + "_enabled"));
        int sideInt = nbt.getInt(prefix + "_direction");

        VoluminousEnergy.LOGGER.debug("Read the following: enabled: " + enabled + " sideInt: " + sideInt);

        setDirection(directionFromInt(sideInt));
        VoluminousEnergy.LOGGER.debug("Direction set to: " + this.getDirection());
    }

    public Direction directionFromInt(int sideInt){
        // 1 = top, 0 = bottom, 2 = north, 3 = south, 4 = west, 5 = east
        if(sideInt == 0){
            return Direction.DOWN;
        } else if (sideInt == 1){
            return Direction.UP;
        } else if (sideInt == 2){
            return Direction.NORTH;
        } else if (sideInt == 3){
            return Direction.SOUTH;
        } else if (sideInt == 4){
            return Direction.WEST;
        } else if (sideInt == 5){
            return Direction.EAST;
        }
        return Direction.UP;
    }
}
