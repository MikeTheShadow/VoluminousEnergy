package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.VoluminousContainer;
import com.veteam.voluminousenergy.blocks.tiles.VEFluidTileEntity;
import com.veteam.voluminousenergy.blocks.tiles.VoluminousTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TankDirectionPacket {
    private int direction;
    private int tankId;

    public TankDirectionPacket(){
        // Do nothing
    }

    public TankDirectionPacket(int updatedDirection, int id){
        this.direction = updatedDirection;
        this.tankId = id;
    }

    public static TankDirectionPacket fromBytes(FriendlyByteBuf buffer){
        TankDirectionPacket packet = new TankDirectionPacket();
        packet.direction = buffer.readInt();
        packet.tankId = buffer.readInt();
        return packet;
    }

    public void toBytes(FriendlyByteBuf buffer){
        buffer.writeInt(this.direction);
        buffer.writeInt(this.tankId);
    }

    public static void handle(TankDirectionPacket packet, Supplier<NetworkEvent.Context> contextSupplier){
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

    public static void handlePacket(TankDirectionPacket packet, AbstractContainerMenu openContainer, boolean onServer){
        if(openContainer != null){

            if (openContainer instanceof VoluminousContainer voluminousContainer) {
                if (onServer) {
                    BlockEntity tileEntity = voluminousContainer.getTileEntity();
                    if (tileEntity instanceof VEFluidTileEntity veFluidTileEntity) {
                        veFluidTileEntity.updateTankPacketFromGui(packet.direction, packet.tankId);
                        veFluidTileEntity.setChanged();
                    }
                } else {
                    //voluminousContainer.updateDirectionTank(packet.direction, packet.tankId);
                }
            } else {
                VoluminousEnergy.LOGGER.warn("TankDirectionPacket: Not a valid container.");
            }
        } else {
            VoluminousEnergy.LOGGER.warn("TankDirectionPacket: The container is null.");
        }
    }
}
