package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.VoluminousContainer;
import com.veteam.voluminousenergy.blocks.tiles.VEFluidTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

public class TankBoolPacket {
    private boolean status;
    private int id;

    public TankBoolPacket(){
        // Do nothing
    }

    public TankBoolPacket(boolean updatedStatus, int id){
        this.status = updatedStatus;
        this.id = id;
    }

    public static TankBoolPacket fromBytes(FriendlyByteBuf buffer){
        TankBoolPacket packet = new TankBoolPacket();
        packet.status = buffer.readBoolean();
        packet.id = buffer.readInt();
        return packet;
    }

    public void toBytes(FriendlyByteBuf buffer){
        buffer.writeBoolean(this.status);
        buffer.writeInt(this.id);
    }

    public static void handle(TankBoolPacket packet, CustomPayloadEvent.Context contextSupplier){
        NetworkDirection packetDirection = contextSupplier.getDirection();
        switch(packetDirection){
            case PLAY_TO_CLIENT:
                AbstractContainerMenu clientContainer = Minecraft.getInstance().player.containerMenu;
                contextSupplier.enqueueWork(() -> handlePacket(packet,clientContainer,false));
                contextSupplier.setPacketHandled(true);
                break;
            default:
                AbstractContainerMenu serverContainer = (contextSupplier.getSender()).containerMenu;
                contextSupplier.enqueueWork(() -> handlePacket(packet,serverContainer,true));
                contextSupplier.setPacketHandled(true);
        }

    }

    public static void handlePacket(TankBoolPacket packet, AbstractContainerMenu openContainer, boolean onServer){
        if(openContainer != null){

            if(openContainer instanceof VoluminousContainer voluminousContainer) {
                if (onServer) {
                    BlockEntity tileEntity = voluminousContainer.getTileEntity();
                    if (tileEntity instanceof VEFluidTileEntity veFluidTileEntity) {
                        veFluidTileEntity.updateTankPacketFromGui(packet.status, packet.id);
                        veFluidTileEntity.setChanged();
                    }
                } else {
                    //voluminousContainer.updateStatusTank(packet.status, packet.id);
                }
            } else {
                VoluminousEnergy.LOGGER.warn("TankBoolPacket: Not a valid container.");
            }
        } else {
            VoluminousEnergy.LOGGER.warn("TankBoolPacket The container is null");
        }
    }
}
