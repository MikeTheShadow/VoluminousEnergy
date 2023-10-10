package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class VERecipe implements Recipe<Container> {
    public List<VERecipeCodecs.RegistryIngredient> registryIngredients;

    private NonNullList<Ingredient> ingredients = null;

    public int processTime;
    public List<ItemStack> results = new ArrayList<>();

    public VERecipe() {

    }

    public VERecipe(List<VERecipeCodecs.RegistryIngredient> ingredients, List<ItemStack> results, int processTime) {
        this.results = results;
        this.processTime = processTime;
        this.registryIngredients = NonNullList.create();
        this.registryIngredients.addAll(ingredients);
    }

    public Ingredient getIngredient(int id) {
        // Sometimes recipes define less that what a machine can pull in (not utilizing all input slots). Therefore, return Empty Ingredient when querying beyond input length
        if (id >= this.getIngredients().size()) {
            return Ingredient.EMPTY;
        }
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
        VoluminousEnergy.LOGGER.warn("Suspicious call to getResultItem in " + this.getClass().getName() + ".");
        return new ItemStack(Items.BUCKET, 1);
    }

    public ItemStack getResult(int id) {
        // Sometimes recipes define less that what a machine can put out (not utilizing all output slots). Therefore, return ItemStack when querying beyond result length
        if (id >= this.getResults().size()) {
            return ItemStack.EMPTY;
        }
        return this.getResults().get(id);
    }

    /**
     * A variable list of results of variable length that can change depending on the recipe requirements
     * Should only be used in serialization
     * @return the raw results
     */
    public List<ItemStack> getResults() {
        return this.results;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<?>> getType() {
        throw new NotImplementedException("Unable to get type for recipe: " + this.getClass().getName());
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        throw new NotImplementedException("Class" + this.getClass().getName() + " missing getToastSymbol impl!");
    }

    public boolean matches(@NotNull VETileEntity veTileEntity) {
        throw new NotImplementedException("Matches is not impl'd for: " + this.getClass().getName());
    }

    public int getResultCount(int slot) {
        // Sometimes recipes define less that what a machine can put out (not utilizing all output slots). Therefore, return ItemStack when querying beyond result length
        if (slot >= this.getResults().size()) {
            return 0;
        }
        return this.results.get(slot).getCount();
    }

    public int getIngredientCount(int slot) {
        if (slot >= this.getIngredients().size()){
            return 0;
        }
        return this.getIngredients().get(slot).getItems().length > 0 ? this.ingredients.get(slot).getItems()[0].getCount() : 0;
    }

    public int getProcessTime() {
        return processTime;
    }

    public void setProcessTime(int processTime) {
        this.processTime = processTime;
    }

    public void setResults(List<ItemStack> results) {
        this.results = results;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {

        if(ingredients == null) {
            ingredients = NonNullList.create();
            for(VERecipeCodecs.RegistryIngredient ingredient : registryIngredients) {
                ingredients.add(ingredient.getIngredient());
            }
        }

        return ingredients;
    }

    public void setIngredients(NonNullList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
