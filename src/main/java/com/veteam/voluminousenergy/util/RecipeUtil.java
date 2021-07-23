package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.recipe.AqueoulizerRecipe;
import com.veteam.voluminousenergy.recipe.CentrifugalAgitatorRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorFuelRecipe;
import com.veteam.voluminousenergy.recipe.CombustionGenerator.CombustionGeneratorOxidizerRecipe;
import com.veteam.voluminousenergy.recipe.DistillationRecipe;
import com.veteam.voluminousenergy.recipe.IndustrialBlastingRecipe;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

public class RecipeUtil {

    public static boolean isAqueoulizerInputFluidEqual(Level world, Fluid fluid){
        for (Recipe<?> iRecipe : world.getRecipeManager().getRecipes()) {
            if(iRecipe instanceof AqueoulizerRecipe){
                for (FluidStack stack : ((AqueoulizerRecipe) iRecipe).fluidInputList){
                    if(stack.getFluid().isSame(fluid)) return true;
                }
            }
        }
        return false;
    }

    public static boolean isAqueoulizerInputFluidEqual(AqueoulizerRecipe recipe, Fluid fluid){
        for (FluidStack stack : recipe.fluidInputList){ if(stack.getFluid().isSame(fluid)) return true; }
        return false;
    }

    public static AqueoulizerRecipe getAqueoulizerRecipe(Level world, FluidStack inputFluid, ItemStack inputItem){
        for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
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

    public static CentrifugalAgitatorRecipe getCentrifugalAgitatorRecipe(Level world, FluidStack inputFluid){
        for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
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

    public static DistillationRecipe getDistillationRecipe(Level world, FluidStack inputFluid){
        for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
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

    public static DistillationRecipe getDistillationRecipeFromResult(Level world, FluidStack resultFluid){
        for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof DistillationRecipe){
                if (((DistillationRecipe) recipe).getOutputFluid().isFluidEqual(resultFluid)){
                    return (DistillationRecipe) recipe;
                }
            }
        }
        return null;
    }

    public static DistillationRecipe getDistillationRecipeFromSecondResult(Level world, FluidStack secondResultFluid){
        for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof DistillationRecipe){
                if (((DistillationRecipe) recipe).getSecondResult().isFluidEqual(secondResultFluid)){
                    return (DistillationRecipe) recipe;
                }
            }
        }
        return null;
    }

    public static DistillationRecipe getDistillationRecipeFromThirdResult(Level world, ItemStack thirdResultItem){
        for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof DistillationRecipe){
                if(((DistillationRecipe) recipe).getThirdResult().sameItem(thirdResultItem)){
                    return (DistillationRecipe) recipe;
                }
            }
        }
        return null;
    }

    public static CombustionGeneratorFuelRecipe getFuelCombustionRecipe(Level world, FluidStack inputFluid){
        for (Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if (recipe instanceof CombustionGeneratorFuelRecipe){
                if (((CombustionGeneratorFuelRecipe) recipe).rawFluidInputList.contains(inputFluid.getRawFluid())){
                    return (CombustionGeneratorFuelRecipe) recipe;
                }
            }
        }
        return null;
    }

    public static boolean isOxidizer(FluidStack inputFluid){
        if (CombustionGeneratorOxidizerRecipe.rawFluidInputList.contains(inputFluid.getRawFluid())) return true;
        return false;
    }

    public static CombustionGeneratorOxidizerRecipe getOxidizerCombustionRecipe(Level world, FluidStack inputFluid){
        for(Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if(recipe instanceof CombustionGeneratorOxidizerRecipe){
                if(((CombustionGeneratorOxidizerRecipe) recipe).nsRawFluidInputList.contains(inputFluid.getRawFluid())){
                    return (CombustionGeneratorOxidizerRecipe) recipe;
                }
            }
        }
        return null;
    }

    public static IndustrialBlastingRecipe getIndustrialBlastingRecipe(Level world, ItemStack firstInput, ItemStack secondInput){
        if(firstInput.isEmpty() || secondInput.isEmpty()) return null;
        for (Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if(recipe instanceof IndustrialBlastingRecipe){
                if( ((IndustrialBlastingRecipe) recipe).getFirstInputAsList().contains(firstInput.getItem()) &&
                        ((IndustrialBlastingRecipe) recipe).onlySecondInput.contains(secondInput.getItem())){
                    return (IndustrialBlastingRecipe) recipe;
                }
            }
        }
        return null;
    }

    public static boolean isFirstIngredientForIndustrialBlastingRecipe(Level world, ItemStack firstInput){
        if (firstInput.isEmpty()) return false;
        for (Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if(recipe instanceof IndustrialBlastingRecipe){
                if (((IndustrialBlastingRecipe) recipe).getFirstInputAsList().contains(firstInput.getItem())) return true;
            }
        }
        return false;
    }

    public static boolean isSecondIngredientForIndustrialBlastingRecipe(Level world, ItemStack secondInput){
        if(secondInput.isEmpty()) return false;
        for (Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if(recipe instanceof IndustrialBlastingRecipe){
                if (((IndustrialBlastingRecipe) recipe).onlySecondInput.contains(secondInput.getItem())) return true;
            }
        }
        return false;
    }

    public static boolean isAnOutputForIndustrialBlastingRecipe(Level world, ItemStack outputStack){
        if (outputStack.isEmpty()) return false;
        for (Recipe<?> recipe : world.getRecipeManager().getRecipes()){
            if(recipe instanceof IndustrialBlastingRecipe){
                if (((IndustrialBlastingRecipe) recipe).getResult().sameItem(outputStack)) return true;
            }
        }
        return false;
    }
}
