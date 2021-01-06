package com.veteam.voluminousenergy.tools.networking;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.CrusherContainer;
import com.veteam.voluminousenergy.blocks.tiles.CrusherTile;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class BoolButtonPacket {
    private boolean status;
    private int slotId;

    public BoolButtonPacket(){
        // Do nothing
    }

    public BoolButtonPacket(boolean updatedStatus, int slot){
        this.status = updatedStatus;
        this.slotId = slot;
    }

    public static BoolButtonPacket fromBytes(PacketBuffer buffer){
        BoolButtonPacket packet = new BoolButtonPacket();
        packet.status = buffer.readBoolean();
        packet.slotId = buffer.readInt();
        return packet;
    }

    public void toBytes(PacketBuffer buffer){
        buffer.writeBoolean(this.status);
        buffer.writeInt(this.slotId);
    }

    public static void handle(BoolButtonPacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        VoluminousEnergy.LOGGER.debug(contextSupplier.get().getDirection());
        NetworkDirection packetDirection = contextSupplier.get().getDirection();
        switch(packetDirection){
            case PLAY_TO_CLIENT:
                Container clientContainer = Minecraft.getInstance().player.openContainer;
                VoluminousEnergy.LOGGER.debug("Client bound packet received.");
                contextSupplier.get().enqueueWork(() -> handlePacket(packet,clientContainer,false));
                break;
            default:
                Container serverContainer = (contextSupplier.get().getSender()).openContainer;
                contextSupplier.get().enqueueWork(() -> handlePacket(packet,serverContainer,true));
        }

    }

    public static void handlePacket(BoolButtonPacket packet, Container openContainer, boolean onServer){
        VoluminousEnergy.LOGGER.debug("Work has enqueued");
        if(openContainer != null){
            if(openContainer instanceof CrusherContainer){
                if(onServer){
                    TileEntity tileEntity = ((CrusherContainer) openContainer).tileEntity;
                    if (tileEntity instanceof CrusherTile){
                        ((CrusherTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((CrusherContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
            } else {
                VoluminousEnergy.LOGGER.debug("Not a crusher container.");
            }
        } else {
            VoluminousEnergy.LOGGER.debug("The container is null");
        }
    }
}
