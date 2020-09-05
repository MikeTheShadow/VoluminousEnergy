package com.veteam.voluminousenergy.tools;

import net.minecraft.util.Direction;

public class VESidedItemManager {
    public final int slot;
    public Direction side;
    public boolean enabled;
    public String io;
    public VESidedItemManager(int slotNum, Direction direction, String mode){
        this.side = direction;
        this.slot = slotNum;
        if (mode.equals("disabled")){
            this.enabled = false;
            this.io = "disabled";
        } else if (mode.equals("insert")) {
            this.enabled = true;
            this.io = "insert";
        } else if (mode.equals("extract")){
            this.enabled = true;
            this.io = "extract";
        }
    }
}
