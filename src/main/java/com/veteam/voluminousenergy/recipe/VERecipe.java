package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.parser.BasicParser;
import com.veteam.voluminousenergy.util.VEClientSide;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class VERecipe implements Recipe<Container> {

    List<VERecipeCodecs.RegistryIngredient> registryIngredients;
    private List<FluidIngredient> fluidIngredientList = null;
    public List<VERecipeCodecs.RegistryFluidIngredient> registryFluidIngredients;
    public List<FluidStack> fluidOutputList;
    private static final HashMap<RecipeType<?>, List<VERecipe>> recipeCache = new HashMap<>();
    private static final HashMap<RecipeType<?>, List<VERecipe>> newCache = new HashMap<>();

    private NonNullList<Ingredient> ingredients = null;

    public int processTime;
    public List<ItemStack> results = new ArrayList<>();

    public VERecipe() {

    }

    /*
     * In single player worlds both the constructor and packet are fired. This bool
     * prevents the method from being called if the constructor is called
     */
    private static boolean isServerSide = false;

    public VERecipe(List<VERecipeCodecs.RegistryIngredient> ingredients,
            List<VERecipeCodecs.RegistryFluidIngredient> fluidIngredients, List<FluidStack> fluidResults,
            List<ItemStack> results, int processTime) {
        this.results = results;
        registryFluidIngredients = fluidIngredients;
        fluidOutputList = fluidResults;
        this.processTime = processTime;
        this.registryIngredients = NonNullList.create();
        this.registryIngredients.addAll(ingredients);
        isServerSide = true;
        VERecipe recipe = this;
        if (newCache.containsKey(this.getType())) {
            newCache.get(this.getType()).add(this);
        } else {
            newCache.put(this.getType(), new ArrayList<>() {
                {
                    add(recipe);
                }
            });
        }
    }

    public Ingredient getIngredient(int id) {
        return id < this.getIngredients().size() ? getIngredients().get(id) : Ingredient.EMPTY;
    }

    @Override
    public boolean matches(@NotNull Container inv, @NotNull Level worldIn) {
        throw new NotImplementedException("Class: " + this.getClass().getName() + " missing matches() impl.");
    }

    public ItemStack assemble(Container inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    public ItemStack getResult(int id) {
        if (id >= this.getResults().size()) {
            return ItemStack.EMPTY;
        }
        return this.getResults().get(id);
    }

    /**
     * A variable list of results of variable length that can change depending on
     * the recipe requirements
     * Should only be used in serialization
     *
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

    @Override
    public @NotNull ItemStack assemble(@NotNull Container pCraftingContainer,
            @NotNull HolderLookup.Provider registries) {
        throw new NotImplementedException("Unable to call assemble on recipe because it has been unimplemented!");
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider pRegistries) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        throw new NotImplementedException("Missing serializer impl for " + this.getClass().getName());
    }

    public boolean matches(@NotNull VETileEntity veTileEntity) {
        throw new NotImplementedException("Matches is not impl'd for: " + this.getClass().getName());
    }

    public int getResultCount(int slot) {
        if (slot >= this.getResults().size()) {
            return 0;
        }
        return this.results.get(slot).getCount();
    }

    public int getIngredientCount(int slot) {
        if (slot >= this.getIngredients().size()) {
            return 0;
        }
        return this.getIngredients().get(slot).getItems().length > 0
                ? this.ingredients.get(slot).getItems()[0].getCount()
                : 0;
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

    public List<FluidStack> getOutputFluids() {
        return this.fluidOutputList;
    }

    public FluidStack getOutputFluid(int slot) {
        return this.fluidOutputList.get(slot).copy();
    }

    public List<FluidIngredient> getFluidIngredients() {
        if (fluidIngredientList == null) {
            List<FluidIngredient> fluidIngredients = new ArrayList<>();
            for (VERecipeCodecs.RegistryFluidIngredient ingredient : registryFluidIngredients) {
                fluidIngredients.add(ingredient.getIngredient());
            }
            this.fluidIngredientList = fluidIngredients;
        }
        return fluidIngredientList;
    }

    public FluidIngredient getFluidIngredient(int slot) {
        return getFluidIngredients().get(slot);
    }

    public int getFluidIngredientAmount(int slot) {
        return getFluidIngredients().get(slot).getFluids()[0].getAmount();
    }

    public void setFluidOutputList(List<FluidStack> fluidOutputList) {
        this.fluidOutputList = fluidOutputList;
    }

    public void setFluidIngredientList(List<FluidIngredient> fluidIngredientList) {
        this.fluidIngredientList = fluidIngredientList;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {

        if (ingredients == null) {
            ingredients = NonNullList.create();
            for (VERecipeCodecs.RegistryIngredient ingredient : registryIngredients) {
                ingredients.add(ingredient.getIngredient());
            }
        }

        return ingredients;
    }

    public void setIngredients(NonNullList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public static List<VERecipe> getCachedRecipes(RecipeType<?> recipeType) {
        if (!recipeCache.containsKey(recipeType))
            return new ArrayList<>();
        return recipeCache.get(recipeType);
    }

    // Call after cache has been populated
    public static void updateCache() {
        recipeCache.clear();
        recipeCache.putAll(newCache);
        newCache.clear();
    }

    public static void addRecipeToCacheClient(VERecipe recipe) {
        if (isServerSide)
            return;
        if (newCache.containsKey(recipe.getType())) {
            newCache.get(recipe.getType()).add(recipe);
        } else {
            newCache.put(recipe.getType(), new ArrayList<>() {
                {
                    add(recipe);
                }
            });
        }
    }

    public static List<VERecipe> getPotentialRecipes(VETileEntity tile) {
        List<VERecipe> recipes = new ArrayList<>();
        for (VERecipe recipe : getCachedRecipes(tile.getRecipeType())) {
            if (recipe.getParser().isPartialRecipe(tile))
                recipes.add(recipe);
        }
        return recipes;
    }

    @Nullable
    public static VERecipe getCompleteRecipe(VETileEntity tile) {
        for (VERecipe recipe : getCachedRecipes(tile.getRecipeType())) {
            if (recipe.getParser().isCompleteRecipe(tile))
                return recipe;
        }
        return null;
    }

    public abstract BasicParser getParser();
}
