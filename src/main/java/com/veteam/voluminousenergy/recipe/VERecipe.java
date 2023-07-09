package com.veteam.voluminousenergy.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

public class VERecipe implements Recipe<Container> {

    public Lazy<Ingredient> ingredient;
    public int ingredientCount;
    public ItemStack result;


    public Ingredient getIngredient() {
        return ingredient.get();
    }

    public int getIngredientCount() {
        return ingredientCount;
    }

    public ItemStack getResult() { return result; }

    @Override
    public boolean matches(Container inv, @NotNull Level worldIn){
        ItemStack stack = inv.getItem(0);
        int count = stack.getCount();
        return ingredient.get().test(stack) && count >= ingredientCount;
    }
    @Override
    public @NotNull ItemStack assemble(@NotNull Container inv, @NotNull RegistryAccess registryAccess) { return this.assemble(inv); }

    public ItemStack assemble(Container inv){
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height){
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) { return this.getResultItem(); }

    public ItemStack getResultItem(){
        return result;
    }

    @Override
    public @NotNull ResourceLocation getId(){
        return null;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer(){
        return null;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<?>> getType(){
        throw new NotImplementedException("Unable to get type for recipe: " + this.getClass().getName());
    }

    @Override
    public @NotNull ItemStack getToastSymbol(){
        return null;
    }
}
