package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.serializer.IngredientSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrusherRecipe extends VERNGExperienceRecipe {

    public static final RecipeType<CrusherRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.CRUSHING.get();

    public CrusherRecipe() {
    }

    public CrusherRecipe(List<VERecipeCodecs.RegistryIngredient> ingredients, List<VERecipeCodecs.VEChancedItemWithCount> results, int processTime, VERecipeCodecs.VERecipeExperience experience) {
        super(ingredients, results, processTime, experience);
    }

    public static final RecipeSerializer<CrusherRecipe> SERIALIZER = new RecipeSerializer<>() {

        public static final Codec<CrusherRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                VERecipeCodecs.VE_LAZY_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.registryIngredients),
                VERecipeCodecs.VE_CHANCED_OUTPUT_ITEM_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.itemResultsWithChance),
                Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime),
                VERecipeCodecs.VE_EXPERIENCE_RANGE_CODEC.fieldOf("experience").forGetter((getter) -> getter.experience)
        ).apply(instance, CrusherRecipe::new));

        private static final IngredientSerializerHelper<CrusherRecipe> helper = new IngredientSerializerHelper<>();

        @Nullable
        @Override
        public CrusherRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
            return helper.fromNetwork(new CrusherRecipe(), buffer);
        }

        @Override
        public @NotNull Codec<CrusherRecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull CrusherRecipe recipe) {
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
        return new ItemStack(VEBlocks.CRUSHER_BLOCK.get());
    }

}
