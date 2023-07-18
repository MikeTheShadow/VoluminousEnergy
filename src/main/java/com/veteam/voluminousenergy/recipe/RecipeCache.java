package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class RecipeCache {

    private static final HashMap<Level, HashMap<RecipeType<? extends Recipe<?>>, List<VERecipe>>> veRecipeCache = new HashMap<>();
    private static final HashMap<Level, HashMap<RecipeType<? extends Recipe<?>>, List<VEFluidRecipe>>> veFluidRecipeCache = new HashMap<>();

    public static void buildCache(Iterable<ServerLevel> levels) {

        int cached = 0;

        for (Level level : levels) {

            var levelCache = veRecipeCache.getOrDefault(level, new HashMap<>());
            var fluidLevelCache = veFluidRecipeCache.getOrDefault(level, new HashMap<>());

            for (Recipe<?> recipe : level.getRecipeManager().getRecipes()) {
                if (recipe instanceof VERecipe veRecipe) {
                    var cache = levelCache.getOrDefault(veRecipe.getType(), new ArrayList<>());
                    cache.add(veRecipe);
                    levelCache.put(veRecipe.getType(), cache);
                    cached++;
                } else if (recipe instanceof VEFluidRecipe veFluidRecipe) {
                    var cache = fluidLevelCache.getOrDefault(veFluidRecipe.getType(), new ArrayList<>());
                    cache.add(veFluidRecipe);
                    fluidLevelCache.put(veFluidRecipe.getType(), cache);
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
    public static VERecipe getRecipeFromCache(Level level, RecipeType<? extends Recipe<?>> type, ItemStack... items) {

        var recipes = getRecipesFromLevelWithClass(level, type);

        for (VERecipe recipe : recipes) {
            boolean isValid = true;
            for (int i = 0; i < items.length; i++) {
                if (!recipe.getIngredient(i).test(items[i])
                        || items[i].getCount() < recipe.getIngredient(i).getItems()[0].getCount()) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) return recipe;
        }
        return null;
    }

    @Nullable
    public static VERecipe getRecipeFromCache(Level level, RecipeType<? extends Recipe<?>> type, List<ItemStack> items) {

        var recipes = getRecipesFromLevelWithClass(level, type);

        for (VERecipe recipe : recipes) {
            boolean isValid = true;
            for (int i = 0; i < items.size(); i++) {
                if (!recipe.getIngredient(i).test(items.get(i))
                        || items.get(i).getCount() < recipe.getIngredient(i).getItems()[0].getCount()) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) return recipe;
        }
        return null;
    }

    @Nullable
    public static VEFluidRecipe getFluidRecipeFromCache(Level level, RecipeType<? extends Recipe<?>> type, List<FluidStack> fluids, List<ItemStack> items) {

        var recipes = getFluidRecipesFromLevelWithClass(level, type);

        for (VEFluidRecipe recipe : recipes) {
            boolean isValid = true;

            for (int i = 0; i < fluids.size(); i++) {
                if (!recipe.getFluidIngredient(i).test(fluids.get(i))
                        || fluids.get(i).getAmount() < recipe.getFluidIngredient(i).getFluids()[0].getAmount()) {
                    isValid = false;
                    break;
                }
            }

            for (int i = 0; i < items.size(); i++) {
                if (!recipe.getItemIngredient(i).test(items.get(i))
                        || items.get(i).getCount() < recipe.getItemIngredient(i).getItems()[0].getCount()) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) return recipe;
        }
        return null;
    }

    public static @NotNull List<VERecipe> getRecipesFromCache(Level level, RecipeType<? extends Recipe<?>> type, List<VESlotManager> slots, VETileEntity entity, boolean ignoreEmpty) {

        var recipes = getRecipesFromLevelWithClass(level, type);

        List<VERecipe> recipeList = new ArrayList<>();

        for (VERecipe recipe : recipes) {
            boolean isValid = true;

            ItemStackHandler handler = entity.getInventoryHandler();
            if (handler != null) {
                for (VESlotManager manager : slots) {
                    if (manager.getSlotType() != SlotType.INPUT) continue;
                    ItemStack stack = manager.getItem(handler);
                    if (ignoreEmpty && stack.isEmpty()) continue;
                    if (!recipe.getIngredient(manager.getRecipePos()).test(stack)) {
                        isValid = false;
                        break;
                    }
                }
            }
            if (isValid) recipeList.add(recipe);
        }
        return recipeList;
    }

    public static @NotNull List<VEFluidRecipe> getFluidRecipesFromCache(Level level, RecipeType<? extends Recipe<?>> type, List<VESlotManager> slots, List<RelationalTank> tanks, VETileEntity entity, boolean ignoreEmpty) {

        var recipes = getFluidRecipesFromLevelWithClass(level, type);

        List<VEFluidRecipe> recipeList = new ArrayList<>();

        for (VEFluidRecipe recipe : recipes) {
            boolean isValid = true;

            for (RelationalTank tank : tanks) {
                if (ignoreEmpty && tank.getTank().isEmpty()) continue;
                if (tank.getTankType() != TankType.INPUT) continue;
                FluidStack currentFluid = tank.getTank().getFluid();
                if (!recipe.getFluidIngredient(tank.getRecipePos()).test(currentFluid)) {
                    isValid = false;
                    break;
                }
            }

            ItemStackHandler handler = entity.getInventoryHandler();
            if (handler != null) {
                for (VESlotManager manager : slots) {
                    if (manager.getSlotType() != SlotType.INPUT) continue;
                    ItemStack stack = manager.getItem(handler);
                    if (ignoreEmpty && stack.isEmpty()) continue;
                    if (!recipe.getItemIngredient(manager.getRecipePos()).test(stack)) {
                        isValid = false;
                        break;
                    }
                }
            }

            if (isValid) recipeList.add(recipe);
        }
        return recipeList;
    }

    public static List<VEFluidRecipe> getFluidRecipesWithoutLevelDangerous(RecipeType<? extends Recipe<?>> type) {

        Set<VEFluidRecipe> veFluidRecipes = new HashSet<>();

        for (var map : veFluidRecipeCache.entrySet()) {
            List<VEFluidRecipe> recipes = map.getValue().get(type);
            veFluidRecipes.addAll(recipes);
        }
        return veFluidRecipes.stream().toList();
    }

    public static List<VERecipe> getRecipesWithoutLevelDangerous(RecipeType<? extends Recipe<?>> type) {

        Set<VERecipe> veFluidRecipes = new HashSet<>();

        for (var map : veRecipeCache.entrySet()) {
            List<VERecipe> recipes = map.getValue().get(type);
            veFluidRecipes.addAll(recipes);
        }
        return veFluidRecipes.stream().toList();
    }

    @Nonnull
    private static List<VEFluidRecipe> getFluidRecipesFromLevelWithClass(Level level, RecipeType<? extends Recipe<?>> type) {
        var levelCache = veFluidRecipeCache.get(level);

        if (levelCache == null) {
            VoluminousEnergy.LOGGER.warn("Unable to find cache for level: " + level.getClass().getCanonicalName());
            return new ArrayList<>();
        }

        var recipes = levelCache.get(type);

        if (recipes == null) {
            VoluminousEnergy.LOGGER.error("No recipes found for " + type.getClass().getName());
            return new ArrayList<>();
        }
        return recipes;
    }

    @Nonnull
    private static List<VERecipe> getRecipesFromLevelWithClass(Level level, RecipeType<? extends Recipe<?>> type) {
        var levelCache = veRecipeCache.get(level);

        if (levelCache == null) {
            VoluminousEnergy.LOGGER.warn("Unable to find cache for level: " + level.getClass().getCanonicalName());
            return new ArrayList<>();
        }

        var recipes = levelCache.get(type);

        if (recipes == null) {
            VoluminousEnergy.LOGGER.error("No recipes found for " + type.getClass().getName());
            return new ArrayList<>();
        }
        return recipes;
    }


    @Nullable
    public static VERecipe checkRecipeUsingMatch(Level level, RecipeType<?> recipeType, VETileEntity tile) {
        List<VERecipe> recipes = getRecipesFromLevelWithClass(level, recipeType);

        for (VERecipe recipe : recipes) {
            if (recipe.matches(tile)) {
                return recipe;
            }
        }
        return null;
    }

}
