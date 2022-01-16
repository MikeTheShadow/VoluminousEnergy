package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.blocks.containers.BatteryBoxContainer;
import com.veteam.voluminousenergy.blocks.tiles.BatteryBoxTile;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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

    public static void handle(BatteryBoxSendOutPowerPacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkDirection packetDirection = contextSupplier.get().getDirection();
        switch (packetDirection){
            case PLAY_TO_CLIENT: // Packet is being sent to client
                AbstractContainerMenu clientContainer = Minecraft.getInstance().player.containerMenu;
                contextSupplier.get().enqueueWork(() -> handlePacket(packet,clientContainer,false));
                contextSupplier.get().setPacketHandled(true);
                break;
            default:
                AbstractContainerMenu serverContainer = (contextSupplier.get().getSender()).containerMenu;
                contextSupplier.get().enqueueWork(() -> handlePacket(packet,serverContainer,true));
                contextSupplier.get().setPacketHandled(true);
        }
    }

    public static void handlePacket(BatteryBoxSendOutPowerPacket packet, AbstractContainerMenu openContainer, boolean onServer){
        if(openContainer != null){
            if(openContainer instanceof BatteryBoxContainer){
                if(onServer){
                    BlockEntity tileEntity = ((BatteryBoxContainer) openContainer).getTileEntity();
                    if(tileEntity instanceof BatteryBoxTile){ // sanity check
                        ((BatteryBoxTile) tileEntity).updateSendOutPower(packet.status);
                        tileEntity.setChanged();
                    }
                } else {
                    ((BatteryBoxContainer) openContainer).updateSendOutPowerButton(packet.status);
                }
            }
        }
    }
}
