package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.serializers.VEFluidIndustrialBlastingRecipeSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This is technically an extendable generic base class and thus has a serializer.
 */
public class IndustrialBlastingRecipe extends VEFluidRecipe {

    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.INDUSTRIAL_BLASTING.get();
    private static final VEFluidIndustrialBlastingRecipeSerializer SERIALIZER = new VEFluidIndustrialBlastingRecipeSerializer();
    private int minimumHeat;

    public IndustrialBlastingRecipe() {

    }

    public IndustrialBlastingRecipe(List<Ingredient> i, List<FluidIngredient> fi, List<FluidStack> of, List<ItemStack> oi, int processTime,int minimumHeat) {
        super(i,fi,of,oi,processTime);
        this.minimumHeat = minimumHeat;
    }

    @Override
    public @NotNull RecipeType<? extends VERecipe> getType() {
        return RECIPE_TYPE;
    }
    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.BLAST_FURNACE_BLOCK.get());
    }

    public void setMinimumHeat(int minimumHeat) {
        this.minimumHeat = minimumHeat;
    }

    public int getMinimumHeat() {
        return minimumHeat;
    }


}
