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

public class BatteryBoxSendOutPowerPacket {
    private boolean status;

    public BatteryBoxSendOutPowerPacket(){
        // Do nothing
    }

    public BatteryBoxSendOutPowerPacket(boolean status){
        this.status = status;
    }

    public static BatteryBoxSendOutPowerPacket fromBytes(PacketBuffer buffer){
        BatteryBoxSendOutPowerPacket packet = new BatteryBoxSendOutPowerPacket();
        packet.status = buffer.readBoolean();
        return packet;
    }

    public void toBytes(PacketBuffer buffer){
        buffer.writeBoolean(this.status);
    }

    public static void handle(BatteryBoxSendOutPowerPacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkDirection packetDirection = contextSupplier.get().getDirection();
        switch (packetDirection){
            case PLAY_TO_CLIENT: // Packet is being sent to client
                Container clientContainer = Minecraft.getInstance().player.openContainer;
                contextSupplier.get().enqueueWork(() -> handlePacket(packet,clientContainer,false));
                break;
            default:
                Container serverContainer = (contextSupplier.get().getSender()).openContainer;
                contextSupplier.get().enqueueWork(() -> handlePacket(packet,serverContainer,true));
        }
    }

    public static void handlePacket(BatteryBoxSendOutPowerPacket packet, Container openContainer, boolean onServer){
        if(openContainer != null){
            if(openContainer instanceof BatteryBoxContainer){
                if(onServer){
                    TileEntity tileEntity = ((BatteryBoxContainer) openContainer).tileEntity;
                    if(tileEntity instanceof BatteryBoxTile){ // sanity check
                        ((BatteryBoxTile) tileEntity).updateSendOutPower(packet.status);
                    }
                } else {
                    ((BatteryBoxContainer) openContainer).updateSendOutPowerButton(packet.status);
                }
            }
        }
    }
}
