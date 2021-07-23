package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.*;
import com.veteam.voluminousenergy.blocks.tiles.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

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

    public static BoolButtonPacket fromBytes(FriendlyByteBuf buffer){
        BoolButtonPacket packet = new BoolButtonPacket();
        packet.status = buffer.readBoolean();
        packet.slotId = buffer.readInt();
        return packet;
    }

    public void toBytes(FriendlyByteBuf buffer){
        buffer.writeBoolean(this.status);
        buffer.writeInt(this.slotId);
    }

    public static void handle(BoolButtonPacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        //VoluminousEnergy.LOGGER.debug(contextSupplier.get().getDirection());
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

    public static void handlePacket(BoolButtonPacket packet, AbstractContainerMenu openContainer, boolean onServer){
        if(openContainer != null){
            // CRUSHER
            if(openContainer instanceof CrusherContainer){
                if(onServer){
                    BlockEntity tileEntity = ((CrusherContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof CrusherTile){
                        ((CrusherTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((CrusherContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
            // AIR COMPRESSOR
            } else if(openContainer instanceof AirCompressorContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((AirCompressorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof AirCompressorTile) {
                        ((AirCompressorTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((AirCompressorContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
            // AQUEOULIZER
            } else if (openContainer instanceof AqueoulizerContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((AqueoulizerContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof AqueoulizerTile) {
                        ((AqueoulizerTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((AqueoulizerContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // CENTRIFUGAL AGITATOR
            } else if(openContainer instanceof CentrifugalAgitatorContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((CentrifugalAgitatorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof CentrifugalAgitatorTile) {
                        ((CentrifugalAgitatorTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((CentrifugalAgitatorContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // COMBUSTION GENERATOR
            } else if(openContainer instanceof CombustionGeneratorContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((CombustionGeneratorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof CombustionGeneratorTile) {
                        ((CombustionGeneratorTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((CombustionGeneratorContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // COMPRESSOR
            } else if (openContainer instanceof CompressorContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((CompressorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof CompressorTile) {
                        ((CompressorTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((CompressorContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // DISTILLATION UNIT
            } else if (openContainer instanceof DistillationUnitContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((DistillationUnitContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof DistillationUnitTile) {
                        ((DistillationUnitTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((DistillationUnitContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // ELECTRIC FURNACE
            } else if (openContainer instanceof ElectricFurnaceContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((ElectricFurnaceContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof ElectricFurnaceTile) {
                        ((ElectricFurnaceTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((ElectricFurnaceContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // ELECTROLYZER
            } else if(openContainer instanceof ElectrolyzerContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((ElectrolyzerContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof ElectrolyzerTile) {
                        ((ElectrolyzerTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((ElectrolyzerContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // Gas Fired Furnace
            } else if(openContainer instanceof GasFiredFurnaceContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((GasFiredFurnaceContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof GasFiredFurnaceTile) {
                        ((GasFiredFurnaceTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((GasFiredFurnaceContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // Primitive Blast Furnace
            } else if(openContainer instanceof PrimitiveBlastFurnaceContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((PrimitiveBlastFurnaceContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof PrimitiveBlastFurnaceTile) {
                        ((PrimitiveBlastFurnaceTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((PrimitiveBlastFurnaceContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // Primitive Stirling generator
            } else if(openContainer instanceof PrimitiveStirlingGeneratorContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((PrimitiveStirlingGeneratorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof PrimitiveStirlingGeneratorTile) {
                        ((PrimitiveStirlingGeneratorTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((PrimitiveStirlingGeneratorContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // Pump
            } else if (openContainer instanceof PumpContainer) {
                if (onServer) {
                    BlockEntity tileEntity = ((PumpContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof PumpTile) {
                        ((PumpTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((PumpContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // Stirling Generator
            } else if (openContainer instanceof StirlingGeneratorContainer){
                    if (onServer) {
                        BlockEntity tileEntity = ((StirlingGeneratorContainer) openContainer).getTileEntity();
                        if (tileEntity instanceof StirlingGeneratorTile) {
                            ((StirlingGeneratorTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                        }
                    } else {
                        ((StirlingGeneratorContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                    }
                    // Battery Box
            } else if (openContainer instanceof BatteryBoxContainer){
                if (onServer) {
                    BlockEntity tileEntity = ((BatteryBoxContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof BatteryBoxTile) {
                        ((BatteryBoxTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((BatteryBoxContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // Centrifugal Separator
            } else if (openContainer instanceof CentrifugalSeparatorContainer){
                if (onServer) {
                    BlockEntity tileEntity = ((CentrifugalSeparatorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof CentrifugalSeparatorTile) {
                        ((CentrifugalSeparatorTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((CentrifugalSeparatorContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // Implosion Compressor
            } else if (openContainer instanceof ImplosionCompressorContainer){
                if (onServer) {
                    BlockEntity tileEntity = ((ImplosionCompressorContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof ImplosionCompressorTile) {
                        ((ImplosionCompressorTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((ImplosionCompressorContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
                }
                // Blast Furnace
            } else if (openContainer instanceof BlastFurnaceContainer){
                if (onServer) {
                    BlockEntity tileEntity = ((BlastFurnaceContainer) openContainer).getTileEntity();
                    if (tileEntity instanceof BlastFurnaceTile) {
                        ((BlastFurnaceTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                    }
                } else {
                    ((BlastFurnaceContainer) openContainer).updateStatusButton(packet.status, packet.slotId);
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
