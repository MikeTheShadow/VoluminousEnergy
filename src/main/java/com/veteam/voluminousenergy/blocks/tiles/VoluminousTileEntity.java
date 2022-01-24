package com.veteam.voluminousenergy.blocks.tiles;

import com.veteam.voluminousenergy.blocks.containers.AqueoulizerContainer;
import com.veteam.voluminousenergy.items.VEItems;
import com.veteam.voluminousenergy.tools.energy.VEEnergyStorage;
import com.veteam.voluminousenergy.tools.networking.VENetwork;
import com.veteam.voluminousenergy.tools.networking.packets.BoolButtonPacket;
import com.veteam.voluminousenergy.tools.networking.packets.DirectionButtonPacket;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.MultiFluidSlotWrapper;
import com.veteam.voluminousenergy.util.MultiSlotWrapper;
import com.veteam.voluminousenergy.util.RelationalTank;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public abstract class VoluminousTileEntity extends BlockEntity implements MenuProvider {

    protected ArrayList<UUID> playerUuid = new ArrayList<>();

    public VoluminousTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, VoluminousTileEntity voluminousTile){
        voluminousTile.tick();
    }

    public abstract void tick();

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

    public void uuidPacket(UUID uuid, boolean connectionFlag) {
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

    /**
     *
     * @param cap Base capability. Pass this in from the super
     * @param side Base Direction
     * @param handler LazyOptional<ItemStackHandler> of the inventory
     * @param inventory The block inventory.
     * @param itemManagers @Nullable the managers for any item stack
     * @param fluidManagers @Nullable the managers for fluid tanks
     * @param energy @Nullable the manager for energy
     * @param <T> T the type of capability
     * @return A LazyOptional of Optional of type T matching the capability passed into this
     */
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side, LazyOptional<ItemStackHandler> handler, ItemStackHandler inventory, @Nullable List<VESlotManager> itemManagers, @Nullable List<RelationalTank> fluidManagers, @Nullable LazyOptional<VEEnergyStorage> energy) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == null || itemManagers == null) return handler.cast();
            Direction modifiedSide = normalizeDirection(side);
            List<VESlotManager> managerList = itemManagers
                    .stream()
                    .filter(manager -> manager.getStatus()
                            && manager.getDirection().get3DDataValue() == modifiedSide.get3DDataValue())
                    .toList();
            if (managerList.size() == 0) return super.getCapability(cap, side);
            MultiSlotWrapper slotWrapper = new MultiSlotWrapper(inventory, managerList);
            return LazyOptional.of(() -> slotWrapper).cast();
        } else if (cap == CapabilityEnergy.ENERGY && energy != null) {
            return energy.cast();
        } else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && side != null && fluidManagers != null) {
            Direction modifiedSide = normalizeDirection(side);
            List<RelationalTank> relationalTanks = fluidManagers.stream().filter(manager -> manager.getSideStatus() && manager.getSideDirection().get3DDataValue() == modifiedSide.get3DDataValue() || manager.isIgnoreDirection()).toList();
            if (relationalTanks.size() == 0) return super.getCapability(cap, side);
            MultiFluidSlotWrapper slotWrapper = new MultiFluidSlotWrapper(relationalTanks);
            return LazyOptional.of(() -> slotWrapper).cast();
        } else {
            return super.getCapability(cap, side);
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return new TextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return null;
    }

    public Direction normalizeDirection(Direction direction) {
        Direction currentDirection = this.getBlockState().getValue(BlockStateProperties.FACING);
        int directionInt = direction.get3DDataValue();
        if(directionInt == 0 || directionInt == 1) return direction;
        Direction rotated = currentDirection;
        for(int i = 0; i < 4;i++) {
            rotated = rotated.getClockWise();
            direction = direction.getClockWise();
            if(rotated.get3DDataValue() == 2) break;
        }
        return direction.getClockWise().getClockWise();
    }

    private void uuidCleanup() {
        if(playerUuid.isEmpty() || level == null) return;
        if(level.getServer() == null) return;

        if(cleanupTick == 20){
            ArrayList<UUID> toRemove = new ArrayList<>();
            level.getServer().getPlayerList().getPlayers().forEach(player ->{
                if(!(player.containerMenu instanceof AqueoulizerContainer)){
                    toRemove.add(player.getUUID());
                }
            });
            toRemove.forEach(uuid -> playerUuid.remove(uuid));
        }
    }

}
