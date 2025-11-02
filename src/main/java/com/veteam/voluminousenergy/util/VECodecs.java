package com.veteam.voluminousenergy.util;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import com.veteam.voluminousenergy.util.records.*;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VECodecs {

    public static final Codec<FluidPumpData> FLUID_PUMP_DATA_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("x").forGetter(FluidPumpData::x),
            Codec.INT.fieldOf("y").forGetter(FluidPumpData::y),
            Codec.INT.fieldOf("z").forGetter(FluidPumpData::z),
            VERecipeCodecs.FLUID_NONAIR_CODEC.fieldOf("fluid").forGetter(FluidPumpData::fluid)
    ).apply(instance, FluidPumpData::new));

    public static final Codec<CounterLength> COUNTER_LENGTH_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("counter").forGetter(CounterLength::counter),
            Codec.INT.fieldOf("length").forGetter(CounterLength::length)
    ).apply(instance, CounterLength::new));

    public static final Codec<ChunkFluidData> CHUNK_FLUID_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("x").forGetter(ChunkFluidData::x),
                    Codec.INT.fieldOf("z").forGetter(ChunkFluidData::z),
                    FluidStack.CODEC.listOf().fieldOf("fluids").forGetter(ChunkFluidData::fluids)
            ).apply(instance, ChunkFluidData::new)
    );

    public static final Codec<BlastFurnaceData> BLAST_FURNACE_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("temp_celsius").forGetter(BlastFurnaceData::temperatureCelsius),
                    Codec.INT.fieldOf("temp_fahrenheit").forGetter(BlastFurnaceData::temperatureFahrenheit),
                    Codec.INT.fieldOf("temp_kelvin").forGetter(BlastFurnaceData::temperatureKelvin)
            ).apply(instance, BlastFurnaceData::new)
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


}
