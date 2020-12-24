package com.veteam.voluminousenergy.tools.networking;

import com.veteam.voluminousenergy.blocks.containers.CrusherContainer;
import com.veteam.voluminousenergy.blocks.tiles.CrusherTile;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
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
        ServerPlayerEntity playerEntity = contextSupplier.get().getSender();
        contextSupplier.get().enqueueWork(() -> handlePacket(packet,playerEntity));
    }

    public static void handlePacket(BoolButtonPacket packet, ServerPlayerEntity playerEntity){
        if(playerEntity != null){
            if(playerEntity.openContainer instanceof CrusherContainer){
                TileEntity tileEntity = ((CrusherContainer) playerEntity.openContainer).tileEntity;
                if (tileEntity instanceof CrusherTile){
                    ((CrusherTile) tileEntity).updatePacketFromGui(packet.status, packet.slotId);
                }
            }
        }
    }
}
