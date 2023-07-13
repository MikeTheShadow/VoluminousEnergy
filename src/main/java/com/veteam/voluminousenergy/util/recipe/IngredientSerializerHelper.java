package com.veteam.voluminousenergy.util.recipe;

import com.veteam.voluminousenergy.recipe.IRNGRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class IngredientSerializerHelper<T extends VERecipe> {

    @Nullable
    public T fromNetwork(T recipe, FriendlyByteBuf buffer) {

        // Read ingredients
        int ingredientSize = buffer.readInt();
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for (int i = 0; i < ingredientSize; i++) {
            ingredients.add(Ingredient.fromNetwork(buffer));
        }
        recipe.setIngredients(ingredients);


        int outputItemSize = buffer.readInt();
        List<ItemStack> outputItems = new ArrayList<>();
        for (int i = 0; i < outputItemSize; i++) {
            outputItems.add(buffer.readItem());
        }
        recipe.setResults(outputItems);

        recipe.setProcessTime(buffer.readInt());

        if (recipe instanceof IRNGRecipe irngRecipe) {
            int totalRandom = buffer.readInt();
            float[] randomValues = new float[totalRandom];
            for (int i = 0; i < totalRandom; i++) {
                randomValues[i] = buffer.readFloat();
            }

            irngRecipe.setRNGOutputs(randomValues);
        }

        if(recipe instanceof IExperienceRecipe iExperienceRecipe) {
            int min = buffer.readInt();
            int max = buffer.readInt();
            iExperienceRecipe.setBoth(min,max);
        }

        return recipe;
    }

    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        buffer.writeInt(recipe.getLazyIngredients().size());
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.toNetwork(buffer);
        }

        buffer.writeInt(recipe.getResults().size());
        for (ItemStack stack : recipe.getResults()) {
            buffer.writeItemStack(stack, true);
        }

        buffer.writeInt(recipe.getProcessTime());

        if (recipe instanceof IRNGRecipe irngRecipe) {
            buffer.writeInt(irngRecipe.getRNGOutputs().length);
            for(float f : irngRecipe.getRNGOutputs()) {
                buffer.writeFloat(f);
            }
        }

        if(recipe instanceof IExperienceRecipe iExperienceRecipe) {
            buffer.writeInt(iExperienceRecipe.getMinExperience());
            buffer.writeInt(iExperienceRecipe.getMaxExperience());
        }
    }

}
