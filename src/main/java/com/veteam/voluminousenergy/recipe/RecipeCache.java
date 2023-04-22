package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.codec.digest.Sha2Crypt;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeCache {

    private static final HashMap<Class<?>, List<VERecipe>> veRecipeCache = new HashMap<>();
    private static final HashMap<Class<?>, List<VEFluidRecipe>> veFluidRecipeCache = new HashMap<>();

    public static void buildCache(@Nullable Level level) {
        if (level == null) {
            VoluminousEnergy.LOGGER.warn("Level is null! Skipping recipe caching");
            return;
        }

        int cached = 0;
        for (Recipe<?> recipe : level.getRecipeManager().getRecipes()) {
            if (recipe instanceof VERecipe veRecipe) {
                var cache = veRecipeCache.getOrDefault(veRecipe.getClass(), new ArrayList<>());
                cache.add(veRecipe);
                veRecipeCache.put(veRecipe.getClass(), cache);
                cached++;
            } else if (recipe instanceof VEFluidRecipe veFluidRecipe) {
                var cache = veFluidRecipeCache.getOrDefault(veFluidRecipe.getClass(), new ArrayList<>());
                cache.add(veFluidRecipe);
                veFluidRecipeCache.put(veFluidRecipe.getClass(), cache);
                cached++;
            }
        }
        VoluminousEnergy.LOGGER.info("Built recipe cache for " + cached + " recipes!");
    }

    @Nullable
    public static VERecipe getRecipeFromCache(Class<? extends VERecipe> clazz, ItemStack... items) {

        var recipes = veRecipeCache.get(clazz);
        if (recipes == null) {
            throw new RuntimeException("Unable to find recipe for class" + clazz.getName());
        }

        for (VERecipe recipe : recipes) {
            boolean isValid = true;
            for (ItemStack stack : recipe.getIngredient().getItems()) {
                boolean hasIngredient = false;
                for (ItemStack item : items) {
                    if (stack.sameItem(item)) {
                        hasIngredient = true;
                        break;
                    }
                }
                if (!hasIngredient) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) return recipe;
        }
        return null;
    }

    @Nullable
    public static VEFluidRecipe getFluidRecipeFromCache(Class<? extends VEFluidRecipe> clazz, List<FluidStack> fluids, ItemStack... items) {

        var recipes = veFluidRecipeCache.get(clazz);
        if (recipes == null) {
            throw new RuntimeException("Unable to find recipe for class" + clazz.getName());
        }

        for (VEFluidRecipe recipe : recipes) {
            boolean isValid = true;
            for (ItemStack stack : recipe.getIngredient().getItems()) {
                boolean hasIngredient = false;
                for (ItemStack item : items) {
                    if (stack.sameItem(item)) {
                        hasIngredient = true;
                        break;
                    }
                }
                if (!hasIngredient) {
                    isValid = false;
                    break;
                }
            }
            for (Fluid fluid : recipe.getRawFluids()) {
                boolean hasFluid = false;
                for(FluidStack inputFluid : fluids) {
                    if (fluid.isSame(inputFluid.getFluid())) {
                        hasFluid = true;
                        break;
                    }
                }
                if (!hasFluid) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) return recipe;
        }
        return null;
    }
}
