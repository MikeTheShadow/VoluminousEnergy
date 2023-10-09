package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.VoluminousContainer;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

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

    public static void handle(DirectionButtonPacket packet, CustomPayloadEvent.Context contextSupplier){
        NetworkDirection packetDirection = contextSupplier.getDirection();
        switch(packetDirection){
            case PLAY_TO_CLIENT:
                AbstractContainerMenu clientContainer = Minecraft.getInstance().player.containerMenu;
                contextSupplier.enqueueWork(() -> handlePacket(packet,clientContainer,false));
                contextSupplier.setPacketHandled(true);
                break;
            default:
                AbstractContainerMenu serverContainer = (contextSupplier.getSender()).containerMenu;
                contextSupplier.enqueueWork(() -> handlePacket(packet,serverContainer,true));
                contextSupplier.setPacketHandled(true);
        }

    }

    public static void handlePacket(DirectionButtonPacket packet, AbstractContainerMenu openContainer, boolean onServer){
        if(openContainer != null){
            if(openContainer instanceof VoluminousContainer voluminousContainer){
                if(onServer){
                    BlockEntity tileEntity = voluminousContainer.getTileEntity();
                    if (tileEntity instanceof VETileEntity VETileEntity){
                        VETileEntity.updatePacketFromGui(packet.direction, packet.slotId);
                        VETileEntity.setChanged();
                    }
                } else {
                    voluminousContainer.updateDirectionButton(packet.direction, packet.slotId);
                }
            } else {
                VoluminousEnergy.LOGGER.warn("DirectionButtonPacket: Not a valid container."  + openContainer.getClass().getName());
            }
        } else {
            VoluminousEnergy.LOGGER.warn("DirectionButtonPacket: The container is null.");
        }
    }
}
