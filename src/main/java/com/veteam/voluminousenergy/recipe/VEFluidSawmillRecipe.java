package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.serializers.VEFluidSawmillRecipeSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This is technically an extendable generic base class and thus has a serializer.
 */
public class VEFluidSawmillRecipe extends VEFluidRecipe {

    public static final RecipeType<VEFluidSawmillRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.SAWMILLING.get();
    private boolean isLogRecipe;

    public VEFluidSawmillRecipe() {

    }

    public VEFluidSawmillRecipe(List<Ingredient> i, List<FluidIngredient> fi, List<FluidStack> of, List<ItemStack> oi, int processTime, boolean isLogRecipe) {
        super(i,fi,of,oi,processTime);
        this.isLogRecipe = isLogRecipe;
    }

    private static final VEFluidSawmillRecipeSerializer SERIALIZER = new VEFluidSawmillRecipeSerializer();

    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<?>> getType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.SAWMILL_BLOCK.get());
    }

    public boolean isLogRecipe() {
        return isLogRecipe;
    }

}
