package com.veteam.voluminousenergy.util;

import com.mojang.serialization.Codec;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.util.records.ChunkFluidData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

import static com.veteam.voluminousenergy.util.VECodecs.CHUNK_FLUID_CODEC;
import static com.veteam.voluminousenergy.util.VECodecs.CHUNK_FLUID_STREAM_CODEC;

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

    public static final Supplier<DataComponentType<ChunkFluidData>> CHUNK_FLUID_DATA =
            DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register("chunk_fluid_data",
                    () -> {
                        DataComponentType.Builder<ChunkFluidData> stackBuilder = DataComponentType.builder();
                        return stackBuilder.persistent(CHUNK_FLUID_CODEC)
                                .networkSynchronized(CHUNK_FLUID_STREAM_CODEC)
                                .build();
                    }
            );

    public static final Supplier<DataComponentType<SimpleFluidContent>> SIMPLE_FLUID_DATA_TYPE =
            DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register("item_fluid_data",
                    () -> {
                        DataComponentType.Builder<SimpleFluidContent> stackBuilder = DataComponentType.builder();
                        return stackBuilder.persistent(SimpleFluidContent.CODEC)
                                .networkSynchronized(SimpleFluidContent.STREAM_CODEC)
                                .build();
                    }
            );
}
