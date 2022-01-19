package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.blocks.containers.VoluminousContainer;
import com.veteam.voluminousenergy.blocks.tiles.VoluminousTileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
        if (openContainer == null) return;
        if (openContainer instanceof VoluminousContainer voluminousContainer) {
            VoluminousTileEntity voluminousTileEntity = (VoluminousTileEntity) voluminousContainer.getTileEntity();
            voluminousTileEntity.uuidPacket(packet.uuid, packet.connection);
            voluminousTileEntity.setChanged();
        }
    }
}
