package com.veteam.voluminousenergy.util.recipe.serializers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.util.recipe.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.CraftingRecipeCodecs;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class VEFluidRecipeSerializer implements RecipeSerializer<VEFluidRecipe> {

    public static final Codec<VEFluidRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            VERecipeCodecs.VE_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.ingredients),
            VERecipeCodecs.VE_FLUID_INGREDIENT_CODEC.listOf().fieldOf("fluid_ingredients").forGetter((getter) -> getter.fluidIngredientList),
            VERecipeCodecs.VE_OUTPUT_FLUID_CODEC.listOf().fieldOf("fluid_results").forGetter((getter) -> getter.fluidOutputList),
            CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.results),
            Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime)
    ).apply(instance, VEFluidRecipe::new));

    private static final FluidSerializerHelper<VEFluidRecipe> helper = new FluidSerializerHelper<>();

    @Nullable
    @Override
    public VEFluidRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
        return helper.fromNetwork(new VEFluidRecipe(), buffer);
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
