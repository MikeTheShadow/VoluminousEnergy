package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.*;
import com.veteam.voluminousenergy.blocks.tiles.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

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

    public static TankDirectionPacket fromBytes(FriendlyByteBuf buffer){
        TankDirectionPacket packet = new TankDirectionPacket();
        packet.direction = buffer.readInt();
        packet.tankId = buffer.readInt();
        return packet;
    }

    public void toBytes(FriendlyByteBuf buffer){
        buffer.writeInt(this.direction);
        buffer.writeInt(this.tankId);
    }

    public static void handle(TankDirectionPacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkDirection packetDirection = contextSupplier.get().getDirection();
        switch(packetDirection){
            case PLAY_TO_CLIENT:
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

    public static void handlePacket(TankDirectionPacket packet, AbstractContainerMenu openContainer, boolean onServer){
        if(openContainer != null){
            if (openContainer instanceof AirCompressorContainer) { // Air Compressor
                if (onServer) {
                    BlockEntity tileEntity = ((AirCompressorContainer) openContainer).getTileEntity();
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
                    BlockEntity tileEntity = ((AqueoulizerContainer) openContainer).getTileEntity();
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
                    BlockEntity tileEntity = ((CentrifugalAgitatorContainer) openContainer).getTileEntity();
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
                    BlockEntity tileEntity = ((CombustionGeneratorContainer) openContainer).getTileEntity();
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
                    BlockEntity tileEntity = ((DistillationUnitContainer) openContainer).getTileEntity();
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
                    BlockEntity tileEntity = ((GasFiredFurnaceContainer) openContainer).getTileEntity();
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
                    BlockEntity tileEntity = ((PumpContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof PumpTile) {
                        ((PumpTile) tileEntity).updateTankPacketFromGui(packet.direction, packet.tankId);
                    }
                    // End of Server side logic
                } else {
                    ((PumpContainer) openContainer).updateDirectionTank(packet.direction, packet.tankId);
                }
                // End of Pump
            } else if(openContainer instanceof BlastFurnaceContainer){
                if (onServer) {
                    BlockEntity tileEntity = ((BlastFurnaceContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof BlastFurnaceTile) {
                        ((BlastFurnaceTile) tileEntity).updateTankPacketFromGui(packet.direction, packet.tankId);
                    }
                    // End of Server side logic
                } else {
                    ((BlastFurnaceContainer) openContainer).updateDirectionTank(packet.direction, packet.tankId);
                }
                // End of Blast Furnace
            } else if (openContainer instanceof ToolingStationContainer){
                if (onServer) {
                    BlockEntity tileEntity = ((ToolingStationContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof ToolingStationTile) {
                        ((ToolingStationTile) tileEntity).updateTankPacketFromGui(packet.direction, packet.tankId);
                    }
                    // End of Server side logic
                } else {
                    ((ToolingStationContainer) openContainer).updateDirectionTank(packet.direction, packet.tankId);
                }
                // End of Tooling Station
            } else if (openContainer instanceof SawmillContainer){
                if (onServer) {
                    BlockEntity tileEntity = ((SawmillContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof SawmillTile) {
                        ((SawmillTile) tileEntity).updateTankPacketFromGui(packet.direction, packet.tankId);
                    }
                    // End of Server side logic
                } else {
                    ((SawmillContainer) openContainer).updateDirectionTank(packet.direction, packet.tankId);
                }
                // End of Sawmill
            } else if (openContainer instanceof TankContainer){
                if (onServer) {
                    BlockEntity tileEntity = ((TankContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof TankTile) {
                        //((TankTile) tileEntity).updateTankPacketFromGui(packet.direction, packet.tankId); TODO: Tank packet handling
                    }
                    // End of Server side logic
                } else {
                    //((TankContainer) openContainer).updateDirectionTank(packet.direction, packet.tankId); TODO: Tank packet handling
                }
                // End of Sawmill
            } else {
                VoluminousEnergy.LOGGER.warn("TankDirectionPacket: Not a valid container.");
            }
        } else {
            VoluminousEnergy.LOGGER.warn("TankDirectionPacket: The container is null.");
        }
    }
}
