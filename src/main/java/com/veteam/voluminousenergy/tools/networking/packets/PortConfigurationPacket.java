package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.util.VEIOPort;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.veteam.voluminousenergy.VoluminousEnergy.MODID;

public class PortConfigurationPacket {
    public enum Setting {
        FACE, UNASSIGN
    }

    public record PortConfigurationPayload(boolean fluid, int portId, Setting setting, int value) implements CustomPacketPayload {
        public static final Type<PortConfigurationPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "port_configuration"));
        public static final StreamCodec<FriendlyByteBuf, PortConfigurationPayload> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public @NotNull PortConfigurationPayload decode(@NotNull FriendlyByteBuf buffer) {
                return new PortConfigurationPayload(buffer.readBoolean(), buffer.readVarInt(), buffer.readEnum(Setting.class), buffer.readVarInt());
            }

            @Override
            public void encode(@NotNull FriendlyByteBuf buffer, @NotNull PortConfigurationPayload payload) {
                buffer.writeBoolean(payload.fluid());
                buffer.writeVarInt(payload.portId());
                buffer.writeEnum(payload.setting());
                buffer.writeVarInt(payload.value());
            }
        };

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void handle(PortConfigurationPayload packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player().containerMenu instanceof VEContainer menu) || !menu.stillValid(context.player())) {
                return;
            }
            VETileEntity tile = menu.getTileEntity();
            if (tile.isRemoved() || tile.getLevel() != context.player().level()) {
                return;
            }
            VEIOPort port = tile.getIOPort(packet.fluid(), packet.portId());
            if (port == null || !apply(port, packet.setting(), packet.value())) {
                return;
            }
            tile.refreshPortFaces();
            tile.setChanged();
            tile.updateClients();
            menu.broadcastChanges();
        });
    }

    public static boolean apply(VEIOPort port, Setting setting, int value) {
        if (setting == Setting.FACE) {
            if (value < 0 || value >= 6) {
                return false;
            }
            port.setDirection(Direction.from3DDataValue(value));
            return true;
        }
        if (value != 0) {
            return false;
        }
        port.setDirection(null);
        return true;
    }
}
