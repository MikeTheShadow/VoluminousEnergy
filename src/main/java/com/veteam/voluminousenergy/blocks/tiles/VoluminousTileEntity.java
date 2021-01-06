package com.veteam.voluminousenergy.blocks.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.Stream;

public class VoluminousTileEntity extends TileEntity {

    protected ArrayList<UUID> playerUuid = new ArrayList<>();

    public VoluminousTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    protected int cleanupTick = 0;

    /**
     * If a player is within 16 blocks send them an update packet
     */
    public void updateClients() {
        if(world == null) return;
        world.notifyBlockUpdate(this.pos,this.getBlockState(),this.getBlockState(),1);
        sendPacketToClient();
        uuidCleanup();
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

    // Override this in Tile Entities, should mainly be for IO management
    public void sendPacketToClient(){
    }

    public void uuidPacket(UUID uuid, boolean connectionFlag){
        if(!playerUuid.isEmpty()){
            if(playerUuid.contains(uuid) && !connectionFlag){
                playerUuid.remove(uuid);
            } else if (!playerUuid.contains(uuid) && connectionFlag){
                playerUuid.add(uuid);
            }
        } else {
            if(connectionFlag){
                playerUuid.add(uuid);
            }
        }
    }

    // Standard cookie cutter cleanup. Works on servers as a crutch, but not so much on singleplayer
    protected void uuidCleanup(){
        if(playerUuid.isEmpty() || world == null) return;
        if(world.getServer() == null) return;
        if(world.getServer() == null) return;
        if(cleanupTick == 20){
            cleanupTick = 0;
            ArrayList<UUID> toRemove = new ArrayList<>();
            this.playerUuid.forEach(u ->{
                if(!world.getServer().getPlayerList().getPlayers().contains(u)){
                    toRemove.add(u);
                }
            });
            toRemove.forEach(uuid -> playerUuid.remove(uuid));
        } else {
            cleanupTick++;
        }
    }
}
