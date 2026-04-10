package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.blocks.containers.VEContainer;
import net.minecraft.client.Minecraft;
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

public class BatteryBoxSlotPairPacket {

    public record BatteryBoxSlotPairPayload(boolean status, int slotId) implements CustomPacketPayload {

        public static final Type<BatteryBoxSlotPairPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MODID, "battery_slot_pair"));

        public static final StreamCodec<FriendlyByteBuf, BatteryBoxSlotPairPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                BatteryBoxSlotPairPayload::status,
                ByteBufCodecs.INT,
                BatteryBoxSlotPairPayload::slotId,
                BatteryBoxSlotPairPayload::new);

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void handle(BatteryBoxSlotPairPayload packet, IPayloadContext contextSupplier) {
        AbstractContainerMenu container = contextSupplier.player().containerMenu;
        handlePacket(packet, container);
    }

    public static void handlePacket(BatteryBoxSlotPairPayload packet, AbstractContainerMenu openContainer) {
        if (openContainer != null) {
            if (openContainer instanceof VEContainer batteryBoxContainer) {
                    BlockEntity tileEntity = batteryBoxContainer.getTileEntity();
//                    if (tileEntity instanceof BatteryBoxTile batteryBoxTile) {
//                        batteryBoxTile.updateSlotPair(packet.status, packet.id);
//                        batteryBoxTile.setChanged();
//                    }
                }
        }
    }
}
