package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.*;

public class RecipeCache {

    private static final HashMap<Level, HashMap<Class<?>, List<VERecipe>>> veRecipeCache = new HashMap<>();

    private static final HashMap<Level, HashMap<Class<?>, HashMap<Integer, List<Ingredient>>>> veRecipeItemCache = new HashMap<>();
    private static final HashMap<Level, HashMap<Class<?>, HashMap<Integer, List<FluidIngredient>>>> veRecipeFluidCache = new HashMap<>();
    private static final List<VERecipe> rawVERecipes = new ArrayList<>();
    private static final HashMap<Level, HashMap<Class<?>, List<VEFluidRecipe>>> veFluidRecipeCache = new HashMap<>();
    private static final List<VEFluidRecipe> rawVEFluidRecipes = new ArrayList<>();

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
                    rawVERecipes.add(veRecipe);
                    cached++;
                } else if (recipe instanceof VEFluidRecipe veFluidRecipe) {
                    var cache = fluidLevelCache.getOrDefault(veFluidRecipe.getClass(), new ArrayList<>());
                    cache.add(veFluidRecipe);
                    fluidLevelCache.put(veFluidRecipe.getClass(), cache);
                    rawVEFluidRecipes.add(veFluidRecipe);
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
        rawVEFluidRecipes.clear();
        rawVERecipes.clear();
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
            VoluminousEnergy.LOGGER.error("No recipes found for " + clazz.getName());
            return null;
        }

        for (VEFluidRecipe recipe : recipes) {
            boolean itemsValid = recipe.getItemIngredients().stream().allMatch(ingredient -> {
                for (ItemStack s : items) {
                    if (ingredient.test(s) && s.getCount() >= ingredient.getItems()[0].getCount()) return true;
                }
                return false;
            });
            boolean fluidsValid = recipe.getFluidIngredients().stream().allMatch(ingredient -> {
                for (FluidStack s : fluids) {
                    if (ingredient.test(s)) return true;
                }
                return false;
            });
            if (itemsValid && fluidsValid) return recipe;
        }
        return null;
    }

    public static int recipeHasItem(Level level, Class<?> recipe, ItemStack stack, int position) {

        if (!veRecipeItemCache.containsKey(level)) return -1;

        if (!veRecipeItemCache.get(level).containsKey(recipe)) return -1;

        var list = veRecipeItemCache.get(level).get(recipe).get(position);

        for (Ingredient ingredient : list) {
            if (ingredient.test(stack)) {
                return list.indexOf(ingredient);
            }
        }
        return -1;
    }

    public static int recipeHasFluid(Level level, Class<?> recipe, FluidStack stack, int position) {

        if (!veRecipeFluidCache.containsKey(level)) return -1;

        if (!veRecipeFluidCache.get(level).containsKey(recipe)) return -1;

        var list = veRecipeFluidCache.get(level).get(recipe).get(position);

        for (FluidIngredient ingredient : list) {
            if (ingredient.test(stack)) {
                return list.indexOf(ingredient);
            }
        }
        return -1;
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
