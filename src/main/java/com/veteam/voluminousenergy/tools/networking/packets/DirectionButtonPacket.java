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
                Container clientContainer = Minecraft.getInstance().player.containerMenu;
                contextSupplier.get().enqueueWork(() -> handlePacket(packet,clientContainer,false));
                break;
            default:
                Container serverContainer = (contextSupplier.get().getSender()).containerMenu;
                contextSupplier.get().enqueueWork(() -> handlePacket(packet,serverContainer,true));
        }

    }

    public static void handlePacket(DirectionButtonPacket packet, Container openContainer, boolean onServer){
        if(openContainer != null){
            // CRUSHER
            if(openContainer instanceof CrusherContainer){ // Crusher
                if(onServer){
                    TileEntity tileEntity = ((CrusherContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof CrusherTile){
                        ((CrusherTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((CrusherContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
            // AIR COMPRESSOR
            } else if (openContainer instanceof AirCompressorContainer) { // Air Compressor
                if (onServer) {
                    TileEntity tileEntity = ((AirCompressorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof AirCompressorTile) {
                        ((AirCompressorTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((AirCompressorContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
            // AQUEOULIZER
            } else if(openContainer instanceof AqueoulizerContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((AqueoulizerContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof AqueoulizerTile) {
                        ((AqueoulizerTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((AqueoulizerContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // CENTRIFUGAL AGITATOR CONTAINER
            } else if(openContainer instanceof CentrifugalAgitatorContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((CentrifugalAgitatorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof CentrifugalAgitatorTile) {
                        ((CentrifugalAgitatorTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((CentrifugalAgitatorContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // COMBUSTION GENERATOR
            } else if (openContainer instanceof CombustionGeneratorContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((CombustionGeneratorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof CombustionGeneratorTile) {
                        ((CombustionGeneratorTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((CombustionGeneratorContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // COMPRESSOR
            } else if (openContainer instanceof CompressorContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((CompressorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof CompressorTile) {
                        ((CompressorTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((CompressorContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // DISTILLATION UNIT
            } else if (openContainer instanceof DistillationUnitContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((DistillationUnitContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof DistillationUnitTile) {
                        ((DistillationUnitTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((DistillationUnitContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // ELECTRIC FURNACE
            } else if (openContainer instanceof ElectricFurnaceContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((ElectricFurnaceContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof ElectricFurnaceTile) {
                        ((ElectricFurnaceTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((ElectricFurnaceContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // ELECTROLYZER
            } else if(openContainer instanceof ElectrolyzerContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((ElectrolyzerContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof ElectrolyzerTile) {
                        ((ElectrolyzerTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((ElectrolyzerContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // GAS FIRED FURNACE
            } else if(openContainer instanceof GasFiredFurnaceContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((GasFiredFurnaceContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof GasFiredFurnaceTile) {
                        ((GasFiredFurnaceTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((GasFiredFurnaceContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // Primitive Blast Furnace
            } else if(openContainer instanceof PrimitiveBlastFurnaceContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((PrimitiveBlastFurnaceContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof PrimitiveBlastFurnaceTile) {
                        ((PrimitiveBlastFurnaceTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((PrimitiveBlastFurnaceContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // Primitive Stirling Generator Container
            } else if (openContainer instanceof PrimitiveStirlingGeneratorContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((PrimitiveStirlingGeneratorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof PrimitiveStirlingGeneratorTile) {
                        ((PrimitiveStirlingGeneratorTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((PrimitiveStirlingGeneratorContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // Pump
            } else if (openContainer instanceof PumpContainer) {
                if (onServer) {
                    TileEntity tileEntity = ((PumpContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof PumpTile) {
                        ((PumpTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((PumpContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // Stirling Generator
            } else if (openContainer instanceof StirlingGeneratorContainer){
                if (onServer) {
                    TileEntity tileEntity = ((StirlingGeneratorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof StirlingGeneratorTile) {
                        ((StirlingGeneratorTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((StirlingGeneratorContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // Battery Box
            } else if (openContainer instanceof BatteryBoxContainer){
                if (onServer) {
                    TileEntity tileEntity = ((BatteryBoxContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof BatteryBoxTile) {
                        ((BatteryBoxTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((BatteryBoxContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // Centrifugal Separator
            } else if (openContainer instanceof CentrifugalSeparatorContainer){
                if (onServer) {
                    TileEntity tileEntity = ((CentrifugalSeparatorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof CentrifugalSeparatorTile) {
                        ((CentrifugalSeparatorTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((CentrifugalSeparatorContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
            } else if (openContainer instanceof ImplosionCompressorContainer){
                if (onServer) {
                    TileEntity tileEntity = ((ImplosionCompressorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof ImplosionCompressorTile) {
                        ((ImplosionCompressorTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((ImplosionCompressorContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
            } else {
                VoluminousEnergy.LOGGER.warn("DirectionButtonPacket: Not a valid container.");
            }
        } else {
            VoluminousEnergy.LOGGER.warn("DirectionButtonPacket: The container is null.");
        }
    }
}
