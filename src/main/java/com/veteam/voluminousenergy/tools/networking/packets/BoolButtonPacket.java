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

    public static void handle(BoolButtonPacket packet, CustomPayloadEvent.Context contextSupplier){
        //VoluminousEnergy.LOGGER.debug(contextSupplier.get().getDirection());
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

    public static void handlePacket(BoolButtonPacket packet, AbstractContainerMenu openContainer, boolean onServer){
        if(openContainer != null){

            if(openContainer instanceof VoluminousContainer voluminousContainer){
                if(onServer){
                    BlockEntity tileEntity = voluminousContainer.getTileEntity();
                    if (tileEntity instanceof VETileEntity VETileEntity){
                        VETileEntity.updatePacketFromGui(packet.status, packet.slotId);
                        VETileEntity.setChanged();
                    }
                } else {
                    voluminousContainer.updateStatusButton(packet.status, packet.slotId);
                }
            }
        else {
                VoluminousEnergy.LOGGER.warn("BoolButtonPacket: Not a valid container.");
            }
        } else {
            VoluminousEnergy.LOGGER.warn("BoolButtonPacket: The container is null");
        }
    }

}
