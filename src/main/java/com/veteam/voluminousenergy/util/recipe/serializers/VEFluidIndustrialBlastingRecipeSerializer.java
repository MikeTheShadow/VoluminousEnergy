package com.veteam.voluminousenergy.util.recipe.serializers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.recipe.IndustrialBlastingRecipe;
import com.veteam.voluminousenergy.util.recipe.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.CraftingRecipeCodecs;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class VEFluidIndustrialBlastingRecipeSerializer  implements RecipeSerializer<IndustrialBlastingRecipe> {

    public static final Codec<IndustrialBlastingRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            VERecipeCodecs.VE_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.ingredients),
            VERecipeCodecs.VE_FLUID_INGREDIENT_CODEC.listOf().fieldOf("fluid_ingredients").forGetter((getter) -> getter.fluidIngredientList),
            VERecipeCodecs.VE_OUTPUT_FLUID_CODEC.listOf().fieldOf("fluid_results").forGetter((getter) -> getter.fluidOutputList),
            CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.results),
            Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime),
            Codec.INT.fieldOf("minimum_heat").forGetter(IndustrialBlastingRecipe::getMinimumHeat)
    ).apply(instance, IndustrialBlastingRecipe::new));

    private static final FluidSerializerHelper<IndustrialBlastingRecipe> helper = new FluidSerializerHelper<>();

    @Nullable
    @Override
    public IndustrialBlastingRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
        IndustrialBlastingRecipe recipe = new IndustrialBlastingRecipe();
        recipe.setMinimumHeat(buffer.readInt());
        return helper.fromNetwork(recipe, buffer);
    }

    @Override
    public @NotNull Codec<IndustrialBlastingRecipe> codec() {
        return VE_RECIPE_CODEC;
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull IndustrialBlastingRecipe recipe) {
        buffer.writeInt(recipe.getMinimumHeat());
        helper.toNetwork(buffer, recipe);
    }
}
