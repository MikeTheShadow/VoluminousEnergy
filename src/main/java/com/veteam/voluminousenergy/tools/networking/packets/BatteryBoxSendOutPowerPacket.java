package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.blocks.tiles.BatteryBoxTile;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

public class BatteryBoxSendOutPowerPacket {
    private boolean status;

    public BatteryBoxSendOutPowerPacket(){
        // Do nothing
    }

    public BatteryBoxSendOutPowerPacket(boolean status){
        this.status = status;
    }

    public static BatteryBoxSendOutPowerPacket fromBytes(FriendlyByteBuf buffer){
        BatteryBoxSendOutPowerPacket packet = new BatteryBoxSendOutPowerPacket();
        packet.status = buffer.readBoolean();
        return packet;
    }

    public void toBytes(FriendlyByteBuf buffer){
        buffer.writeBoolean(this.status);
    }

    public static void handle(BatteryBoxSendOutPowerPacket packet, CustomPayloadEvent.Context contextSupplier){
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

    public static void handlePacket(BatteryBoxSendOutPowerPacket packet, AbstractContainerMenu openContainer, boolean onServer){
        if(openContainer != null){
            if(openContainer instanceof VEContainer batteryBoxContainer){
                if(onServer){
                    BlockEntity tileEntity = batteryBoxContainer.getTileEntity();
                    if(tileEntity instanceof BatteryBoxTile batteryBoxTile){
                        batteryBoxTile.updateSendOutPower(packet.status);
                        batteryBoxTile.setChanged();
                    }
                } else {
                    batteryBoxContainer.updateSendOutPowerButton(packet.status);
                }
            }
        }
    }
}
