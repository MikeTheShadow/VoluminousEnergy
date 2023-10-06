package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.recipe.IngredientSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ElectrolyzerRecipe extends VERNGRecipe {

    public static final RecipeType<ElectrolyzerRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.ELECTROLYZING.get();

    public ElectrolyzerRecipe() {
    }

    public ElectrolyzerRecipe(List<VERecipeCodecs.RegistryIngredient> ingredients, List<ItemStack> results, int processTime, List<Float> rngOutputs) {
        super(ingredients, results, processTime, rngOutputs);
    }

    public static final RecipeSerializer<ElectrolyzerRecipe> SERIALIZER = new RecipeSerializer<>() {

        public static final Codec<ElectrolyzerRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                VERecipeCodecs.VE_LAZY_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.registryIngredients),
                VERecipeCodecs.VE_OUTPUT_ITEM_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.results),
                Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime),
                Codec.FLOAT.listOf().fieldOf("rng_values").forGetter((getter) -> getter.rngValues)
        ).apply(instance, ElectrolyzerRecipe::new));

        private static final IngredientSerializerHelper<ElectrolyzerRecipe> helper = new IngredientSerializerHelper<>();

        @Nullable
        @Override
        public ElectrolyzerRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
            return helper.fromNetwork(new ElectrolyzerRecipe(), buffer);
        }

        @Override
        public @NotNull Codec<ElectrolyzerRecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull ElectrolyzerRecipe recipe) {
            helper.toNetwork(buffer, recipe);
        }
    };
    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer(){ return SERIALIZER;}

    @Override
    public @NotNull RecipeType<?> getType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.ELECTROLYZER_BLOCK.get());
    }

}
