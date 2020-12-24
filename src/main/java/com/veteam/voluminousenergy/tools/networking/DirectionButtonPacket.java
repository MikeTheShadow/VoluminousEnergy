package com.veteam.voluminousenergy.tools.networking;

import com.veteam.voluminousenergy.blocks.containers.CrusherContainer;
import com.veteam.voluminousenergy.blocks.tiles.CrusherTile;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
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
        ServerPlayerEntity playerEntity = contextSupplier.get().getSender();
        contextSupplier.get().enqueueWork(() -> handlePacket(packet,playerEntity));
    }

    public static void handlePacket(DirectionButtonPacket packet, ServerPlayerEntity playerEntity){
        if(playerEntity != null){
            if(playerEntity.openContainer instanceof CrusherContainer){
                TileEntity tileEntity = ((CrusherContainer) playerEntity.openContainer).tileEntity;
                if(tileEntity instanceof CrusherTile){
                    ((CrusherTile) tileEntity).updatePacketFromGui(packet.direction, packet.slotId);
                }
            }
        }
    }
}
