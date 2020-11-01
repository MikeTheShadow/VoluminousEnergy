package com.veteam.voluminousenergy.blocks.tiles;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class VoluminousTileEntity extends TileEntity {


    public VoluminousTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    /**
     * If a player is within 16 blocks send them an update packet
     */
    public void updateClients() {
        if(world == null) return;
        world.notifyBlockUpdate(this.pos,this.getBlockState(),this.getBlockState(),1);
    }
}
