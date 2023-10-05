package com.veteam.voluminousenergy.util.recipe.serializers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.util.recipe.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class VEFluidNoIngredientEnergyRecipeSerializer implements RecipeSerializer<CombustionGeneratorFuelRecipe> {

    public static final Codec<CombustionGeneratorFuelRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            VERecipeCodecs.VE_FLUID_INGREDIENT_CODEC.listOf().fieldOf("fluid_ingredients").forGetter((getter) -> getter.fluidIngredientList),
            Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime),
            Codec.INT.fieldOf("volumetric_energy").forGetter(CombustionGeneratorFuelRecipe::getVolumetricEnergy)
    ).apply(instance, CombustionGeneratorFuelRecipe::new));

    private static final FluidSerializerHelper<CombustionGeneratorFuelRecipe> helper = new FluidSerializerHelper<>();

    @Nullable
    @Override
    public CombustionGeneratorFuelRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
        return helper.fromNetwork(new CombustionGeneratorFuelRecipe(), buffer);
    }

    @Override
    public @NotNull Codec<CombustionGeneratorFuelRecipe> codec() {
        return VE_RECIPE_CODEC;
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull CombustionGeneratorFuelRecipe recipe) {
        helper.toNetwork(buffer, recipe);
    }
}

