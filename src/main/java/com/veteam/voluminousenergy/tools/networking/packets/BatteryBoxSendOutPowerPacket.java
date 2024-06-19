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

public class BatteryBoxSendOutPowerPacket {

    public record BatteryBoxSendOutPowerPayload(boolean status) implements CustomPacketPayload {

        public static final Type<BatteryBoxSendOutPowerPayload> TYPE = new Type<>(new ResourceLocation(MODID, "battery_box_send_out_power"));

        public static final StreamCodec<FriendlyByteBuf, BatteryBoxSendOutPowerPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                BatteryBoxSendOutPowerPayload::status,
                BatteryBoxSendOutPowerPayload::new);

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void handle(BatteryBoxSendOutPowerPayload packet, IPayloadContext contextSupplier) {
        AbstractContainerMenu container = contextSupplier.player().containerMenu;
        handlePacket(packet, container);
    }

    public static void handlePacket(BatteryBoxSendOutPowerPayload packet, AbstractContainerMenu openContainer) {
        if (openContainer instanceof VEContainer container) {
            BlockEntity tileEntity = container.getTileEntity();
            if (tileEntity instanceof VETileEntity tile) {
                tile.setSendsOutPower(packet.status());
                tile.setChanged();
            }
        }
    }
}
