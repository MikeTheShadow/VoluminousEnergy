package com.veteam.voluminousenergy.util.recipe.serializers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.recipe.VERNGExperienceRecipe;
import com.veteam.voluminousenergy.util.recipe.IngredientSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class VERNGExperienceRecipeSerializer implements RecipeSerializer<VERNGExperienceRecipe> {

    public static final Codec<VERNGExperienceRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            VERecipeCodecs.VE_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.ingredients),
            VERecipeCodecs.VE_OUTPUT_ITEM_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.results),
            Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime),
            Codec.FLOAT.listOf().fieldOf("rng_values").forGetter((getter) -> getter.rngValues),
            Codec.INT.fieldOf("min_exp").forGetter((getter) -> getter.processTime),
            Codec.INT.fieldOf("max_exp").forGetter((getter) -> getter.processTime)
    ).apply(instance, VERNGExperienceRecipe::new));

    private static final IngredientSerializerHelper<VERNGExperienceRecipe> helper = new IngredientSerializerHelper<>();

    @Nullable
    @Override
    public VERNGExperienceRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
        return helper.fromNetwork(new VERNGExperienceRecipe(), buffer);
    }

    @Override
    public @NotNull Codec<VERNGExperienceRecipe> codec() {
        return VE_RECIPE_CODEC;
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull VERNGExperienceRecipe recipe) {
        helper.toNetwork(buffer, recipe);
    }
}
