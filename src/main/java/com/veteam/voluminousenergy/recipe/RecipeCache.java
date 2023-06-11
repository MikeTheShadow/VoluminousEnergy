package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.VoluminousEnergy;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class RecipeCache {

    private static final HashMap<Level, HashMap<Class<?>, List<VERecipe>>> veRecipeCache = new HashMap<>();
    private static final HashMap<Level, HashMap<Class<?>, List<VEFluidRecipe>>> veFluidRecipeCache = new HashMap<>();

    public static void buildCache(Iterable<ServerLevel> levels) {

        int cached = 0;

        for (Level level : levels) {

            var levelCache = veRecipeCache.getOrDefault(level, new HashMap<>());
            var fluidLevelCache = veFluidRecipeCache.getOrDefault(level, new HashMap<>());

            for (Recipe<?> recipe : level.getRecipeManager().getRecipes()) {
                if (recipe instanceof VERecipe veRecipe) {
                    var cache = levelCache.getOrDefault(veRecipe.getClass(), new ArrayList<>());
                    cache.add(veRecipe);
                    levelCache.put(veRecipe.getClass(), cache);
                    cached++;
                } else if (recipe instanceof VEFluidRecipe veFluidRecipe) {
                    var cache = fluidLevelCache.getOrDefault(veFluidRecipe.getClass(), new ArrayList<>());
                    cache.add(veFluidRecipe);
                    fluidLevelCache.put(veFluidRecipe.getClass(), cache);
                    cached++;
                }
            }
            veRecipeCache.put(level, levelCache);
            veFluidRecipeCache.put(level, fluidLevelCache);
        }

        VoluminousEnergy.LOGGER.info("Built recipe cache for " + cached + " recipes!");
    }

    public static void rebuildCache() {
        VoluminousEnergy.LOGGER.warn("Cache rebuilding! This may produce instability!");
        HashSet<ServerLevel> levels = new HashSet<>();

        for (var entry : veRecipeCache.entrySet()) {
            levels.add((ServerLevel) entry.getKey());
        }
        for (var entry : veFluidRecipeCache.entrySet()) {
            levels.add((ServerLevel) entry.getKey());
        }
        veRecipeCache.clear();
        veFluidRecipeCache.clear();
        buildCache(levels);
    }

    @Nullable
    public static VERecipe getRecipeFromCache(Level level, Class<? extends VERecipe> clazz, ItemStack... items) {

        var levelCache = veRecipeCache.get(level);

        if (levelCache == null) return null;

        var recipes = levelCache.get(clazz);

        if (recipes == null) return null;

        for (VERecipe recipe : recipes) {
            if (containsIngredient(recipe.getIngredient(), items)) return recipe;
        }
        return null;
    }

    @Nullable
    public static VEFluidRecipe getFluidRecipeFromCache(Level level, Class<? extends VEFluidRecipe> clazz, List<FluidStack> fluids, ItemStack... items) {

        var levelCache = veFluidRecipeCache.get(level);

        if (levelCache == null) {
            VoluminousEnergy.LOGGER.warn("Unable to find cache for level: " + level.getClass().getCanonicalName());
            return null;
        }

        var recipes = levelCache.get(clazz);

        if (recipes == null) {
            VoluminousEnergy.LOGGER.info("No recipes?");
            return null;
        }

        for (VEFluidRecipe recipe : recipes) {
            boolean isValid = true;
            if (items.length != 0) {
                isValid = containsIngredient(recipe.getIngredient(), items);
            }
            for (FluidStack fluid : recipe.getFluids()) {
                boolean hasFluid = false;
                for (FluidStack inputFluid : fluids) {
                    if (fluid.isFluidEqual(inputFluid) && inputFluid.getAmount() >= fluid.getAmount()) {
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

    private static boolean containsIngredient(Ingredient ingredient, ItemStack[] items) {
        for (ItemStack stack : ingredient.getItems()) {
            boolean hasIngredient = false;
            for (ItemStack item : items) {
                if (stack.is(item.getItem()) && item.getCount() >= stack.getCount()) {
                    hasIngredient = true;
                    break;
                }
            }
            if (!hasIngredient) {
                return false;
            }
        }
        return true;
    }
}
