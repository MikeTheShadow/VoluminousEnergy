package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.*;
import com.veteam.voluminousenergy.blocks.tiles.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

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

    public static TankBoolPacket fromBytes(FriendlyByteBuf buffer){
        TankBoolPacket packet = new TankBoolPacket();
        packet.status = buffer.readBoolean();
        packet.id = buffer.readInt();
        return packet;
    }

    public void toBytes(FriendlyByteBuf buffer){
        buffer.writeBoolean(this.status);
        buffer.writeInt(this.id);
    }

    public static void handle(TankBoolPacket packet, Supplier<NetworkEvent.Context> contextSupplier){
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

    public static void handlePacket(TankBoolPacket packet, AbstractContainerMenu openContainer, boolean onServer){
        //VoluminousEnergy.LOGGER.debug("Work has enqueued");
        if(openContainer != null){
            if(openContainer instanceof AirCompressorContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((AirCompressorContainer) openContainer).getTileEntity();
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
                    BlockEntity tileEntity = ((AqueoulizerContainer) openContainer).getTileEntity();
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
                    BlockEntity tileEntity = ((CentrifugalAgitatorContainer) openContainer).getTileEntity();
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
                    BlockEntity tileEntity = ((CombustionGeneratorContainer) openContainer).getTileEntity();
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
                    BlockEntity tileEntity = ((DistillationUnitContainer) openContainer).getTileEntity();
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
                    BlockEntity tileEntity = ((GasFiredFurnaceContainer) openContainer).getTileEntity();
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
                    BlockEntity tileEntity = ((PumpContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof PumpTile) {
                        ((PumpTile) tileEntity).updateTankPacketFromGui(packet.status, packet.id);
                    }
                    // End of Server side logic
                } else {
                    ((PumpContainer) openContainer).updateStatusTank(packet.status, packet.id);
                }
                // End of Pump
            } else if (openContainer instanceof BlastFurnaceContainer){
                if (onServer) {
                    BlockEntity tileEntity = ((BlastFurnaceContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof BlastFurnaceTile) {
                        ((BlastFurnaceTile) tileEntity).updateTankPacketFromGui(packet.status, packet.id);
                    }
                    // End of Server side logic
                } else {
                    ((BlastFurnaceContainer) openContainer).updateStatusTank(packet.status, packet.id);
                }
                // End of Blast Furnace
            } else {
                VoluminousEnergy.LOGGER.warn("TankBoolPacket: Not a valid container.");
            }
        } else {
            VoluminousEnergy.LOGGER.warn("TankBoolPacket The container is null");
        }
    }
}
