package com.veteam.voluminousenergy.util.recipe;

import com.veteam.voluminousenergy.recipe.IRNGRecipe;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FluidSerializerHelper<T extends VEFluidRecipe> {

    // TODO fix me

    @Nullable
    public T fromNetwork(FriendlyByteBuf buffer) {

        // Read ingredients
        int ingredientSize = buffer.readInt();
        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientSize; i++) {
            ingredients.add(Ingredient.fromNetwork(buffer));
        }
        recipe.setIngredientList(ingredients);

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
        recipe.setItemOutputList(outputItems);

        int outputFluidSize = buffer.readInt();
        List<FluidStack> outputFluids = new ArrayList<>();
        for (int i = 0; i < outputFluidSize; i++) {
            outputFluids.add(buffer.readFluidStack());
        }
        recipe.setFluidOutputList(outputFluids);

        recipe.setProcessTime(buffer.readInt());

        if (recipe instanceof IRNGRecipe irngRecipe) {
            int totalRandom = buffer.readInt();
            float[] randomValues = new float[totalRandom];
            for (int i = 0; i < totalRandom; i++) {
                randomValues[i] = buffer.readFloat();
            }

            irngRecipe.setRNGOutputs(randomValues);
        }

        return recipe;
    }

    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        buffer.writeInt(recipe.getItemIngredients().size());
        for (Ingredient ingredient : recipe.getItemIngredients()) {
            ingredient.toNetwork(buffer);
        }

        buffer.writeInt(recipe.getFluidIngredientList().size());

        for (FluidIngredient fluidIngredient : recipe.getFluidIngredients()) {
            fluidIngredient.toNetwork(buffer);
        }

        buffer.writeInt(recipe.getOutputItems().size());
        for (ItemStack stack : recipe.getOutputItems()) {
            buffer.writeItemStack(stack, true);
        }

        buffer.writeInt(recipe.getOutputFluids().size());
        for (FluidStack stack : recipe.getOutputFluids()) {
            buffer.writeFluidStack(stack);
        }

        buffer.writeInt(recipe.getProcessTime());

        if (recipe instanceof IRNGRecipe irngRecipe) {
            buffer.writeInt(irngRecipe.getRNGOutputs().length);
            for(float f : irngRecipe.getRNGOutputs()) {
                buffer.writeFloat(f);
            }
        }
    }

}
