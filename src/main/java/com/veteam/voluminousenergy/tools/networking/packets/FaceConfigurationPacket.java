package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.util.VEFaceIO;
import com.veteam.voluminousenergy.util.VEFaceIO.Mode;
import com.veteam.voluminousenergy.util.VEIOPort;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.veteam.voluminousenergy.VoluminousEnergy.MODID;

public class FaceConfigurationPacket {
    public record FaceConfigurationPayload(Direction face, Mode mode, boolean enabled) implements CustomPacketPayload {
        public static final Type<FaceConfigurationPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "face_configuration"));
        public static final StreamCodec<FriendlyByteBuf, FaceConfigurationPayload> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public @NotNull FaceConfigurationPayload decode(@NotNull FriendlyByteBuf buffer) {
                return new FaceConfigurationPayload(buffer.readEnum(Direction.class), buffer.readEnum(Mode.class), buffer.readBoolean());
            }

            @Override
            public void encode(@NotNull FriendlyByteBuf buffer, @NotNull FaceConfigurationPayload payload) {
                buffer.writeEnum(payload.face());
                buffer.writeEnum(payload.mode());
                buffer.writeBoolean(payload.enabled());
            }
        };

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void handle(FaceConfigurationPayload packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player().containerMenu instanceof VEContainer menu) || !menu.stillValid(context.player())) {
                return;
            }
            VETileEntity tile = menu.getTileEntity();
            if (tile.isRemoved() || tile.getLevel() != context.player().level()) {
                return;
            }
            if (!apply(tile.getFaceIO(), tile.getIOPorts(), packet.face(), packet.mode(), packet.enabled())) {
                tile.updateClients();
                return;
            }
            tile.setChanged();
            tile.updateClients();
            menu.broadcastChanges();
        });
    }

    public static boolean apply(VEFaceIO settings, List<VEIOPort> ports, Direction face, Mode mode, boolean enabled) {
        if (mode != Mode.PUSH && mode != Mode.PULL || enabled && !VEFaceIO.supports(ports, face, mode)) {
            return false;
        }
        settings.setEnabled(face, mode, enabled);
        return true;
    }
}
