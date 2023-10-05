package com.veteam.voluminousenergy.util.recipe.serializers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import com.veteam.voluminousenergy.util.recipe.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class VEFluidInputNoOutputRecipeSerializer implements RecipeSerializer<CombustionGeneratorOxidizerRecipe> {

    public static final Codec<CombustionGeneratorOxidizerRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            VERecipeCodecs.VE_FLUID_INGREDIENT_CODEC.listOf().fieldOf("fluid_ingredients").forGetter((getter) -> getter.fluidIngredientList),
            Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime)
    ).apply(instance, CombustionGeneratorOxidizerRecipe::new));

    private static final FluidSerializerHelper<CombustionGeneratorOxidizerRecipe> helper = new FluidSerializerHelper<>();

    @Nullable
    @Override
    public CombustionGeneratorOxidizerRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
        return helper.fromNetwork(new CombustionGeneratorOxidizerRecipe(), buffer);
    }

    @Override
    public @NotNull Codec<CombustionGeneratorOxidizerRecipe> codec() {
        return VE_RECIPE_CODEC;
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull CombustionGeneratorOxidizerRecipe recipe) {
        helper.toNetwork(buffer, recipe);
    }
}