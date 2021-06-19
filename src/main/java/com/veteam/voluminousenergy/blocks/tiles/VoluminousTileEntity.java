package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.UniversalUpdatePacket;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.network.PacketDistributor;

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
        if(level == null) return;
        level.sendBlockUpdated(this.worldPosition,this.getBlockState(),this.getBlockState(),1); // notifyBlockUpdate --> sendBlockUpdated
        sendPacketToClient();
        uuidCleanup();
    }

    public String getDirection() {

        if(!this.level.isStateAtPosition(this.getBlockPos(),e -> e == this.getBlockState())) return "null";
        BlockState state = this.level.getBlockState(this.worldPosition);
        Optional<Map.Entry<Property<?>, Comparable<?>>> it = state.getValues().entrySet().stream().filter(e -> e.getKey().getValueClass() == Direction.class).findFirst();
        String direction = "null";
        if(it.isPresent()) {
            direction = it.get().getValue().toString();
        }
        return direction;
    }

    // Override this in Tile Entities, should mainly be for IO management. SUPER to this function with proper writing of Universal Update Packets
    public void sendPacketToClient(){
        if(this.cleanupTick == 10){
            double x = this.getBlockPos().getX();
            double y = this.getBlockPos().getY();
            double z = this.getBlockPos().getZ();
            final double radius = 64.0D; // Vanilla's Container#stillValid uses this as a <= value, so we'll use this to ensure no corner cases TODO: Config this as an option between 16 and 64
            RegistryKey<World> worldRegistryKey = this.getLevel().dimension();
            PacketDistributor.TargetPoint targetPoint = new PacketDistributor.TargetPoint(x,y,z,radius,worldRegistryKey);
            VENetwork.channel.send(PacketDistributor.NEAR.with(() -> targetPoint), writeUniversalUpdatePacket());
        }
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
        if(playerUuid.isEmpty() || level == null) return;
        if(level.getServer() == null) return;
        if(level.getServer() == null) return;
        if(cleanupTick == 20){
            cleanupTick = 0;
            ArrayList<UUID> toRemove = new ArrayList<>();
            this.playerUuid.forEach(u ->{
                if(!level.getServer().getPlayerList().getPlayers().contains(u)){
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

    public void readUniversalUpdatePacket(UniversalUpdatePacket packet){}

    protected UniversalUpdatePacket writeUniversalUpdatePacket(){
        return new UniversalUpdatePacket();
    }

    // Simplified call to get the stored energy from the Energy Capability that a TE might have
    protected int getEnergyStored(){
        return this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }
}
