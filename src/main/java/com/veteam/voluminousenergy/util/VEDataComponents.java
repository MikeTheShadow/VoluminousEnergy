package com.veteam.voluminousenergy.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.VoluminousEnergy;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VEDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPE_DEFERRED_REGISTER
            = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, VoluminousEnergy.MODID);

    // DATA COMPONENTS

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FluidStack>> FLUID_STACK_DATA =
            DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register("fluid_stack_data",
                    () -> {
                        DataComponentType.Builder<FluidStack> stackBuilder = DataComponentType.builder();
                        return stackBuilder.persistent(FluidStack.CODEC)
                                .networkSynchronized(FluidStack.STREAM_CODEC)
                                .build();
                    }
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> MULTIPLIER_DATA =
            DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register("multiplier_data",
                    () -> {
                        DataComponentType.Builder<Float> stackBuilder = DataComponentType.builder();
                        return stackBuilder.persistent(Codec.FLOAT)
                                .networkSynchronized(ByteBufCodecs.FLOAT)
                                .build();
                    }
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_JEI =
            DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register("is_jei",
                    () -> {
                        DataComponentType.Builder<Boolean> stackBuilder = DataComponentType.builder();
                        return stackBuilder.persistent(Codec.BOOL)
                                .networkSynchronized(ByteBufCodecs.BOOL)
                                .build();
                    }
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SOLARIUM_DURABILITY_BONUS =
            DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register("solarium_durability_bonus",
                    () -> {
                        DataComponentType.Builder<Integer> stackBuilder = DataComponentType.builder();
                        return stackBuilder.persistent(Codec.INT)
                                .networkSynchronized(ByteBufCodecs.INT)
                                .build();
                    });

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MECHANICAL_ENERGY =
            DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register("mechanical_energy",
                    () -> {
                        DataComponentType.Builder<Integer> stackBuilder = DataComponentType.builder();
                        return stackBuilder.persistent(Codec.INT)
                                .networkSynchronized(ByteBufCodecs.INT)
                                .build();
                    });

    // SPECIAL CASES BELOW

    public static final Codec<ChunkFluidData> CHUNK_FLUID_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("x").forGetter(ChunkFluidData::x),
                    Codec.INT.fieldOf("z").forGetter(ChunkFluidData::z),
                    FluidStack.CODEC.listOf().fieldOf("fluids").forGetter(ChunkFluidData::fluids)
            ).apply(instance, ChunkFluidData::new)
    );


    public static final StreamCodec<RegistryFriendlyByteBuf, List<FluidStack>> FLUID_STACK_LIST_STREAM_CODEC = new StreamCodec<>() {

        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull List<FluidStack> fluidStacks) {

            int total = fluidStacks.size();
            buf.writeVarInt(total);

            for (FluidStack fluidStack : fluidStacks) {

                if (fluidStack.isEmpty())
                    throw new EncoderException("Empty FluidStack not allowed for chunk fluid!");

                FluidStack.OPTIONAL_STREAM_CODEC.encode(buf, fluidStack);
            }
        }

        @Override
        public @NotNull List<FluidStack> decode(@NotNull RegistryFriendlyByteBuf buf) {

            int total = buf.readVarInt();
            List<FluidStack> stacks = new ArrayList<>();

            for (int i = 0; i < total; i++) {
                FluidStack stack = FluidStack.OPTIONAL_STREAM_CODEC.decode(buf);
                if (stack.isEmpty())
                    throw new DecoderException("Empty FluidStack not allowed");
                stacks.add(stack);
            }
            return stacks;
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, ChunkFluidData> CHUNK_FLUID_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ChunkFluidData::x,
            ByteBufCodecs.INT, ChunkFluidData::z,
            FLUID_STACK_LIST_STREAM_CODEC, ChunkFluidData::fluids,
            ChunkFluidData::new
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ChunkFluidData>> CHUNK_FLUID_DATA =
            DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register("chunk_fluid_data",
                    () -> {
                        DataComponentType.Builder<ChunkFluidData> stackBuilder = DataComponentType.builder();
                        return stackBuilder.persistent(CHUNK_FLUID_CODEC)
                                .networkSynchronized(CHUNK_FLUID_STREAM_CODEC)
                                .build();
                    }
            );

    public record ChunkFluidData(int x, int z, List<FluidStack> fluids) {

    }
}
