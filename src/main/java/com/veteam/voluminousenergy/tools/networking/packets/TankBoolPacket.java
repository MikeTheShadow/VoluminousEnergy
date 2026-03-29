package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.veteam.voluminousenergy.VoluminousEnergy.MODID;

public class TankBoolPacket {

    public record TankBoolPacketPayload(boolean status, int tankId) implements CustomPacketPayload {

        public static final Type<TankBoolPacketPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "tank_bool"));

        public static final StreamCodec<FriendlyByteBuf, TankBoolPacketPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                TankBoolPacketPayload::status,
                ByteBufCodecs.INT,
                TankBoolPacketPayload::tankId,
                TankBoolPacketPayload::new);

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void handle(TankBoolPacketPayload packet, IPayloadContext contextSupplier) {
        AbstractContainerMenu container = contextSupplier.player().containerMenu;
        handlePacket(packet, container);
    }

    public static void handlePacket(TankBoolPacketPayload packet, AbstractContainerMenu openContainer) {
        if (openContainer instanceof VEContainer VEContainer) {
            BlockEntity tileEntity = VEContainer.getTileEntity();
            if (tileEntity instanceof VETileEntity VETileEntity) {
                VETileEntity.updateTankPacketFromGui(packet.status(), packet.tankId());
                VETileEntity.setChanged();
            }
        }
    }
}
