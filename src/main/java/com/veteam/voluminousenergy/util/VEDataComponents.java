package com.veteam.voluminousenergy.util;

import com.mojang.serialization.Codec;
import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VEDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPE_DEFERRED_REGISTER
            = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, VoluminousEnergy.MODID);

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

}
