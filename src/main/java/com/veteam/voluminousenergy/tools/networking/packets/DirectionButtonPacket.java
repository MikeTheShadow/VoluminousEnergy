package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.blocks.screens.VEContainerScreen;
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

public class DirectionButtonPacket {

    public record DirectionButtonPayload(int direction, int slotId) implements CustomPacketPayload {

        public static final Type<DirectionButtonPayload> TYPE = new Type<>(new ResourceLocation(MODID, "direction_button"));

        public static final StreamCodec<FriendlyByteBuf, DirectionButtonPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                DirectionButtonPayload::direction,
                ByteBufCodecs.INT,
                DirectionButtonPayload::slotId,
                DirectionButtonPayload::new);

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void handle(DirectionButtonPayload packet, IPayloadContext contextSupplier) {
        AbstractContainerMenu container = contextSupplier.player().containerMenu;
        handlePacket(packet, container);
    }

    public static void handlePacket(DirectionButtonPayload packet, AbstractContainerMenu openContainer) {
        if (openContainer instanceof VEContainer veContainer) {
            BlockEntity tileEntity = veContainer.getTileEntity();
            if (tileEntity instanceof VETileEntity VETileEntity) {
                VETileEntity.updatePacketFromGui(packet.direction, packet.slotId);
                VETileEntity.setChanged();
            }
        } else {
            VoluminousEnergy.LOGGER.warn("DirectionButtonPacket: Not a valid container." + openContainer.getClass().getName());
        }
    }
}
