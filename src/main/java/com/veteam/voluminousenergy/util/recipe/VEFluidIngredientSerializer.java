package com.veteam.voluminousenergy.util.recipe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;

import java.util.stream.Stream;

public class VEFluidIngredientSerializer
{
    public static final VEFluidIngredientSerializer INSTANCE  = new VEFluidIngredientSerializer();

    public FluidIngredient parse(FriendlyByteBuf buffer)
    {
        return FluidIngredient.fromValues(Stream.generate(() -> new FluidIngredient.FluidValue(buffer.readFluidStack())).limit(buffer.readVarInt()));
    }

    public void write(FriendlyByteBuf buffer, FluidIngredient ingredient)
    {
        FluidStack[] fluids = ingredient.getFluids();
        buffer.writeVarInt(fluids.length);

        for (FluidStack stack : fluids)
            buffer.writeFluidStack(stack);
    }
}