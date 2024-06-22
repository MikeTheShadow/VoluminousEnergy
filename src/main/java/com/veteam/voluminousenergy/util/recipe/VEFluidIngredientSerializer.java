package com.veteam.voluminousenergy.util.recipe;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class VEFluidIngredientSerializer {
    public static final VEFluidIngredientSerializer INSTANCE = new VEFluidIngredientSerializer();

    public FluidIngredient read(RegistryFriendlyByteBuf buffer) {
        int total = buffer.readVarInt();
        List<FluidStack> fluidStacks = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            fluidStacks.add(FluidStack.STREAM_CODEC.decode(buffer));
        }
        return FluidIngredient.of(fluidStacks.stream());
    }

    public void write(RegistryFriendlyByteBuf buffer, FluidIngredient ingredient) {
        FluidStack[] fluids = ingredient.getFluids();
        buffer.writeVarInt(fluids.length);

        for (FluidStack stack : fluids)
            FluidStack.STREAM_CODEC.encode(buffer,stack);
    }
}