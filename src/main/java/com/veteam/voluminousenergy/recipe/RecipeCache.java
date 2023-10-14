package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RelationalTank;
import com.veteam.voluminousenergy.util.SlotType;
import com.veteam.voluminousenergy.util.TankType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class RecipeCache {

    @Nullable
    public static VERecipe getRecipeFromCache(Level level, RecipeType<? extends Recipe<?>> type, List<ItemStack> items) {

        var recipes = VERecipe.getCachedRecipes(type);

        for (VERecipe recipe : recipes) {
            boolean isValid = true;
            for (int i = 0; i < items.size(); i++) {
                if(recipe.getIngredient(i).isEmpty()) continue;
                if (!recipe.getIngredient(i).test(items.get(i))
                        || items.get(i).getCount() < recipe.getIngredientCount(i)) {
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

        var recipes = VERecipe.getCachedRecipes(type);
        for (VERecipe r : recipes) {
            VEFluidRecipe recipe = (VEFluidRecipe) r;
            boolean isValid = true;

            for (int i = 0; i < fluids.size(); i++) {
                if(recipe.getFluidIngredient(i).isEmpty()) continue;
                if (!recipe.getFluidIngredient(i).test(fluids.get(i))
                        || fluids.get(i).getAmount() < recipe.getFluidIngredientAmount(i)) {
                    isValid = false;
                    break;
                }
            }

            for (int i = 0; i < items.size(); i++) {
                if (!recipe.getIngredient(i).test(items.get(i))
                        || items.get(i).getCount() < recipe.getIngredientCount(i)) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) return recipe;
        }
        return null;
    }

    public static @NotNull List<VERecipe> getRecipesFromCache(Level level, RecipeType<? extends Recipe<?>> type, List<VESlotManager> slots, VETileEntity entity, boolean ignoreEmpty) {

        var recipes = VERecipe.getCachedRecipes(type);

        List<VERecipe> recipeList = new ArrayList<>();

        for (VERecipe recipe : recipes) {
            boolean isValid = true;

            ItemStackHandler handler = entity.getInventoryHandler();
            if (handler != null) {
                for (VESlotManager manager : slots) {
                    if (manager.getSlotType() != SlotType.INPUT) continue;
                    ItemStack stack = manager.getItem(handler);
                    if (ignoreEmpty && stack.isEmpty()) continue;
                    if(recipe.getIngredient(manager.getRecipePos()).isEmpty()) continue;
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

        var recipes = VERecipe.getCachedRecipes(type);

        List<VEFluidRecipe> recipeList = new ArrayList<>();

        for (VERecipe recipe : recipes) {
            boolean isValid = true;

            for (RelationalTank tank : tanks) {
                if (ignoreEmpty && tank.getTank().isEmpty()) continue;
                if (tank.getTankType() != TankType.INPUT) continue;
                FluidStack currentFluid = tank.getTank().getFluid();
                if (!((VEFluidRecipe) recipe).getFluidIngredient(tank.getRecipePos()).test(currentFluid)) {
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
                    if(recipe.getIngredient(manager.getRecipePos()).isEmpty()) continue;
                    if (!recipe.getIngredient(manager.getRecipePos()).test(stack)) {
                        isValid = false;
                        break;
                    }
                }
            }

            if (isValid) recipeList.add((VEFluidRecipe) recipe);
        }
        return recipeList;
    }
}
