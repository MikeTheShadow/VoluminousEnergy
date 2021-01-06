package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.CrusherContainer;
import com.veteam.voluminousenergy.blocks.tiles.CrusherTile;
import com.veteam.voluminousenergy.blocks.tiles.VoluminousTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.network.NetworkDirection;
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
        packet.uuid = buffer.readUniqueId();
        packet.connection = buffer.readBoolean();
        return packet;
    }

    public void toBytes(PacketBuffer buffer){
        buffer.writeUniqueId(this.uuid);
        buffer.writeBoolean(this.connection);
    }

    public static void handle(UuidPacket packet, Supplier<NetworkEvent.Context> contextSupplier){
        Container serverContainer = (contextSupplier.get().getSender()).openContainer;
        contextSupplier.get().enqueueWork(() -> handlePacket(packet,serverContainer));

    }

    public static void handlePacket(UuidPacket packet, Container openContainer){
        if(openContainer != null){
            if(openContainer instanceof CrusherContainer){
                TileEntity tileEntity = ((CrusherContainer) openContainer).tileEntity;
                interactWithTile(packet,tileEntity);
            }
        }
    }

    public static void interactWithTile(UuidPacket packet, TileEntity tileEntity){
        if (tileEntity instanceof VoluminousTileEntity){
            ((VoluminousTileEntity) tileEntity).uuidPacket(packet.uuid, packet.connection);
        }
    }
}
