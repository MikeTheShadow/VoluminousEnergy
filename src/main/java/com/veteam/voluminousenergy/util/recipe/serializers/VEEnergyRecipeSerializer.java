package com.veteam.voluminousenergy.util.recipe.serializers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.recipe.VEEnergyRecipe;
import com.veteam.voluminousenergy.util.recipe.IngredientSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class VEEnergyRecipeSerializer implements RecipeSerializer<VEEnergyRecipe> {

    public static final Codec<VEEnergyRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            VERecipeCodecs.VE_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.ingredients),
            Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime),
            Codec.INT.fieldOf("energy_per_tick").forGetter((getter) -> getter.processTime)
    ).apply(instance, VEEnergyRecipe::new));

    private static final IngredientSerializerHelper<VEEnergyRecipe> helper = new IngredientSerializerHelper<>();

    @Nullable
    @Override
    public VEEnergyRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
        VEEnergyRecipe recipe = new VEEnergyRecipe();
        recipe.setEnergyPerTick(buffer.readInt());
        return helper.fromNetwork(recipe, buffer);
    }

    @Override
    public @NotNull Codec<VEEnergyRecipe> codec() {
        return VE_RECIPE_CODEC;
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull VEEnergyRecipe recipe) {
        buffer.writeInt(recipe.getEnergyPerTick());
        helper.toNetwork(buffer, recipe);
    }
}
