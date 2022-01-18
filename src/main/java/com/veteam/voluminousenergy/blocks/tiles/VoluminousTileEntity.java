package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.BoolButtonPacket;
import com.veteam.voluminousenergy.tools.networking.packets.DirectionButtonPacket;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.MultiSlotWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class VoluminousTileEntity extends BlockEntity implements MenuProvider {

    protected ArrayList<UUID> playerUuid = new ArrayList<>();

    public VoluminousTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, VoluminousTileEntity voluminousTile){
        voluminousTile.tick();
    }

    public void tick(){ }

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
    public void sendPacketToClient(){ }

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

    // Simplified call to get the stored energy from the Energy Capability that a TE might have
    protected int getEnergyStored(){
        return this.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    public void updatePacketFromGui(boolean status, int slotId){

    }

    public void updatePacketFromGui(int direction, int slotId){
    }
    public void updateTankPacketFromGui(boolean status, int id) {

    }
    public void updateTankPacketFromGui(int direction, int id) {

    }

    public void bulkSendSMPacket(ServerPlayer s, VESlotManager... slots) {
        for(VESlotManager slot : slots) {
            // Boolean button
            VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new BoolButtonPacket(slot.getStatus(), slot.getSlotNum()));
            // Slot direction
            VENetwork.channel.send(PacketDistributor.PLAYER.with(() -> s), new DirectionButtonPacket(slot.getDirection().get3DDataValue(),slot.getSlotNum()));
        }
   }

   public void processGUIPacketStatus(boolean status, int slotId, VESlotManager... slots) {
        for(VESlotManager slot : slots) {
            if(slotId == slot.getSlotNum()) slot.setStatus(status);
        }
   }

    public void processGUIPacketDirection(int direction, int slotId, VESlotManager... slots) {
        for(VESlotManager slot : slots) {
            if(slotId == slot.getSlotNum()) slot.setDirection(direction);
        }
    }

    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side, LazyOptional<ItemStackHandler> handler, ItemStackHandler inventory , List<VESlotManager> managers) {
        if (side == null) return handler.cast();
        List<VESlotManager> managerList = managers.stream().filter(manager -> manager.getStatus() && manager.getDirection().get3DDataValue() == side.get3DDataValue()).toList();
        if(managerList.size() == 0) return super.getCapability(cap, side);
        MultiSlotWrapper slotWrapper = new MultiSlotWrapper(inventory,managerList);
        return LazyOptional.of(() -> slotWrapper).cast();
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent(getType().getRegistryName().getPath());
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return null;
    }
}
