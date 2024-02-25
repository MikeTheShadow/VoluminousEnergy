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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CentrifugalSeparatorRecipe extends VEFluidRNGRecipe {

    public static final RecipeType<CentrifugalSeparatorRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.CENTRIFUGAL_SEPARATION.get();

    public CentrifugalSeparatorRecipe() {
    }

    public CentrifugalSeparatorRecipe(List<VERecipeCodecs.RegistryIngredient> ingredients, List<VERecipeCodecs.VEChancedItemWithCount> results, int processTime) {
        super(ingredients,new ArrayList<>(),new ArrayList<>(), results, processTime);
    }

    public static final RecipeSerializer<CentrifugalSeparatorRecipe> SERIALIZER = new RecipeSerializer<>() {

        public static final Codec<CentrifugalSeparatorRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                VERecipeCodecs.VE_LAZY_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.registryIngredients),
                VERecipeCodecs.VE_CHANCED_OUTPUT_ITEM_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.itemResultsWithChance),
                Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime)
        ).apply(instance, CentrifugalSeparatorRecipe::new));

        private static final IngredientSerializerHelper<CentrifugalSeparatorRecipe> helper = new IngredientSerializerHelper<>();

        @Nullable
        @Override
        public CentrifugalSeparatorRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
            return helper.fromNetwork(new CentrifugalSeparatorRecipe(), buffer);
        }

        @Override
        public @NotNull Codec<CentrifugalSeparatorRecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull CentrifugalSeparatorRecipe recipe) {
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
        return new ItemStack(VEBlocks.CENTRIFUGAL_SEPARATOR_BLOCK.get());
    }
}
