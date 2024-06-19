package com.veteam.voluminousenergy.util.recipe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.stream.Stream;

public class VEFluidIngredientSerializer {
    public static final VEFluidIngredientSerializer INSTANCE = new VEFluidIngredientSerializer();

    public FluidIngredient parse(RegistryFriendlyByteBuf buffer) {
        FluidStack stack = FluidStack.STREAM_CODEC.decode(buffer);
        return FluidIngredient.fromValues(Stream.generate(() -> new FluidIngredient.FluidValue(FluidStack.STREAM_CODEC.decode(buffer), stack.getAmount(), stack.getFluid())).limit(buffer.readVarInt()));
    }

    public void write(RegistryFriendlyByteBuf buffer, FluidIngredient ingredient) {
        FluidStack[] fluids = ingredient.getFluids();
        buffer.writeVarInt(fluids.length);

        for (FluidStack stack : fluids)
            FluidStack.STREAM_CODEC.encode(buffer,stack);
    }
}