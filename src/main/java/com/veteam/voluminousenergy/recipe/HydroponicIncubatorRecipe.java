package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.serializer.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class HydroponicIncubatorRecipe extends VEFluidRNGRecipe {
    public static final RecipeType<VERecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.HYDROPONIC_INCUBATING.get();

    public HydroponicIncubatorRecipe() {
    }

    public HydroponicIncubatorRecipe(List<VERecipeCodecs.RegistryIngredient> i, List<VERecipeCodecs.RegistryFluidIngredient> fi, List<VERecipeCodecs.VEChancedItemWithCount> oi, int processTime) {
        super(i, fi, List.of(), oi, processTime);
    }

    public static final RecipeSerializer<HydroponicIncubatorRecipe> SERIALIZER = new RecipeSerializer<>() {

        public static final Codec<HydroponicIncubatorRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                VERecipeCodecs.VE_LAZY_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.registryIngredients),
                VERecipeCodecs.VE_FLUID_INGREDIENT_CODEC.listOf().fieldOf("fluid_ingredients").forGetter((getter) -> getter.registryFluidIngredients),
                VERecipeCodecs.VE_CHANCED_OUTPUT_ITEM_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.itemResultsWithChance),
                Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime)
        ).apply(instance, HydroponicIncubatorRecipe::new));

        private static final FluidSerializerHelper<HydroponicIncubatorRecipe> helper = new FluidSerializerHelper<>();

        @Nullable
        @Override
        public HydroponicIncubatorRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
            return helper.fromNetwork(new HydroponicIncubatorRecipe(), buffer);
        }

        @Override
        public @NotNull Codec<HydroponicIncubatorRecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull HydroponicIncubatorRecipe recipe) {
            helper.toNetwork(buffer, recipe);
        }
    };

    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer(){ return SERIALIZER;}
    @Override
    public @NotNull RecipeType<VERecipe> getType() {
        return RECIPE_TYPE;
    }


    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.HYDROPONIC_INCUBATOR_BLOCK.get());
    }
}
