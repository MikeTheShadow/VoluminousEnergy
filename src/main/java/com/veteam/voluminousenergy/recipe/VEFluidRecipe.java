package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.util.recipe.*;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class VEFluidRecipe implements Recipe<Container> {


    List<VEIngredientItem> ingredientList;
    List<FluidIngredient> fluidIngredientList;

    List<FluidStack> fluidOutputList = new ArrayList<>();
    List<ItemStack> itemOutputList = new ArrayList<>();

    public static final Serializer SERIALIZER = new Serializer();
    int processTime;

    public VEFluidRecipe() {

    }

    public VEFluidRecipe(List<VEIngredientItem> i, List<FluidIngredient> fi, List<FluidStack> of, List<ItemStack> oi,int processTime) {
        ingredientList = i;
        fluidIngredientList = fi;
        fluidOutputList = of;
        itemOutputList = oi;
        this.processTime = processTime;
    }

    @Override
    public boolean matches(Container inv, @NotNull Level worldIn) {
        throw new NotImplementedException();
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container inv, @NotNull RegistryAccess registryAccess) {
        return this.assemble(inv);
    }

    // NOTE: Legacy impl pre 1.19.4
    public ItemStack assemble(Container inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull RecipeSerializer<VEFluidRecipe> getSerializer() {
        return SERIALIZER;
    }


    public @NotNull RecipeType<VEFluidRecipe> getType() {
        throw new NotImplementedException("Not yet impl'd!");
    }

    public List<ItemStack> getOutputItems() {
        return this.itemOutputList;
    }

    public List<FluidStack> getOutputFluids() {
        return this.fluidOutputList;
    }

    public FluidStack getOutputFluid(int slot) {
        return this.fluidOutputList.get(slot).copy();
    }


    public List<FluidIngredient> getFluidIngredients() {
        return this.fluidIngredientList;
    }

    public FluidIngredient getFluidIngredient(int slot) {
        return getFluidIngredients().get(slot);
    }

    public int getFluidIngredientAmount(int slot) {
        return getFluidIngredients().get(slot).getFluids()[0].getAmount();
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        VoluminousEnergy.LOGGER.warn("Suspicious call to getResultItem in " + this.getClass().getName() + ".");
        return new ItemStack(Items.BUCKET, 1);
    }

    private final NonNullList<Ingredient> rawItemIngredients = NonNullList.create();
    public @NotNull NonNullList<Ingredient> getIngredients() {
        if (rawItemIngredients.isEmpty()) {
            for (VEIngredientItem item : ingredientList) {
                for (ItemStack stack : item.ingredient().getItems()) {
                    stack.setCount(item.count());
                }
                rawItemIngredients.add(item.ingredient());
            }
        }
        return this.rawItemIngredients;
    }

    public Ingredient getItemIngredient(int slot) {
        return getIngredients().get(slot);
    }

    public int getItemIngredientCount(int slot) {
        return this.ingredientList.get(slot).count();
    }

    public List<Float> getRNGAmounts() {
        throw new NotImplementedException("This method needs to be impl'd before call in " + this.getClass().getName());
    }

    public int getProcessTime() {
        return this.processTime;
    }

    public void setProcessTime(int time) {
        this.processTime = time;
    }

    public List<FluidIngredient> getFluidIngredientList() {
        return fluidIngredientList;
    }

    public void addFluidOutput(FluidStack stack) {
        this.fluidOutputList.add(stack);
    }

    public void setFluidOutputList(List<FluidStack> fluidOutputList) {
        this.fluidOutputList = fluidOutputList;
    }

    public void setItemOutputList(List<ItemStack> itemOutputList) {
        this.itemOutputList = itemOutputList;
    }

    public void addItemOutput(ItemStack stack) {
        this.itemOutputList.add(stack);
    }

    public ItemStack getOutputItem(int slot) {
        return this.itemOutputList.get(slot);
    }

    public static class Serializer implements RecipeSerializer<VEFluidRecipe> {

        public static final Codec<VEFluidRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                VERecipeCodecs.VE_INGREDIENT_ITEM_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.ingredientList),
                VERecipeCodecs.VE_FLUID_INGREDIENT_CODEC.listOf().fieldOf("fluid_ingredients").forGetter((getter) -> getter.fluidIngredientList),
                VERecipeCodecs.VE_OUTPUT_FLUID_CODEC.listOf().fieldOf("fluid_results").forGetter((getter) -> getter.fluidOutputList),
                CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.itemOutputList),
                Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime)
        ).apply(instance, VEFluidRecipe::new));

        FluidSerializerHelper<VEFluidRecipe> helper = new FluidSerializerHelper<>();

        @Nullable
        @Override
        public VEFluidRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
            VEFluidRecipe recipe = new VEFluidRecipe();
            helper.fromNetwork(recipe, buffer);
            return recipe;
        }

        @Override
        public @NotNull Codec<VEFluidRecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull VEFluidRecipe recipe) {
            helper.toNetwork(buffer, recipe);
        }
    }
}
