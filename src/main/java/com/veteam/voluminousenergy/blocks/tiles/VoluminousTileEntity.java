package com.veteam.voluminousenergy.blocks.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

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

    public String getDirection() {

        if(!this.world.hasBlockState(this.getPos(),e -> e == this.getBlockState())) return "null";
        BlockState state = this.world.getBlockState(this.pos);
        Optional<Map.Entry<Property<?>, Comparable<?>>> it = state.getValues().entrySet().stream().filter(e -> e.getKey().getValueClass() == Direction.class).findFirst();
        String direction = "null";
        if(it.isPresent()) {
            direction = it.get().getValue().toString();
        }
        return direction;
    }
}
