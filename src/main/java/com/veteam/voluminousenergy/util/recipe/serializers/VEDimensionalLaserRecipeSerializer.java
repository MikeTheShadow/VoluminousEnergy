package com.veteam.voluminousenergy.util.recipe.serializers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.recipe.DimensionalLaserRecipe;
import com.veteam.voluminousenergy.util.recipe.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.CraftingRecipeCodecs;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class VEDimensionalLaserRecipeSerializer implements RecipeSerializer<DimensionalLaserRecipe> {

    public static final Codec<DimensionalLaserRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            VERecipeCodecs.VE_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.ingredients),
            VERecipeCodecs.VE_FLUID_INGREDIENT_CODEC.listOf().fieldOf("fluid_ingredients").forGetter((getter) -> getter.fluidIngredientList),
            VERecipeCodecs.VE_OUTPUT_FLUID_CODEC.listOf().fieldOf("fluid_results").forGetter((getter) -> getter.fluidOutputList),
            CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.results),
            Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime),
            Codec.INT.fieldOf("min_amount").forGetter(DimensionalLaserRecipe::getMinimumAmount),
            Codec.INT.fieldOf("max_amount").forGetter(DimensionalLaserRecipe::getMaximumAmount),
            Codec.FLOAT.fieldOf("continentalness_min").forGetter(DimensionalLaserRecipe::getContinentalnessMin),
            Codec.FLOAT.fieldOf("continentalness_max").forGetter(DimensionalLaserRecipe::getContinentalnessMax),

            Codec.FLOAT.fieldOf("erosion_min").forGetter(DimensionalLaserRecipe::getErosionMin),
            Codec.FLOAT.fieldOf("erosion_max").forGetter(DimensionalLaserRecipe::getErosionMax),

            Codec.FLOAT.fieldOf("humidity_min").forGetter(DimensionalLaserRecipe::getHumidityMin),
            Codec.FLOAT.fieldOf("humidity_max").forGetter(DimensionalLaserRecipe::getHumidityMax),

            Codec.FLOAT.fieldOf("temp_min").forGetter(DimensionalLaserRecipe::getTemperatureMin),
            Codec.FLOAT.fieldOf("temp_max").forGetter(DimensionalLaserRecipe::getTemperatureMax)
    ).apply(instance, DimensionalLaserRecipe::new));

    private static final FluidSerializerHelper<DimensionalLaserRecipe> helper = new FluidSerializerHelper<>();

    @Nullable
    @Override
    public DimensionalLaserRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
        DimensionalLaserRecipe recipe = new DimensionalLaserRecipe();

        buffer.writeInt(recipe.getMinimumAmount());
        buffer.writeInt(recipe.getMaximumAmount());

        buffer.writeFloat(recipe.getContinentalnessMin());
        buffer.writeFloat(recipe.getContinentalnessMax());

        buffer.writeFloat(recipe.getErosionMin());
        buffer.writeFloat(recipe.getErosionMax());

        buffer.writeFloat(recipe.getHumidityMin());
        buffer.writeFloat(recipe.getHumidityMax());

        buffer.writeFloat(recipe.getTemperatureMin());
        buffer.writeFloat(recipe.getTemperatureMax());
        return helper.fromNetwork(recipe, buffer);
    }

    @Override
    public @NotNull Codec<DimensionalLaserRecipe> codec() {
        return VE_RECIPE_CODEC;
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull DimensionalLaserRecipe recipe) {
        recipe.setMinimumAmount(buffer.readInt());
        recipe.setMaximumAmount(buffer.readInt());

        recipe.setContinentalnessMin(buffer.readFloat());
        recipe.setContinentalnessMax(buffer.readFloat());

        recipe.setErosionMin(buffer.readFloat());
        recipe.setErosionMax(buffer.readFloat());

        recipe.setHumidityMin(buffer.readFloat());
        recipe.setHumidityMax(buffer.readFloat());

        recipe.setTemperatureMin(buffer.readFloat());
        recipe.setTemperatureMax(buffer.readFloat());
        helper.toNetwork(buffer, recipe);
    }
}
