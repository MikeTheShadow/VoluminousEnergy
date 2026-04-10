package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.veteam.voluminousenergy.VoluminousEnergy.MODID;

public class TankDirectionPacket {
    public record TankDirectionPayload(int direction, int tankId) implements CustomPacketPayload {

        public static final Type<TankDirectionPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MODID, "tank_direction"));

        public static final StreamCodec<FriendlyByteBuf, TankDirectionPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                TankDirectionPayload::direction,
                ByteBufCodecs.INT,
                TankDirectionPayload::tankId,
                TankDirectionPayload::new);

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void handle(TankDirectionPayload packet, IPayloadContext contextSupplier) {
        AbstractContainerMenu container = contextSupplier.player().containerMenu;
        handlePacket(packet, container);
    }

    public static void handlePacket(TankDirectionPayload packet, AbstractContainerMenu openContainer) {
        if (openContainer instanceof VEContainer VEContainer) {
            BlockEntity tileEntity = VEContainer.getTileEntity();
            if (tileEntity instanceof VETileEntity VETileEntity) {
                VETileEntity.updateTankPacketFromGui(packet.direction(), packet.tankId());
                VETileEntity.setChanged();
            }
        } else {
            VoluminousEnergy.LOGGER.warn("TankDirectionPacket: Not a valid container.");
        }
    }
}
