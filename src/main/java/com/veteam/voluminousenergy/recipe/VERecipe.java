package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VERecipe implements Recipe<Container> {

    public List<Lazy<Ingredient>> lazyIngredients = new ArrayList<>();
    public NonNullList<Ingredient> ingredients;
    int processTime;
    public List<ItemStack> results = new ArrayList<>();
    ResourceLocation recipeId;


    public Ingredient getIngredient(int id) {
        return getIngredients().get(id);
    }

    @Override
    public boolean matches(@NotNull Container inv, @NotNull Level worldIn) {
        throw new NotImplementedException("Class: " + this.getClass().getName() + " missing matches() impl.");
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container inv, @NotNull RegistryAccess registryAccess) {
        return this.assemble(inv);
    }

    public ItemStack assemble(Container inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        VoluminousEnergy.LOGGER.warn("Dangerous getResultItem call from class: " + this.getClass().getName());
        return new ItemStack(Items.DIRT,1);
    }

    public ItemStack getResult(int id) {
        return this.getResults().get(id);
    }



    public List<ItemStack> getResults() {
        return this.results;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        throw new NotImplementedException("Class" + this.getClass().getName() + " missing getId impl!");
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        throw new NotImplementedException("Class" + this.getClass().getName() + " missing getSerializer impl!");
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<?>> getType() {
        throw new NotImplementedException("Unable to get type for recipe: " + this.getClass().getName());
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        throw new NotImplementedException("Class" + this.getClass().getName() + " missing getToastSymbol impl!");
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        if(this.ingredients == null) {
            this.ingredients = unwrapIngredients();
        }
        return this.ingredients;
    }

    private NonNullList<Ingredient> unwrapIngredients() {
        NonNullList<Ingredient> ingredientList = NonNullList.create();
        for (Lazy<Ingredient> ingredient : this.lazyIngredients) {
            ingredientList.add(ingredient.get());
        }
        return ingredientList;
    }

    public boolean matches(@NotNull VETileEntity veTileEntity) {
        throw new NotImplementedException("Matches is not impl'd for: " + this.getClass().getName());
    }

    public void addLazyIngredient(Lazy<Ingredient> ingredientLazy) {
        this.lazyIngredients.add(ingredientLazy);
    }

    public void addResult(ItemStack output) {
        this.results.add(output);
    }

    public int getResultCount(int slot) {
        return this.results.get(slot).getCount();
    }

    public int getIngredientCount(int slot) {
        return this.ingredients.get(slot).getItems()[0].getCount();
    }

    public void setProcessTime(int processTime) {
        this.processTime = processTime;
    }

    public int getProcessTime() {
        return processTime;
    }

    public void setIngredients(NonNullList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void setResults(List<ItemStack> results) {
        this.results = results;
    }

    public List<Lazy<Ingredient>> getLazyIngredients() {
        return lazyIngredients;
    }

}
