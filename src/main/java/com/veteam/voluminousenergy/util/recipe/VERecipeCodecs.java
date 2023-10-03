package com.veteam.voluminousenergy.util.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

public class VERecipeCodecs {
    private static final Codec<Item> ITEM_NONAIR_CODEC = ExtraCodecs.validate(BuiltInRegistries.ITEM.byNameCodec(), (p_297750_) -> {
        return p_297750_ == Items.AIR ? DataResult.error(() -> {
            return "Crafting result must not be minecraft:air";
        }) : DataResult.success(p_297750_);
    });
    public static final Codec<VEIngredientItem> VE_INGREDIENT_ITEM_CODEC = RecordCodecBuilder.create((p_298321_) -> {
        return p_298321_.group(Ingredient.CODEC.fieldOf("ingredient").forGetter(VEIngredientItem::ingredient),
                ExtraCodecs.strictOptionalField(ExtraCodecs.POSITIVE_INT, "count", 1)
                        .forGetter(VEIngredientItem::count)).apply(p_298321_, VEIngredientItem::new);
    });

    public static final Codec<VEOutputItem> VE_OUTPUT_ITEM_CODEC = RecordCodecBuilder.create((p_298321_) -> {
        return p_298321_.group(ITEM_NONAIR_CODEC.fieldOf("item").forGetter(VEOutputItem::item),
                ExtraCodecs.strictOptionalField(ExtraCodecs.POSITIVE_INT, "count", 1)
                        .forGetter(VEOutputItem::amount),
                ExtraCodecs.strictOptionalField(ExtraCodecs.POSITIVE_FLOAT, "chance", 1.0F).forGetter(VEOutputItem::rng)).apply(p_298321_, VEOutputItem::new);
    });

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
