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

    public static TankBoolPacket fromBytes(PacketBuffer buffer){
        TankBoolPacket packet = new TankBoolPacket();
        packet.status = buffer.readBoolean();
        packet.id = buffer.readInt();
        return packet;
    }

    public void toBytes(PacketBuffer buffer){
        buffer.writeBoolean(this.status);
        buffer.writeInt(this.id);
    }

    public static void handle(TankBoolPacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        VoluminousEnergy.LOGGER.debug(contextSupplier.get().getDirection());
        NetworkDirection packetDirection = contextSupplier.get().getDirection();
        switch(packetDirection){
            case PLAY_TO_CLIENT:
                Container clientContainer = Minecraft.getInstance().player.openContainer;
                //VoluminousEnergy.LOGGER.debug("Client bound packet received.");
                contextSupplier.get().enqueueWork(() -> handlePacket(packet,clientContainer,false));
                break;
            default:
                Container serverContainer = (contextSupplier.get().getSender()).openContainer;
                contextSupplier.get().enqueueWork(() -> handlePacket(packet,serverContainer,true));
        }

    }

    public static void handlePacket(TankBoolPacket packet, Container openContainer, boolean onServer){
        //VoluminousEnergy.LOGGER.debug("Work has enqueued");
        if(openContainer != null){
            if(openContainer instanceof AirCompressorContainer){
                if(onServer){
                    TileEntity tileEntity = ((AirCompressorContainer) openContainer).tileEntity;
                    if (tileEntity instanceof AirCompressorTile){
                        ((AirCompressorTile) tileEntity).updateTankPacketFromGui(packet.status, packet.id);
                    }
                // End of Server side logic
                } else {
                    ((AirCompressorContainer) openContainer).updateStatusTank(packet.status, packet.id);
                }
            // End of Air Compressor logic
            } else {
                VoluminousEnergy.LOGGER.debug("TankBoolPacket: Not a valid container.");
            }
        } else {
            VoluminousEnergy.LOGGER.debug("TankBoolPacket The container is null");
        }
    }
}
