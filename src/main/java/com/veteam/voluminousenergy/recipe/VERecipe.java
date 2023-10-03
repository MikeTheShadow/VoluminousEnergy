package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.util.recipe.IngredientSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VEIngredientItem;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class VERecipe implements Recipe<Container> {

    public List<Lazy<Ingredient>> lazyIngredients = new ArrayList<>();
    public NonNullList<VEIngredientItem> ingredients;
    public int processTime;
    public List<ItemStack> results = new ArrayList<>();

    public static final Serializer SERIALIZER = new Serializer();
    ResourceLocation recipeId;

    public VERecipe() {

    }

    public VERecipe(List<ItemStack> results,int processTime,List<VEIngredientItem> ingredients) {
        this.results = results;
        this.processTime = processTime;
        this.ingredients = NonNullList.create();
        this.ingredients.addAll(ingredients);
    }

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
    public @NotNull RecipeSerializer<VERecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<?>> getType() {
        throw new NotImplementedException("Unable to get type for recipe: " + this.getClass().getName());
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        throw new NotImplementedException("Class" + this.getClass().getName() + " missing getToastSymbol impl!");
    }

    // TODO see if we really need this!
    private final List<Ingredient> rawItemIngredients = new ArrayList<>();
    public List<Ingredient> getItemIngredients() {
        if (rawItemIngredients.isEmpty()) {
            for (VEIngredientItem item : ingredients) {
                for (ItemStack stack : item.ingredient().getItems()) {
                    stack.setCount(item.count());
                }
                rawItemIngredients.add(item.ingredient());
            }
        }
        return this.rawItemIngredients;
    }

    public boolean matches(@NotNull VETileEntity veTileEntity) {
        throw new NotImplementedException("Matches is not impl'd for: " + this.getClass().getName());
    }

    public int getResultCount(int slot) {
        return this.results.get(slot).getCount();
    }

    public int getIngredientCount(int slot) {
        return this.ingredients.get(slot).count();
    }

    public int getProcessTime() {
        return processTime;
    }
    public void setResults(List<ItemStack> results) {
        this.results = results;
    }

    public static class Serializer implements RecipeSerializer<VERecipe> {

        public static final Codec<VERecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.results),
                Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime),
                VERecipeCodecs.VE_INGREDIENT_ITEM_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.ingredients)
        ).apply(instance, VERecipe::new));

        IngredientSerializerHelper<VERecipe> helper = new IngredientSerializerHelper<>();

        @Nullable
        @Override
        public VERecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
            VERecipe recipe = new VERecipe();
            helper.fromNetwork(recipe, buffer);
            return recipe;
        }

        @Override
        public @NotNull Codec<VERecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull VERecipe recipe) {
            helper.toNetwork(buffer,recipe);
        }
    }
}
