package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.blocks.containers.*;
import com.veteam.voluminousenergy.blocks.tiles.VoluminousTileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/*
 * THIS IS TO SERVER ONLY
 * The purpose of this class/packet is ensure only the correct player is blasted with packets when appropriate.
 */

public class UuidPacket {
    private UUID uuid;
    private boolean connection;

    public UuidPacket(){
        // Do nothing
    }

    public UuidPacket(UUID uuid, boolean connection){
        this.uuid = uuid;
        this.connection = connection;
    }

    public static UuidPacket fromBytes(FriendlyByteBuf buffer){
        UuidPacket packet = new UuidPacket();
        packet.uuid = buffer.readUUID();
        packet.connection = buffer.readBoolean();
        return packet;
    }

    public void toBytes(FriendlyByteBuf buffer){
        buffer.writeUUID(this.uuid);
        buffer.writeBoolean(this.connection);
    }

    public static void handle(UuidPacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        AbstractContainerMenu serverContainer = (contextSupplier.get().getSender()).containerMenu;
        contextSupplier.get().enqueueWork(() -> handlePacket(packet,serverContainer));
        contextSupplier.get().setPacketHandled(true);
    }

    public static void handlePacket(UuidPacket packet, AbstractContainerMenu openContainer){
        if(openContainer != null){
            BlockEntity tileEntity = null;
            if(openContainer instanceof CrusherContainer)
                tileEntity = ((CrusherContainer) openContainer).getTileEntity();
            else if (openContainer instanceof AirCompressorContainer)
                tileEntity = ((AirCompressorContainer) openContainer).getTileEntity();
            else if (openContainer instanceof AqueoulizerContainer)
                tileEntity = ((AqueoulizerContainer) openContainer).getTileEntity();
            else if (openContainer instanceof CentrifugalAgitatorContainer)
                tileEntity = ((CentrifugalAgitatorContainer) openContainer).getTileEntity();
            else if (openContainer instanceof CombustionGeneratorContainer)
                tileEntity = ((CombustionGeneratorContainer) openContainer).getTileEntity();
            else if (openContainer instanceof CompressorContainer)
                tileEntity = ((CompressorContainer) openContainer).getTileEntity();
            else if (openContainer instanceof DistillationUnitContainer)
                tileEntity = ((DistillationUnitContainer) openContainer).getTileEntity();
            else if (openContainer instanceof ElectricFurnaceContainer)
                tileEntity = ((ElectricFurnaceContainer) openContainer).getTileEntity();
            else if (openContainer instanceof ElectrolyzerContainer)
                tileEntity = ((ElectrolyzerContainer) openContainer).getTileEntity();
            else if (openContainer instanceof GasFiredFurnaceContainer)
                tileEntity = ((GasFiredFurnaceContainer) openContainer).getTileEntity();
            else if (openContainer instanceof  PrimitiveBlastFurnaceContainer)
                tileEntity = ((PrimitiveBlastFurnaceContainer) openContainer).getTileEntity();
            else if (openContainer instanceof PrimitiveStirlingGeneratorContainer)
                tileEntity = ((PrimitiveStirlingGeneratorContainer) openContainer).getTileEntity();
            else if (openContainer instanceof PumpContainer)
                tileEntity = ((PumpContainer) openContainer).getTileEntity();
            else if (openContainer instanceof StirlingGeneratorContainer)
                tileEntity = ((StirlingGeneratorContainer) openContainer).getTileEntity();
            else if (openContainer instanceof BatteryBoxContainer)
                tileEntity = ((BatteryBoxContainer) openContainer).getTileEntity();
            else if (openContainer instanceof CentrifugalSeparatorContainer)
                tileEntity = ((CentrifugalSeparatorContainer) openContainer).getTileEntity();
            else if (openContainer instanceof ImplosionCompressorContainer)
                tileEntity = ((ImplosionCompressorContainer) openContainer).getTileEntity();
            else if (openContainer instanceof BlastFurnaceContainer)
                tileEntity = ((BlastFurnaceContainer) openContainer).getTileEntity();
            else if (openContainer instanceof ToolingStationContainer)
                tileEntity = ((ToolingStationContainer) openContainer).getTileEntity();
            else if (openContainer instanceof SawmillContainer)
                tileEntity = ((SawmillContainer) openContainer).getTileEntity();
            else if (openContainer instanceof TankContainer)
                tileEntity = ((ToolingStationContainer) openContainer).getTileEntity();

            // When tile is set, but not null
            if(tileEntity != null) interactWithTile(packet,tileEntity);
        }
    }

    public static void interactWithTile(UuidPacket packet, BlockEntity tileEntity){
        if (tileEntity instanceof VoluminousTileEntity){
            ((VoluminousTileEntity) tileEntity).uuidPacket(packet.uuid, packet.connection);
        }
    }
}
