package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.*;
import com.veteam.voluminousenergy.blocks.tiles.*;
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
            if(openContainer instanceof AirCompressorContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((AirCompressorContainer) openContainer).tileEntity;
                    if (tileEntity instanceof AirCompressorTile) {
                        ((AirCompressorTile) tileEntity).updateTankPacketFromGui(packet.status, packet.id);
                    }
                    // End of Server side logic
                } else {
                    ((AirCompressorContainer) openContainer).updateStatusTank(packet.status, packet.id);
                }
                // End of Air Compressor logic
            } else if(openContainer instanceof AqueoulizerContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((AqueoulizerContainer) openContainer).tileEntity;
                    if (tileEntity instanceof AqueoulizerTile) {
                        ((AqueoulizerTile) tileEntity).updateTankPacketFromGui(packet.status, packet.id);
                    }
                    // End of Server side logic
                } else {
                    ((AqueoulizerContainer) openContainer).updateStatusTank(packet.status, packet.id);
                }
                // End of Aqueoulizer
            } else if(openContainer instanceof CentrifugalAgitatorContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((CentrifugalAgitatorContainer) openContainer).tileEntity;
                    if (tileEntity instanceof CentrifugalAgitatorTile) {
                        ((CentrifugalAgitatorTile) tileEntity).updateTankPacketFromGui(packet.status, packet.id);
                    }
                    // End of Server side logic
                } else {
                    ((CentrifugalAgitatorContainer) openContainer).updateStatusTank(packet.status, packet.id);
                }
                // End of CentrifugalAgitator
            } else if(openContainer instanceof CombustionGeneratorContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((CombustionGeneratorContainer) openContainer).tileEntity;
                    if (tileEntity instanceof CombustionGeneratorTile) {
                        ((CombustionGeneratorTile) tileEntity).updateTankPacketFromGui(packet.status, packet.id);
                    }
                    // End of Server side logic
                } else {
                    ((CombustionGeneratorContainer) openContainer).updateStatusTank(packet.status, packet.id);
                }
                // End of CombustionGenerator
            } else if (openContainer instanceof DistillationUnitContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((DistillationUnitContainer) openContainer).tileEntity;
                    if (tileEntity instanceof DistillationUnitTile) {
                        ((DistillationUnitTile) tileEntity).updateTankPacketFromGui(packet.status, packet.id);
                    }
                    // End of Server side logic
                } else {
                    ((DistillationUnitContainer) openContainer).updateStatusTank(packet.status, packet.id);
                }
                // End of DistillationUnit
            } else if(openContainer instanceof GasFiredFurnaceContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((GasFiredFurnaceContainer) openContainer).tileEntity;
                    if (tileEntity instanceof GasFiredFurnaceTile) {
                        ((GasFiredFurnaceTile) tileEntity).updateTankPacketFromGui(packet.status, packet.id);
                    }
                    // End of Server side logic
                } else {
                    ((GasFiredFurnaceContainer) openContainer).updateStatusTank(packet.status, packet.id);
                }
                // End of GasFiredFurnace
            } else if (openContainer instanceof PumpContainer){
                if (onServer) {
                    TileEntity tileEntity = ((PumpContainer) openContainer).tileEntity;
                    if (tileEntity instanceof PumpTile) {
                        ((PumpTile) tileEntity).updateTankPacketFromGui(packet.status, packet.id);
                    }
                    // End of Server side logic
                } else {
                    ((PumpContainer) openContainer).updateStatusTank(packet.status, packet.id);
                }
                // End of Pump
            } else {
                VoluminousEnergy.LOGGER.debug("TankBoolPacket: Not a valid container.");
            }
        } else {
            VoluminousEnergy.LOGGER.debug("TankBoolPacket The container is null");
        }
    }
}
