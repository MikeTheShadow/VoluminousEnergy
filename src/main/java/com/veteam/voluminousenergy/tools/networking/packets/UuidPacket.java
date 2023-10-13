package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.UUID;

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

    public static void handle(UuidPacket packet, CustomPayloadEvent.Context contextSupplier){
        AbstractContainerMenu serverContainer = (contextSupplier.getSender()).containerMenu;
        //contextSupplier.get().enqueueWork(() -> handlePacket(packet,serverContainer));
        contextSupplier.setPacketHandled(true);
    }

    public static void handlePacket(UuidPacket packet, AbstractContainerMenu openContainer){
        if (openContainer == null) return;
        if (openContainer instanceof VEContainer VEContainer) {
            VETileEntity VETileEntity = (VETileEntity) VEContainer.getTileEntity();
            //voluminousTileEntity.uuidPacket(packet.uuid, packet.connection);
            VETileEntity.setChanged();
        }
    }
}
