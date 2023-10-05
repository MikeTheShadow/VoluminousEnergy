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

public class DistillationRecipe extends VEFluidRNGRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.DISTILLING.get();

    public DistillationRecipe() {
    }

    public DistillationRecipe(List<Ingredient> i, List<FluidIngredient> fi, List<FluidStack> of, List<ItemStack> oi, int processTime, List<Float> rngOutputs) {
        super(i, fi, of, oi, processTime, rngOutputs);
    }

    public static final RecipeSerializer<DistillationRecipe> SERIALIZER = new RecipeSerializer<>() {

        public static final Codec<DistillationRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                VERecipeCodecs.VE_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.ingredients),
                VERecipeCodecs.VE_FLUID_INGREDIENT_CODEC.listOf().fieldOf("fluid_ingredients").forGetter((getter) -> getter.fluidIngredientList),
                VERecipeCodecs.VE_OUTPUT_FLUID_CODEC.listOf().fieldOf("fluid_results").forGetter((getter) -> getter.fluidOutputList),
                CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.results),
                Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime),
                Codec.FLOAT.listOf().fieldOf("rng_values").forGetter((getter) -> getter.rngValues)
        ).apply(instance, DistillationRecipe::new));

        private static final FluidSerializerHelper<DistillationRecipe> helper = new FluidSerializerHelper<>();

        @Nullable
        @Override
        public DistillationRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
            return helper.fromNetwork(new DistillationRecipe(), buffer);
        }

        @Override
        public @NotNull Codec<DistillationRecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull DistillationRecipe recipe) {
            helper.toNetwork(buffer, recipe);
        }
    };

    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer(){ return SERIALIZER;}
    
    @Override
    public @NotNull ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.DISTILLATION_UNIT_BLOCK.get());
    }

    @Override
    public @NotNull RecipeType<VEFluidRecipe> getType(){return RECIPE_TYPE;}
}
