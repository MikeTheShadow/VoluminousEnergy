package com.veteam.voluminousenergy.tools;

import net.minecraft.util.Direction;

public class VESidedItemManager {
    public final int slot;
    public Direction side;
    public boolean enabled;
    public VESidedItemManager(int slotNum, Direction direction, boolean status){
        this.side = direction;
        this.slot = slotNum;
        this.enabled = status;
    }
}
