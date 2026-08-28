package com.veteam.voluminousenergy.recipe;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.parser.BasicParser;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.IngredientUtil;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class VERecipe implements Recipe<RecipeInput> {

    List<VERecipeCodecs.RegistryIngredient> registryIngredients;
    private List<FluidIngredient> fluidIngredientList = null;
    public List<VERecipeCodecs.RegistryFluidIngredient> registryFluidIngredients;
    public List<FluidStack> fluidOutputList = new ArrayList<>();
    // Same deferred-materialization deal as resultTemplates below, but for fluid outputs.
    public List<net.neoforged.neoforge.fluids.FluidStackTemplate> fluidOutputTemplates = null;
    private static final HashMap<RecipeType<?>, List<VERecipe>> recipeCache = new HashMap<>();
    private static final HashMap<RecipeType<?>, List<VERecipe>> newCache = new HashMap<>();

    private NonNullList<Ingredient> ingredients = null;

    public int processTime;
    public List<ItemStack> results = new ArrayList<>();
    // Templates for results, materialized into `results` lazily on first getResults() call rather
    // than eagerly at construction time (i.e. during recipe/codec decode), since constructing an
    // ItemStack from a Holder<Item> that early resolves data components before they're bound.
    public List<net.minecraft.world.item.ItemStackTemplate> resultTemplates = null;

    private Identifier id;

    public void setId(Identifier id) {
        this.id = id;
    }

    public Identifier id() {
        return id;
    }

    public VERecipe() {

    }

    /*
     * In single player worlds both the constructor and packet are fired. This bool
     * prevents the method from being called if the constructor is called
     */
    private static boolean isServerSide = false;

    public VERecipe(List<VERecipeCodecs.RegistryIngredient> ingredients,
            List<VERecipeCodecs.RegistryFluidIngredient> fluidIngredients, List<net.neoforged.neoforge.fluids.FluidStackTemplate> fluidResultTemplates,
            List<net.minecraft.world.item.ItemStackTemplate> resultTemplates, int processTime) {
        this.resultTemplates = resultTemplates;
        registryFluidIngredients = fluidIngredients;
        this.fluidOutputTemplates = fluidResultTemplates;
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

    public boolean hasIngredient(int id) {
        return id >= 0 && id < this.getIngredients().size();
    }

    @Nullable
    public Ingredient getIngredient(int id) {
        return hasIngredient(id) ? getIngredients().get(id) : null;
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput input) {
        throw new NotImplementedException("Class" + this.getClass().getName() + " missing assemble impl!");
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public @NotNull String group() {
        return "";
    }

    /*
     * VE recipes are never placed into a crafting grid via the recipe book, so they have no
     * PlacementInfo. Marking them special keeps RecipeManager#finalizeRecipeLoading from warning
     * that every one of them "can't be placed due to empty ingredients", since that check is
     * !isSpecial() && placementInfo().isImpossibleToPlace(), and NOT_PLACEABLE always satisfies
     * the latter. Machines resolve recipes through getCachedRecipes, not the recipe book.
     */
    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
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
        if (this.results.isEmpty() && this.resultTemplates != null && !this.resultTemplates.isEmpty()) {
            this.results = this.resultTemplates.stream().map(net.minecraft.world.item.ItemStackTemplate::create).toList();
        }
        return this.results;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<RecipeInput>> getType() {
        throw new NotImplementedException("Unable to get type for recipe: " + this.getClass().getName());
    }

    public @NotNull ItemStack getToastSymbol() {
        throw new NotImplementedException("Class" + this.getClass().getName() + " missing getToastSymbol impl!");
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        throw new NotImplementedException("Missing serializer impl for " + this.getClass().getName());
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
        return IngredientUtil.getItems(this.getIngredients().get(slot)).length > 0
                ? IngredientUtil.getItems(this.ingredients.get(slot))[0].getCount()
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
        if (this.fluidOutputList.isEmpty() && this.fluidOutputTemplates != null && !this.fluidOutputTemplates.isEmpty()) {
            this.fluidOutputList = this.fluidOutputTemplates.stream().map(net.neoforged.neoforge.fluids.FluidStackTemplate::create).toList();
        }
        return this.fluidOutputList;
    }

    public FluidStack getOutputFluid(int slot) {
        return this.getOutputFluids().get(slot).copy();
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
    public static void updateCache(Iterable<RecipeHolder<?>> recipes) {
        recipeCache.clear();
        for (RecipeHolder<?> holder : recipes) {
            if (holder.value() instanceof VERecipe veRecipe) {
                veRecipe.setId(holder.id().identifier());
                recipeCache.computeIfAbsent(veRecipe.getType(), k -> new ArrayList<>()).add(veRecipe);
            }
        }
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
