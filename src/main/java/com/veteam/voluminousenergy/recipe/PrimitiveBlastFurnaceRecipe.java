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
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PrimitiveBlastFurnaceRecipe extends VERecipe {

    public static final RecipeType<PrimitiveBlastFurnaceRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.PRIMITIVE_BLAST_FURNACING.get();

    public PrimitiveBlastFurnaceRecipe() {
    }

    public PrimitiveBlastFurnaceRecipe(List<Ingredient> ingredients, List<ItemStack> results, int processTime) {
        super(ingredients, results, processTime);
    }

    public static final RecipeSerializer<PrimitiveBlastFurnaceRecipe> SERIALIZER = new RecipeSerializer<>() {

        public static final Codec<PrimitiveBlastFurnaceRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                VERecipeCodecs.VE_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.ingredients),
                VERecipeCodecs.VE_OUTPUT_ITEM_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.results),
                Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime)
        ).apply(instance, PrimitiveBlastFurnaceRecipe::new));

        private static final IngredientSerializerHelper<PrimitiveBlastFurnaceRecipe> helper = new IngredientSerializerHelper<>();

        @Nullable
        @Override
        public PrimitiveBlastFurnaceRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
            return helper.fromNetwork(new PrimitiveBlastFurnaceRecipe(), buffer);
        }

        @Override
        public @NotNull Codec<PrimitiveBlastFurnaceRecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull PrimitiveBlastFurnaceRecipe recipe) {
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
        return new ItemStack(VEBlocks.PRIMITIVE_BLAST_FURNACE_BLOCK.get());
    }

}
