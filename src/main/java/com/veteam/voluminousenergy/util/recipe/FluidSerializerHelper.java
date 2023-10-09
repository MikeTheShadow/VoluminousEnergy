package com.veteam.voluminousenergy.util.recipe;

import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRNGRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FluidSerializerHelper<T extends VEFluidRecipe> {

    @Nullable
    public T fromNetwork(T recipe, FriendlyByteBuf buffer) {
        // Read ingredients
        int ingredientSize = buffer.readInt();
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for (int i = 0; i < ingredientSize; i++) {
            ingredients.add(Ingredient.fromNetwork(buffer));
        }
        recipe.setIngredients(ingredients);

        // Read fluid ingredients
        int fluidIngredientSize = buffer.readInt();
        List<FluidIngredient> fluidIngredients = new ArrayList<>();
        for (int i = 0; i < fluidIngredientSize; i++) {
            fluidIngredients.add(FluidIngredient.fromNetwork(buffer));
        }
        recipe.setFluidIngredientList(fluidIngredients);

        int outputItemSize = buffer.readInt();
        List<ItemStack> outputItems = new ArrayList<>();
        for (int i = 0; i < outputItemSize; i++) {
            outputItems.add(buffer.readItem());
        }
        recipe.setResults(outputItems);

        int outputFluidSize = buffer.readInt();
        List<FluidStack> outputFluids = new ArrayList<>();
        for (int i = 0; i < outputFluidSize; i++) {
            outputFluids.add(buffer.readFluidStack());
        }
        recipe.setFluidOutputList(outputFluids);

        recipe.setProcessTime(buffer.readInt());

        if (recipe instanceof VEFluidRNGRecipe irngRecipe) {
            int totalRandom = buffer.readInt();
            List<Float> randomValues = new ArrayList<>();
            for (int i = 0; i < totalRandom; i++) {
                randomValues.add(buffer.readFloat());
            }

            irngRecipe.setRNGOutputs(randomValues);
        }

        return recipe;
    }

    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        buffer.writeInt(recipe.getIngredients().size());
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.toNetwork(buffer);
        }

        buffer.writeInt(recipe.getFluidIngredients().size());

        for (FluidIngredient fluidIngredient : recipe.getFluidIngredients()) {
            fluidIngredient.toNetwork(buffer);
        }

        buffer.writeInt(recipe.getResults().size());
        for (ItemStack stack : recipe.getResults()) {
            buffer.writeItemStack(stack, true);
        }

        buffer.writeInt(recipe.getOutputFluids().size());
        for (FluidStack stack : recipe.getOutputFluids()) {
            buffer.writeFluidStack(stack);
        }

        buffer.writeInt(recipe.getProcessTime());

        if (recipe instanceof VEFluidRNGRecipe irngRecipe) {
            buffer.writeInt(irngRecipe.getRNGOutputs().size());
            for(float f : irngRecipe.getRNGOutputs()) {
                buffer.writeFloat(f);
            }
        }
    }

}
