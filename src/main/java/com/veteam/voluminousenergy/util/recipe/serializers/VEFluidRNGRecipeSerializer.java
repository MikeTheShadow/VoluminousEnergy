package com.veteam.voluminousenergy.util.recipe.serializers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.recipe.VEFluidRNGRecipe;
import com.veteam.voluminousenergy.util.recipe.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.CraftingRecipeCodecs;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class VEFluidRNGRecipeSerializer implements RecipeSerializer<VEFluidRNGRecipe> {

    public static final Codec<VEFluidRNGRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            VERecipeCodecs.VE_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.ingredients),
            VERecipeCodecs.VE_FLUID_INGREDIENT_CODEC.listOf().fieldOf("fluid_ingredients").forGetter((getter) -> getter.fluidIngredientList),
            VERecipeCodecs.VE_OUTPUT_FLUID_CODEC.listOf().fieldOf("fluid_results").forGetter((getter) -> getter.fluidOutputList),
            CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.results),
            Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime),
            Codec.FLOAT.listOf().fieldOf("rng_values").forGetter((getter) -> getter.rngValues)
    ).apply(instance, VEFluidRNGRecipe::new));

    private static final FluidSerializerHelper<VEFluidRNGRecipe> helper = new FluidSerializerHelper<>();

    @Nullable
    @Override
    public VEFluidRNGRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
        return helper.fromNetwork(new VEFluidRNGRecipe(), buffer);
    }

    @Override
    public @NotNull Codec<VEFluidRNGRecipe> codec() {
        return VE_RECIPE_CODEC;
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull VEFluidRNGRecipe recipe) {
        helper.toNetwork(buffer, recipe);
    }
}
