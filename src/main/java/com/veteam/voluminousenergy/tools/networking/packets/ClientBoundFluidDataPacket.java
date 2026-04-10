package com.veteam.voluminousenergy.tools.networking.packets;

import com.veteam.voluminousenergy.items.data.CombustibleFluidsData;
import com.veteam.voluminousenergy.items.data.OxidizerFluidsData;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.veteam.voluminousenergy.VoluminousEnergy.MODID;

/**
 * Update the client(s) with server-side fluid information.
 */
public class ClientBoundFluidDataPacket {

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<Fluid>> FLUID_STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.FLUID);

    public record ClientBoundFluidDataPayload(HashMap<Fluid, Integer> combustibleFluids,
                                              HashMap<Fluid, Float> oxidizerFluids) implements CustomPacketPayload {

        public static final Type<ClientBoundFluidDataPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MODID, "client_fluid_data"));

        public static final StreamCodec<RegistryFriendlyByteBuf, ClientBoundFluidDataPayload> STREAM_CODEC = StreamCodec.composite(
                FLUID_INT_HASH_MAP_STREAM_CODEC,
                ClientBoundFluidDataPayload::combustibleFluids,
                FLUID_FLOAT_HASH_MAP_STREAM_CODEC,
                ClientBoundFluidDataPayload::oxidizerFluids,
                ClientBoundFluidDataPayload::new);

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void handle(ClientBoundFluidDataPayload packet, IPayloadContext contextSupplier) {
        CombustibleFluidsData.updateFromPacket(packet.combustibleFluids());
        OxidizerFluidsData.updateFromPacket(packet.oxidizerFluids());
    }

    private static final StreamCodec<RegistryFriendlyByteBuf, HashMap<Fluid, Float>> FLUID_FLOAT_HASH_MAP_STREAM_CODEC = new StreamCodec<>() {
        public @NotNull HashMap<Fluid, Float> decode(@NotNull RegistryFriendlyByteBuf buffer) {

            int totalEntries = ByteBufCodecs.INT.decode(buffer);
            HashMap<Fluid, Float> fluids = new HashMap<>();

            for(int i = 0; i < totalEntries; i++) {
                Holder<Fluid> fluidHolder = FLUID_STREAM_CODEC.decode(buffer);
                float value = ByteBufCodecs.FLOAT.decode(buffer);
                fluids.put(fluidHolder.value(),value);
            }
            return fluids;
        }

        public void encode(@NotNull RegistryFriendlyByteBuf buffer, HashMap<Fluid, Float> map) {
            ByteBufCodecs.INT.encode(buffer, map.size());
            for (Map.Entry<Fluid, Float> entry : map.entrySet()) {
                // builtInRegistryHolder is deprecated but used by Fluidstack#getFluidHolder(). So check that if this breaks
                FLUID_STREAM_CODEC.encode(buffer, entry.getKey().builtInRegistryHolder());
                ByteBufCodecs.FLOAT.encode(buffer, entry.getValue());
            }
        }
    };

    private static final StreamCodec<RegistryFriendlyByteBuf, HashMap<Fluid, Integer>> FLUID_INT_HASH_MAP_STREAM_CODEC = new StreamCodec<>() {

        public @NotNull HashMap<Fluid, Integer> decode(@NotNull RegistryFriendlyByteBuf buffer) {

            int totalEntries = ByteBufCodecs.INT.decode(buffer);
            HashMap<Fluid, Integer> fluids = new HashMap<>();

            for(int i = 0; i < totalEntries; i++) {
                Holder<Fluid> fluidHolder = FLUID_STREAM_CODEC.decode(buffer);
                int value = ByteBufCodecs.INT.decode(buffer);
                fluids.put(fluidHolder.value(),value);
            }
            return fluids;
        }

        public void encode(@NotNull RegistryFriendlyByteBuf buffer, HashMap<Fluid, Integer> map) {
            ByteBufCodecs.INT.encode(buffer, map.size());
            for (Map.Entry<Fluid, Integer> entry : map.entrySet()) {
                // builtInRegistryHolder is deprecated but used by Fluidstack#getFluidHolder(). So check that if this breaks
                FLUID_STREAM_CODEC.encode(buffer, entry.getKey().builtInRegistryHolder());
                ByteBufCodecs.INT.encode(buffer, entry.getValue());
            }
        }
    };
}
