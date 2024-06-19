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

public class BoolButtonPacket {

    public record BoolButtonPayload(boolean status, int slotId) implements CustomPacketPayload {

        public static final Type<BoolButtonPayload> TYPE = new Type<>(new ResourceLocation(MODID,"bool_button"));

        public static final StreamCodec<FriendlyByteBuf,BoolButtonPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                BoolButtonPayload::status,
                ByteBufCodecs.INT,
                BoolButtonPayload::slotId,
                BoolButtonPayload::new);

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void handle(BoolButtonPayload packet, IPayloadContext contextSupplier) {
        AbstractContainerMenu container = contextSupplier.player().containerMenu;
        handlePacket(packet, container);
    }

    public static void handlePacket(BoolButtonPayload packet, AbstractContainerMenu openContainer) {
        if (openContainer != null) {
            if (openContainer instanceof VEContainer VEContainer) {

                BlockEntity tileEntity = VEContainer.getTileEntity();
                if (tileEntity instanceof VETileEntity VETileEntity) {
                    VETileEntity.updatePacketFromGui(packet.status(), packet.slotId());
                    VETileEntity.setChanged();
                }
                VEContainer.updateStatusButton(packet.status(), packet.slotId());
            }
        }
    }
}
