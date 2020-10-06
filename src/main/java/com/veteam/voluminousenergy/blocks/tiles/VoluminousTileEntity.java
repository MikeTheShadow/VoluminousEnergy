package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.Vec3i;

import java.util.List;

public class VoluminousTileEntity extends TileEntity {


    public VoluminousTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    /**
     * If a player is within 16 blocks send them an update packet
     */
    public void updateClients() {
        world.notifyBlockUpdate(this.pos,this.getBlockState(),this.getBlockState(),1);
    }
}
