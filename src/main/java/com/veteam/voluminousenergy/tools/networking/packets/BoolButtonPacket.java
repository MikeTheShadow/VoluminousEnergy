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
        //VoluminousEnergy.LOGGER.debug(contextSupplier.get().getDirection());
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

    public static void handlePacket(BoolButtonPacket packet, Container openContainer, boolean onServer){
        if(openContainer != null){
            // CRUSHER
            if(openContainer instanceof CrusherContainer){
                if(onServer){
                    TileEntity tileEntity = ((CrusherContainer) openContainer).tileEntity;
                    if (tileEntity instanceof CrusherTile){
                        ((CrusherTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((CrusherContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
            // AIR COMPRESSOR
            } else if(openContainer instanceof AirCompressorContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((AirCompressorContainer) openContainer).tileEntity;
                    if (tileEntity instanceof AirCompressorTile) {
                        ((AirCompressorTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((AirCompressorContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
            // AQUEOULIZER
            } else if (openContainer instanceof AqueoulizerContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((AqueoulizerContainer) openContainer).tileEntity;
                    if (tileEntity instanceof AqueoulizerTile) {
                        ((AqueoulizerTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((AqueoulizerContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // CENTRIFUGAL AGITATOR
            } else if(openContainer instanceof CentrifugalAgitatorContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((CentrifugalAgitatorContainer) openContainer).tileEntity;
                    if (tileEntity instanceof CentrifugalAgitatorTile) {
                        ((CentrifugalAgitatorTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((CentrifugalAgitatorContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // COMBUSTION GENERATOR
            } else if(openContainer instanceof CombustionGeneratorContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((CombustionGeneratorContainer) openContainer).tileEntity;
                    if (tileEntity instanceof CombustionGeneratorTile) {
                        ((CombustionGeneratorTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((CombustionGeneratorContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // COMPRESSOR
            } else if (openContainer instanceof CompressorContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((CompressorContainer) openContainer).tileEntity;
                    if (tileEntity instanceof CompressorTile) {
                        ((CompressorTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((CompressorContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // DISTILLATION UNIT
            } else if (openContainer instanceof DistillationUnitContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((DistillationUnitContainer) openContainer).tileEntity;
                    if (tileEntity instanceof DistillationUnitTile) {
                        ((DistillationUnitTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((DistillationUnitContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // ELECTRIC FURNACE
            } else if (openContainer instanceof ElectricFurnaceContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((ElectricFurnaceContainer) openContainer).tileEntity;
                    if (tileEntity instanceof ElectricFurnaceTile) {
                        ((ElectricFurnaceTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((ElectricFurnaceContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // ELECTROLYZER
            } else if(openContainer instanceof ElectrolyzerContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((ElectrolyzerContainer) openContainer).tileEntity;
                    if (tileEntity instanceof ElectrolyzerTile) {
                        ((ElectrolyzerTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((ElectrolyzerContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // Gas Fired Furnace
            } else if(openContainer instanceof GasFiredFurnaceContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((GasFiredFurnaceContainer) openContainer).tileEntity;
                    if (tileEntity instanceof GasFiredFurnaceTile) {
                        ((GasFiredFurnaceTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((GasFiredFurnaceContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // Primitive Blast Furnace
            } else if(openContainer instanceof PrimitiveBlastFurnaceContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((PrimitiveBlastFurnaceContainer) openContainer).tileEntity;
                    if (tileEntity instanceof PrimitiveBlastFurnaceTile) {
                        ((PrimitiveBlastFurnaceTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((PrimitiveBlastFurnaceContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // Primitive Stirling generator
            } else if(openContainer instanceof PrimitiveStirlingGeneratorContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((PrimitiveStirlingGeneratorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof PrimitiveStirlingGeneratorTile) {
                        ((PrimitiveStirlingGeneratorTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((PrimitiveStirlingGeneratorContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // Pump
            } else if (openContainer instanceof PumpContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((PumpContainer) openContainer).tileEntity;
                    if (tileEntity instanceof PumpTile) {
                        ((PumpTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((PumpContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // Stirling Generator
            } else if (openContainer instanceof StirlingGeneratorContainer){
                    if (onServer) {
                        TileEntity tileEntity = ((StirlingGeneratorContainer) openContainer).tileEntity;
                        if (tileEntity instanceof StirlingGeneratorTile) {
                            ((StirlingGeneratorTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                        }
                    } else {
                        ((StirlingGeneratorContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                    }
                    // Battery Box
            } else if (openContainer instanceof BatteryBoxContainer){
                if (onServer) {
                    TileEntity tileEntity = ((BatteryBoxContainer) openContainer).tileEntity;
                    if (tileEntity instanceof BatteryBoxTile) {
                        ((BatteryBoxTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((BatteryBoxContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // INVALID TE/Container
            } else {
                VoluminousEnergy.LOGGER.warn("BoolButtonPacket: Not a valid container.");
            }
        } else {
            VoluminousEnergy.LOGGER.warn("BoolButtonPacket: The container is null");
        }
    }
}
