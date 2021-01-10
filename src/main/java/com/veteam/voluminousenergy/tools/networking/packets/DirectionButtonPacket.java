package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.AirCompressorContainer;
import com.veteam.voluminousenergy.blocks.containers.CrusherContainer;
import com.veteam.voluminousenergy.blocks.tiles.AirCompressorTile;
import com.veteam.voluminousenergy.blocks.tiles.CrusherTile;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class DirectionButtonPacket {
    private int direction;
    private int slotId;

    public DirectionButtonPacket(){
        // Do nothing
    }

    public DirectionButtonPacket(int updatedDirection, int slot){
        this.direction = updatedDirection;
        this.slotId = slot;
    }

    public static DirectionButtonPacket fromBytes(PacketBuffer buffer){
        DirectionButtonPacket packet = new DirectionButtonPacket();
        packet.direction = buffer.readInt();
        packet.slotId = buffer.readInt();
        return packet;
    }

    public void toBytes(PacketBuffer buffer){
        buffer.writeInt(this.direction);
        buffer.writeInt(this.slotId);
    }

    public static void handle(DirectionButtonPacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkDirection packetDirection = contextSupplier.get().getDirection();
        switch(packetDirection){
            case PLAY_TO_CLIENT:
                Container clientContainer = Minecraft.getInstance().player.openContainer;
                contextSupplier.get().enqueueWork(() -> handlePacket(packet,clientContainer,false));
                break;
            default:
                Container serverContainer = (contextSupplier.get().getSender()).openContainer;
                contextSupplier.get().enqueueWork(() -> handlePacket(packet,serverContainer,true));
        }

    }

    public static void handlePacket(DirectionButtonPacket packet, Container openContainer, boolean onServer){
        if(openContainer != null){
            if(openContainer instanceof CrusherContainer){ // Crusher
                if(onServer){
                    TileEntity tileEntity = ((CrusherContainer) openContainer).tileEntity;
                    if (tileEntity instanceof CrusherTile){
                        ((CrusherTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((CrusherContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
            } else if (openContainer instanceof AirCompressorContainer) { // Air Compressor
                if(onServer){
                    TileEntity tileEntity = ((AirCompressorContainer) openContainer).tileEntity;
                    if (tileEntity instanceof AirCompressorTile){
                        ((AirCompressorTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((AirCompressorContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
            } else {
                VoluminousEnergy.LOGGER.warn("DirectionButtonPacket: Not a valid container.");
            }
        } else {
            VoluminousEnergy.LOGGER.warn("DirectionButtonPacket: The container is null.");
        }
    }
}
