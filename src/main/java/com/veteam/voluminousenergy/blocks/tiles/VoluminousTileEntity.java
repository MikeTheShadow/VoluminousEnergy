package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.items.upgrades.QuartzMultiplier;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.tools.VEEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    protected int calculateCounter(int processTime, ItemStack upgradeStack){
        if (upgradeStack.getItem() == VEItems.QUARTZ_MULTIPLIER) {
            int count = upgradeStack.getCount();
            if(count == 4){
                return 5;
            } else {
                return (-45*upgradeStack.getCount())+processTime;
            }
        }
        return processTime;
    }

    protected int consumptionMultiplier(int consumption, ItemStack upgradeStack){
        if(upgradeStack.getItem() == VEItems.QUARTZ_MULTIPLIER){
            int count = upgradeStack.getCount();
            if(count == 4){
                return consumption*16;
            } else if (count == 3) {
                return consumption*8;
            } else if(count == 2){
                return consumption*4;
            } else if(count == 1){
                return consumption*2;
            }
        }
        return consumption;
    }
}
