package com.veteam.voluminousenergy.util.recipe;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class VEIngredient extends Ingredient {
    protected VEIngredient(Stream<? extends Value> stream) {
        super(stream);
    }

    @Override
    public @NotNull IIngredientSerializer<? extends Ingredient> getSerializer() {
        return net.minecraftforge.common.crafting.VanillaIngredientSerializer.INSTANCE;
    }

    public int getAmount() {
        return this.getItems()[0].getCount();
    }
}
