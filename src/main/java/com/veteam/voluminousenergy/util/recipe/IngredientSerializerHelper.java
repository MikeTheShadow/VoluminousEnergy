package com.veteam.voluminousenergy.util.recipe;

import com.veteam.voluminousenergy.recipe.IRNGRecipe;
import com.veteam.voluminousenergy.recipe.VERNGExperienceRecipe;
import com.veteam.voluminousenergy.recipe.VERNGRecipe;
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

        if (recipe instanceof VERNGRecipe irngRecipe) {
            int totalRandom = buffer.readInt();
            List<Float> values = new ArrayList<>();
            for (int i = 0; i < totalRandom; i++) {
                values.add(buffer.readFloat());
            }
            irngRecipe.setRNGOutputs(values);
        }

        if(recipe instanceof VERNGExperienceRecipe iExperienceRecipe) {
            int min = buffer.readInt();
            int max = buffer.readInt();
            iExperienceRecipe.setBoth(min,max);
        }

        return recipe;
    }

    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        buffer.writeInt(recipe.getIngredients().size());
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.toNetwork(buffer);
        }

        buffer.writeInt(recipe.getResults().size());
        for (ItemStack stack : recipe.getResults()) {
            buffer.writeItemStack(stack, true);
        }

        buffer.writeInt(recipe.getProcessTime());

        if (recipe instanceof VERNGRecipe irngRecipe) {
            buffer.writeInt(irngRecipe.getRNGOutputs().size());
            for(float f : irngRecipe.getRNGOutputs()) {
                buffer.writeFloat(f);
            }
        }

        if(recipe instanceof VERNGExperienceRecipe iExperienceRecipe) {
            buffer.writeInt(iExperienceRecipe.minExp);
            buffer.writeInt(iExperienceRecipe.maxExp);
        }
    }

}
