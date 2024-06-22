package com.veteam.voluminousenergy.recipe.serializer;

import com.veteam.voluminousenergy.recipe.VERNGExperienceRecipe;
import com.veteam.voluminousenergy.recipe.VERNGRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class IngredientSerializerHelper<T extends VERecipe> {

    @Nullable
    public T fromNetwork(T recipe, RegistryFriendlyByteBuf buffer) {
        // Read ingredients
        int ingredientSize = buffer.readInt();
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for (int i = 0; i < ingredientSize; i++) {
            ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
        }
        recipe.setIngredients(ingredients);


        int outputItemSize = buffer.readInt();
        List<ItemStack> outputItems = new ArrayList<>();
        for (int i = 0; i < outputItemSize; i++) {
            outputItems.add(ItemStack.STREAM_CODEC.decode(buffer));
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

        if (recipe instanceof VERNGExperienceRecipe iExperienceRecipe) {
            int min = buffer.readInt();
            int max = buffer.readInt();
            iExperienceRecipe.setExperience(min, max);
        }
        VERecipe.addRecipeToCacheClient(recipe);
        return recipe;
    }

    public void toNetwork(RegistryFriendlyByteBuf buffer, T recipe) {
        buffer.writeInt(recipe.getIngredients().size());
        for (Ingredient ingredient : recipe.getIngredients()) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer,ingredient);
        }

        buffer.writeInt(recipe.getResults().size());
        for (ItemStack stack : recipe.getResults()) {
            ItemStack.STREAM_CODEC.encode(buffer,stack);
        }

        buffer.writeInt(recipe.getProcessTime());

        if (recipe instanceof VERNGRecipe irngRecipe) {
            buffer.writeInt(irngRecipe.getRNGOutputs().size());
            for (float f : irngRecipe.getRNGOutputs()) {
                buffer.writeFloat(f);
            }
        }

        if (recipe instanceof VERNGExperienceRecipe iExperienceRecipe) {
            buffer.writeInt(iExperienceRecipe.getMinExp());
            buffer.writeInt(iExperienceRecipe.getMaxExp());
        }
    }

}
