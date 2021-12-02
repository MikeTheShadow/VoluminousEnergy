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

    public static DirectionButtonPacket fromBytes(FriendlyByteBuf buffer){
        DirectionButtonPacket packet = new DirectionButtonPacket();
        packet.direction = buffer.readInt();
        packet.slotId = buffer.readInt();
        return packet;
    }

    public void toBytes(FriendlyByteBuf buffer){
        buffer.writeInt(this.direction);
        buffer.writeInt(this.slotId);
    }

    public static void handle(DirectionButtonPacket packet, Supplier<NetworkEvent.Context> contextSupplier){
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

    public static void handlePacket(DirectionButtonPacket packet, AbstractContainerMenu openContainer, boolean onServer){
        if(openContainer != null){
            // CRUSHER
            if(openContainer instanceof CrusherContainer){ // Crusher
                if(onServer){
                    BlockEntity tileEntity = ((CrusherContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof CrusherTile){
                        ((CrusherTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((CrusherContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
            // AIR COMPRESSOR
            } else if (openContainer instanceof AirCompressorContainer) { // Air Compressor
                if (onServer) {
                    BlockEntity tileEntity = ((AirCompressorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof AirCompressorTile) {
                        ((AirCompressorTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((AirCompressorContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
            // AQUEOULIZER
            } else if(openContainer instanceof AqueoulizerContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((AqueoulizerContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof AqueoulizerTile) {
                        ((AqueoulizerTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((AqueoulizerContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // CENTRIFUGAL AGITATOR CONTAINER
            } else if(openContainer instanceof CentrifugalAgitatorContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((CentrifugalAgitatorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof CentrifugalAgitatorTile) {
                        ((CentrifugalAgitatorTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((CentrifugalAgitatorContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // COMBUSTION GENERATOR
            } else if (openContainer instanceof CombustionGeneratorContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((CombustionGeneratorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof CombustionGeneratorTile) {
                        ((CombustionGeneratorTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((CombustionGeneratorContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // COMPRESSOR
            } else if (openContainer instanceof CompressorContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((CompressorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof CompressorTile) {
                        ((CompressorTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((CompressorContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // DISTILLATION UNIT
            } else if (openContainer instanceof DistillationUnitContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((DistillationUnitContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof DistillationUnitTile) {
                        ((DistillationUnitTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((DistillationUnitContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // ELECTRIC FURNACE
            } else if (openContainer instanceof ElectricFurnaceContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((ElectricFurnaceContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof ElectricFurnaceTile) {
                        ((ElectricFurnaceTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((ElectricFurnaceContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // ELECTROLYZER
            } else if(openContainer instanceof ElectrolyzerContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((ElectrolyzerContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof ElectrolyzerTile) {
                        ((ElectrolyzerTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((ElectrolyzerContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // GAS FIRED FURNACE
            } else if(openContainer instanceof GasFiredFurnaceContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((GasFiredFurnaceContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof GasFiredFurnaceTile) {
                        ((GasFiredFurnaceTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((GasFiredFurnaceContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // Primitive Blast Furnace
            } else if(openContainer instanceof PrimitiveBlastFurnaceContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((PrimitiveBlastFurnaceContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof PrimitiveBlastFurnaceTile) {
                        ((PrimitiveBlastFurnaceTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((PrimitiveBlastFurnaceContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // Primitive Stirling Generator Container
            } else if (openContainer instanceof PrimitiveStirlingGeneratorContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((PrimitiveStirlingGeneratorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof PrimitiveStirlingGeneratorTile) {
                        ((PrimitiveStirlingGeneratorTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((PrimitiveStirlingGeneratorContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // Pump
            } else if (openContainer instanceof PumpContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((PumpContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof PumpTile) {
                        ((PumpTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((PumpContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // Stirling Generator
            } else if (openContainer instanceof StirlingGeneratorContainer){
                if (onServer) {
                    BlockEntity tileEntity = ((StirlingGeneratorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof StirlingGeneratorTile) {
                        ((StirlingGeneratorTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((StirlingGeneratorContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // Battery Box
            } else if (openContainer instanceof BatteryBoxContainer){
                if (onServer) {
                    BlockEntity tileEntity = ((BatteryBoxContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof BatteryBoxTile) {
                        ((BatteryBoxTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((BatteryBoxContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
                // Centrifugal Separator
            } else if (openContainer instanceof CentrifugalSeparatorContainer){
                if (onServer) {
                    BlockEntity tileEntity = ((CentrifugalSeparatorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof CentrifugalSeparatorTile) {
                        ((CentrifugalSeparatorTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((CentrifugalSeparatorContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
            } else if (openContainer instanceof ImplosionCompressorContainer){
                if (onServer) {
                    BlockEntity tileEntity = ((ImplosionCompressorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof ImplosionCompressorTile) {
                        ((ImplosionCompressorTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((ImplosionCompressorContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
            } else if (openContainer instanceof BlastFurnaceContainer){
                if (onServer) {
                    BlockEntity tileEntity = ((BlastFurnaceContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof BlastFurnaceTile) {
                        ((BlastFurnaceTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((BlastFurnaceContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
            }  else if (openContainer instanceof ToolingStationContainer){
                if (onServer) {
                    BlockEntity tileEntity = ((ToolingStationContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof ToolingStationTile) {
                        ((ToolingStationTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((ToolingStationContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
            } else if (openContainer instanceof SawmillContainer){
                if (onServer) {
                    BlockEntity tileEntity = ((SawmillContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof SawmillTile) {
                        ((SawmillTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                    }
                } else {
                    ((SawmillContainer) openContainer).updateDirectionButton(packet.direction, packet.slotId);
                }
            } else {
                VoluminousEnergy.LOGGER.warn("DirectionButtonPacket: Not a valid container.");
            }
        } else {
            VoluminousEnergy.LOGGER.warn("DirectionButtonPacket: The container is null.");
        }
    }
}
