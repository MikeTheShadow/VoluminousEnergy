package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipeCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class IndustrialBlastingRecipe extends VEFluidRecipe {

    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.INDUSTRIAL_BLASTING.get();
    private static final RecipeSerializer<IndustrialBlastingRecipe> SERIALIZER = new RecipeSerializer<>() {

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
    };

    private int minimumHeat;

    public IndustrialBlastingRecipe() {

    }

    public IndustrialBlastingRecipe(List<Ingredient> i, List<FluidIngredient> fi, List<FluidStack> of, List<ItemStack> oi, int processTime,int minimumHeat) {
        super(i,fi,of,oi,processTime);
        this.minimumHeat = minimumHeat;
    }

    @Override
    public @NotNull RecipeType<? extends VERecipe> getType() {
        return RECIPE_TYPE;
    }
    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.BLAST_FURNACE_BLOCK.get());
    }

    public void setMinimumHeat(int minimumHeat) {
        this.minimumHeat = minimumHeat;
    }

    public int getMinimumHeat() {
        return minimumHeat;
    }


}
