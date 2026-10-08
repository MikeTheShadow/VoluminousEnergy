package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.veteam.voluminousenergy.VoluminousEnergy.MODID;

public class TankInteractionPacket {

    public enum Action {
        TAKE,
        PUT
    }

    public record TankInteractionPayload(Action action, int tankId) implements CustomPacketPayload {

        public static final Type<TankInteractionPayload> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(MODID, "tank_interaction"));

        public static final StreamCodec<FriendlyByteBuf, TankInteractionPayload> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public @NotNull TankInteractionPayload decode(@NotNull FriendlyByteBuf buffer) {
                return new TankInteractionPayload(buffer.readEnum(Action.class), buffer.readVarInt());
            }

            @Override
            public void encode(@NotNull FriendlyByteBuf buffer, @NotNull TankInteractionPayload payload) {
                buffer.writeEnum(payload.action());
                buffer.writeVarInt(payload.tankId());
            }
        };

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void handle(TankInteractionPayload packet, IPayloadContext context) {
        context.enqueueWork(() -> handlePacket(packet, context.player()));
    }

    static void handlePacket(TankInteractionPayload packet, Player player) {
        AbstractContainerMenu openContainer = player.containerMenu;
        if (!(openContainer instanceof VEContainer container) || !container.stillValid(player)) {
            return;
        }

        VETileEntity tile = container.getTileEntity();
        if (tile.isRemoved() || tile.getLevel() != player.level()) {
            return;
        }

        tile.interactWithTank(player, openContainer, packet.tankId(), packet.action() == Action.PUT);
    }
}
