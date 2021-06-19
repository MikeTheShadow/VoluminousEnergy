package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.blocks.containers.BatteryBoxContainer;
import com.veteam.voluminousenergy.blocks.tiles.BatteryBoxTile;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

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

    public static BatteryBoxSlotPairPacket fromBytes(PacketBuffer buffer){
        BatteryBoxSlotPairPacket packet = new BatteryBoxSlotPairPacket();
        packet.status = buffer.readBoolean();
        packet.id = buffer.readInt();
        return packet;
    }

    public void toBytes(PacketBuffer buffer){
        buffer.writeBoolean(this.status);
        buffer.writeInt(this.id);
    }

    public static void handle(BatteryBoxSlotPairPacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkDirection packetDirection = contextSupplier.get().getDirection();
        switch (packetDirection){
            case PLAY_TO_CLIENT: // Packet is being sent to client
                Container clientContainer = Minecraft.getInstance().player.containerMenu;
                contextSupplier.get().enqueueWork(() -> handlePacket(packet,clientContainer,false));
                break;
            default:
                Container serverContainer = (contextSupplier.get().getSender()).containerMenu;
                contextSupplier.get().enqueueWork(() -> handlePacket(packet,serverContainer,true));
        }
    }

    public static void handlePacket(BatteryBoxSlotPairPacket packet, Container openContainer, boolean onServer){
        if(openContainer != null){
            if(openContainer instanceof BatteryBoxContainer){
                if(onServer){
                    TileEntity tileEntity = ((BatteryBoxContainer) openContainer).getTileEntity();
                    if(tileEntity instanceof BatteryBoxTile){ // sanity check
                        ((BatteryBoxTile) tileEntity).updateSlotPair(packet.status, packet.id);
                    }
                } else {
                    ((BatteryBoxContainer) openContainer).updateSlotPairButton(packet.status,packet.id);
                }
            }
        }
    }
}
