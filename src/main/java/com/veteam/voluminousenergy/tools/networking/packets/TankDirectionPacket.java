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

public class TankDirectionPacket {
    private int direction;
    private int tankId;

    public TankDirectionPacket(){
        // Do nothing
    }

    public TankDirectionPacket(int updatedDirection, int id){
        this.direction = updatedDirection;
        this.tankId = id;
    }

    public static TankDirectionPacket fromBytes(PacketBuffer buffer){
        TankDirectionPacket packet = new TankDirectionPacket();
        packet.direction = buffer.readInt();
        packet.tankId = buffer.readInt();
        return packet;
    }

    public void toBytes(PacketBuffer buffer){
        buffer.writeInt(this.direction);
        buffer.writeInt(this.tankId);
    }

    public static void handle(TankDirectionPacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkDirection packetDirection = contextSupplier.get().getDirection();
        switch(packetDirection){
            case PLAY_TO_CLIENT:
                Container clientContainer = Minecraft.getInstance().player.containerMenu;
                contextSupplier.get().enqueueWork(() -> handlePacket(packet,clientContainer,false));
                break;
            default:
                Container serverContainer = (contextSupplier.get().getSender()).containerMenu;
                contextSupplier.get().enqueueWork(() -> handlePacket(packet,serverContainer,true));
        }

    }

    public static void handlePacket(TankDirectionPacket packet, Container openContainer, boolean onServer){
        if(openContainer != null){
            if (openContainer instanceof AirCompressorContainer) { // Air Compressor
                if (onServer) {
                    TileEntity tileEntity = ((AirCompressorContainer) openContainer).tileEntity;
                    if (tileEntity instanceof AirCompressorTile) {
                        ((AirCompressorTile) tileEntity).updateTankPacketFromGui(packet.direction, packet.tankId);
                    }
                    // End of Server side logic
                } else {
                    ((AirCompressorContainer) openContainer).updateDirectionTank(packet.direction, packet.tankId);
                }
                // End of Air Compressor
            } else if(openContainer instanceof AqueoulizerContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((AqueoulizerContainer) openContainer).tileEntity;
                    if (tileEntity instanceof AqueoulizerTile) {
                        ((AqueoulizerTile) tileEntity).updateTankPacketFromGui(packet.direction, packet.tankId);
                    }
                    // End of Server side logic
                } else {
                    ((AqueoulizerContainer) openContainer).updateDirectionTank(packet.direction, packet.tankId);
                }
                // End of Aqueoulizer
            } else if(openContainer instanceof CentrifugalAgitatorContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((CentrifugalAgitatorContainer) openContainer).tileEntity;
                    if (tileEntity instanceof CentrifugalAgitatorTile) {
                        ((CentrifugalAgitatorTile) tileEntity).updateTankPacketFromGui(packet.direction, packet.tankId);
                    }
                    // End of Server side logic
                } else {
                    ((CentrifugalAgitatorContainer) openContainer).updateDirectionTank(packet.direction, packet.tankId);
                }
                // End of CentrifugalAgitator
            } else if(openContainer instanceof CombustionGeneratorContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((CombustionGeneratorContainer) openContainer).tileEntity;
                    if (tileEntity instanceof CombustionGeneratorTile) {
                        ((CombustionGeneratorTile) tileEntity).updateTankPacketFromGui(packet.direction, packet.tankId);
                    }
                    // End of Server side logic
                } else {
                    ((CombustionGeneratorContainer) openContainer).updateDirectionTank(packet.direction, packet.tankId);
                }
                // End of CombustionGenerator
            } else if (openContainer instanceof DistillationUnitContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((DistillationUnitContainer) openContainer).tileEntity;
                    if (tileEntity instanceof DistillationUnitTile) {
                        ((DistillationUnitTile) tileEntity).updateTankPacketFromGui(packet.direction, packet.tankId);
                    }
                    // End of Server side logic
                } else {
                    ((DistillationUnitContainer) openContainer).updateDirectionTank(packet.direction, packet.tankId);
                }
                // End of DistillationUnit
            } else if(openContainer instanceof GasFiredFurnaceContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((GasFiredFurnaceContainer) openContainer).tileEntity;
                    if (tileEntity instanceof GasFiredFurnaceTile) {
                        ((GasFiredFurnaceTile) tileEntity).updateTankPacketFromGui(packet.direction, packet.tankId);
                    }
                    // End of Server side logic
                } else {
                    ((GasFiredFurnaceContainer) openContainer).updateDirectionTank(packet.direction, packet.tankId);
                }
                // End of GasFiredFurnace
            } else if(openContainer instanceof PumpContainer){
                if (onServer) {
                    TileEntity tileEntity = ((PumpContainer) openContainer).tileEntity;
                    if (tileEntity instanceof PumpTile) {
                        ((PumpTile) tileEntity).updateTankPacketFromGui(packet.direction, packet.tankId);
                    }
                    // End of Server side logic
                } else {
                    ((PumpContainer) openContainer).updateDirectionTank(packet.direction, packet.tankId);
                }
                // End of Pump
            } else {
                VoluminousEnergy.LOGGER.warn("TankDirectionPacket: Not a valid container.");
            }
        } else {
            VoluminousEnergy.LOGGER.warn("TankDirectionPacket: The container is null.");
        }
    }
}
