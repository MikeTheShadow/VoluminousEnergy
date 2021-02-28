package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.recipe.AqueoulizerRecipe;
import com.veteam.voluminousenergy.recipe.CentrifugalAgitatorRecipe;
import com.veteam.voluminousenergy.recipe.DistillationRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class RecipeUtil {

    public static boolean isAqueoulizerInputFluidEqual(World world, Fluid fluid){
        for (IRecipe<?> iRecipe : world.getRecipeManager().getRecipes()) {
            if(iRecipe instanceof AqueoulizerRecipe){
                for (FluidStack stack : ((AqueoulizerRecipe) iRecipe).fluidInputList){
                    if(stack.getFluid().isEquivalentTo(fluid)) return true;
                }
            }
        }
        return false;
    }

    public static boolean isAqueoulizerInputFluidEqual(AqueoulizerRecipe recipe, Fluid fluid){
        for (FluidStack stack : recipe.fluidInputList){ if(stack.getFluid().isEquivalentTo(fluid)) return true; }
        return false;
    }

    public static AqueoulizerRecipe getAqueoulizerRecipe(World world, FluidStack inputFluid, ItemStack inputItem){
        for(IRecipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof AqueoulizerRecipe){
                for (FluidStack recipeFluid : ((AqueoulizerRecipe) recipe).fluidInputList){
                    if(recipeFluid.isFluidEqual(inputFluid)) {
                        for(Item ingredient : ((AqueoulizerRecipe) recipe).ingredientList){
                            if(ingredient.equals(inputItem.getItem())) return (AqueoulizerRecipe) recipe;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static CentrifugalAgitatorRecipe getCentrifugalAgitatorRecipe(World world, FluidStack inputFluid){
        for(IRecipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof CentrifugalAgitatorRecipe){
                for (FluidStack recipeFluid : ((CentrifugalAgitatorRecipe) recipe).fluidInputList){
                    if(recipeFluid.isFluidEqual(inputFluid)){
                        return (CentrifugalAgitatorRecipe) recipe;
                    }
                }
            }
        }
        return null;
    }

    public static DistillationRecipe getDistillationRecipe(World world, FluidStack inputFluid){
        for(IRecipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof DistillationRecipe){
                for (FluidStack recipeFluid : ((DistillationRecipe) recipe).fluidInputList){
                    if(recipeFluid.isFluidEqual(inputFluid)){
                        return (DistillationRecipe) recipe;
                    }
                }
            }
        }
        return null;
    }

    public static DistillationRecipe getDistillationRecipeFromResult(World world, FluidStack resultFluid){
        for(IRecipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof DistillationRecipe){
                if (((DistillationRecipe) recipe).getOutputFluid().isFluidEqual(resultFluid)){
                    return (DistillationRecipe) recipe;
                }
            }
        }
        return null;
    }

    public static DistillationRecipe getDistillationRecipeFromSecondResult(World world, FluidStack secondResultFluid){
        for(IRecipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof DistillationRecipe){
                if (((DistillationRecipe) recipe).getSecondResult().isFluidEqual(secondResultFluid)){
                    return (DistillationRecipe) recipe;
                }
            }
        }
        return null;
    }

    public static DistillationRecipe getDistillationRecipeFromThirdResult(World world, ItemStack thirdResultItem){
        for(IRecipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof DistillationRecipe){
                if(((DistillationRecipe) recipe).getThirdResult().isItemEqual(thirdResultItem)){
                    return (DistillationRecipe) recipe;
                }
            }
        }
        return null;
    }

}
