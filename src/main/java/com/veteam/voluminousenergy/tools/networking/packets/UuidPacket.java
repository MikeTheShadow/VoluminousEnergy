package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.blocks.containers.*;
import com.veteam.voluminousenergy.blocks.tiles.ImplosionCompressorTile;
import com.veteam.voluminousenergy.blocks.tiles.VoluminousTileEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.network.NetworkEvent;

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

    public static UuidPacket fromBytes(PacketBuffer buffer){
        UuidPacket packet = new UuidPacket();
        packet.uuid = buffer.readUUID();
        packet.connection = buffer.readBoolean();
        return packet;
    }

    public void toBytes(PacketBuffer buffer){
        buffer.writeUUID(this.uuid);
        buffer.writeBoolean(this.connection);
    }

    public static void handle(UuidPacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        Container serverContainer = (contextSupplier.get().getSender()).containerMenu;
        contextSupplier.get().enqueueWork(() -> handlePacket(packet,serverContainer));

    }

    public static void handlePacket(UuidPacket packet, Container openContainer){
        if(openContainer != null){
            TileEntity tileEntity = null;
            if(openContainer instanceof CrusherContainer)
                tileEntity = ((CrusherContainer) openContainer).tileEntity;
            else if (openContainer instanceof AirCompressorContainer)
                tileEntity = ((AirCompressorContainer) openContainer).tileEntity;
            else if (openContainer instanceof AqueoulizerContainer)
                tileEntity = ((AqueoulizerContainer) openContainer).tileEntity;
            else if (openContainer instanceof CentrifugalAgitatorContainer)
                tileEntity = ((CentrifugalAgitatorContainer) openContainer).tileEntity;
            else if (openContainer instanceof CombustionGeneratorContainer)
                tileEntity = ((CombustionGeneratorContainer) openContainer).tileEntity;
            else if (openContainer instanceof CompressorContainer)
                tileEntity = ((CompressorContainer) openContainer).tileEntity;
            else if (openContainer instanceof DistillationUnitContainer)
                tileEntity = ((DistillationUnitContainer) openContainer).tileEntity;
            else if (openContainer instanceof ElectricFurnaceContainer)
                tileEntity = ((ElectricFurnaceContainer) openContainer).tileEntity;
            else if (openContainer instanceof ElectrolyzerContainer)
                tileEntity = ((ElectrolyzerContainer) openContainer).tileEntity;
            else if (openContainer instanceof GasFiredFurnaceContainer)
                tileEntity = ((GasFiredFurnaceContainer) openContainer).tileEntity;
            else if (openContainer instanceof  PrimitiveBlastFurnaceContainer)
                tileEntity = ((PrimitiveBlastFurnaceContainer) openContainer).tileEntity;
            else if (openContainer instanceof PrimitiveStirlingGeneratorContainer)
                tileEntity = ((PrimitiveStirlingGeneratorContainer) openContainer).getTileEntity();
            else if (openContainer instanceof PumpContainer)
                tileEntity = ((PumpContainer) openContainer).tileEntity;
            else if (openContainer instanceof StirlingGeneratorContainer)
                tileEntity = ((StirlingGeneratorContainer) openContainer).tileEntity;
            else if (openContainer instanceof BatteryBoxContainer)
                tileEntity = ((BatteryBoxContainer) openContainer).tileEntity;
            else if (openContainer instanceof CentrifugalSeparatorContainer)
                tileEntity = ((CentrifugalSeparatorContainer) openContainer).tileEntity;
            else if (openContainer instanceof ImplosionCompressorContainer)
                tileEntity = ((ImplosionCompressorContainer) openContainer).tileEntity;

            // When tile is set, but not null
            if(tileEntity != null) interactWithTile(packet,tileEntity);
        }
    }

    public static void interactWithTile(UuidPacket packet, TileEntity tileEntity){
        if (tileEntity instanceof VoluminousTileEntity){
            ((VoluminousTileEntity) tileEntity).uuidPacket(packet.uuid, packet.connection);
        }
    }
}
