package com.veteam.voluminousenergy.util.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Function;

public class VERecipeCodecs {
    private static final Codec<Item> ITEM_NONAIR_CODEC = ExtraCodecs.validate(BuiltInRegistries.ITEM.byNameCodec(), (p_297750_) -> {
        return p_297750_ == Items.AIR ? DataResult.error(() -> {
            return "Crafting result must not be minecraft:air";
        }) : DataResult.success(p_297750_);
    });

    // TODO test to see if it's possible for the count to be set inside the ingredient object
    public static final Codec<Ingredient> VE_INGREDIENT_CODEC = RecordCodecBuilder.create((p_298321_) -> {
        return p_298321_.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(Function.identity()),
                ExtraCodecs.strictOptionalField(ExtraCodecs.POSITIVE_INT, "count", 1)
                        .forGetter((ingredient) -> 1)  // Dummy getter, we're not using this to get anything
        ).apply(p_298321_, VERecipeCodecs::modifyIngredientCount);
    });

    private static Ingredient modifyIngredientCount(Ingredient ingredient, int count) {
        ItemStack[] stacks = ingredient.getItems();
        for (ItemStack stack : stacks) {
            stack.setCount(count);
        }
        return Ingredient.of(stacks);
    }

    public static final Codec<ItemStack> VE_OUTPUT_ITEM_CODEC = RecordCodecBuilder.create((p_298321_) -> {
        return p_298321_.group(ITEM_NONAIR_CODEC.fieldOf("item").forGetter(ItemStack::getItem), ExtraCodecs.strictOptionalField(ExtraCodecs.POSITIVE_INT, "count", 1).forGetter(ItemStack::getCount)).apply(p_298321_, ItemStack::new);
    });

    // ExtraCodecs.strictOptionalField(ExtraCodecs.POSITIVE_FLOAT,"chance",1.0F).forGetter(Float::new)

    public static final Codec<Fluid> FLUID_NONAIR_CODEC = ExtraCodecs.validate(BuiltInRegistries.FLUID.byNameCodec(), (p_297750_) -> {
        return p_297750_ == Fluids.EMPTY ? DataResult.error(() -> {
            return "Crafting result must not be fluid:empty";
        }) : DataResult.success(p_297750_);
    });
    public static final Codec<FluidIngredient> VE_FLUID_INGREDIENT_CODEC = RecordCodecBuilder.create((p_298321_) -> {
        return p_298321_.group(FluidIngredient.CODEC.fieldOf("fluid").forGetter(FluidIngredient::new))
                .apply(p_298321_, FluidIngredient::new);
    });

    public static final Codec<FluidStack> VE_OUTPUT_FLUID_CODEC = RecordCodecBuilder.create((p_298321_) -> {
        return p_298321_.group(FLUID_NONAIR_CODEC.fieldOf("fluid").forGetter(FluidStack::getFluid),
                ExtraCodecs.strictOptionalField(ExtraCodecs.POSITIVE_INT, "amount", 1)
                        .forGetter(FluidStack::getAmount)).apply(p_298321_, FluidStack::new);
    });

}
