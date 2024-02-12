package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.blocks.tiles.BatteryBoxTile;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

public class BatteryBoxSlotPairPacket {
    private boolean status;
    private int id;

    public BatteryBoxSlotPairPacket(){
        // Do nothing
    }

    public BatteryBoxSlotPairPacket(boolean status, int id){
        this.status = status;
        this.id = id;
    }

    public static BatteryBoxSlotPairPacket fromBytes(FriendlyByteBuf buffer){
        BatteryBoxSlotPairPacket packet = new BatteryBoxSlotPairPacket();
        packet.status = buffer.readBoolean();
        packet.id = buffer.readInt();
        return packet;
    }

    public void toBytes(FriendlyByteBuf buffer){
        buffer.writeBoolean(this.status);
        buffer.writeInt(this.id);
    }

    public static void handle(BatteryBoxSlotPairPacket packet, CustomPayloadEvent.Context contextSupplier){
        NetworkDirection packetDirection = contextSupplier.getDirection();
        switch (packetDirection){
            case PLAY_TO_CLIENT: // Packet is being sent to client
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

    public static void handlePacket(BatteryBoxSlotPairPacket packet, AbstractContainerMenu openContainer, boolean onServer) {
        if (openContainer != null) {
            if (openContainer instanceof VEContainer batteryBoxContainer) {
                if (onServer) {
                    BlockEntity tileEntity = batteryBoxContainer.getTileEntity();
                    if (tileEntity instanceof BatteryBoxTile batteryBoxTile) {
                        batteryBoxTile.updateSlotPair(packet.status, packet.id);
                        batteryBoxTile.setChanged();
                    }
                }
            }
        }
    }
}
