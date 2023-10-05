package com.veteam.voluminousenergy.recipe.CombustionGenerator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.VERecipes;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CombustionGeneratorOxidizerRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.OXIDIZING.get();

    public CombustionGeneratorOxidizerRecipe() {

    }

    public CombustionGeneratorOxidizerRecipe(List<FluidIngredient> fi, int processTime) {
        super(new ArrayList<>(), fi, new ArrayList<>(), new ArrayList<>(), processTime);
    }

    public static final RecipeSerializer<CombustionGeneratorOxidizerRecipe> SERIALIZER = new RecipeSerializer<>() {
        public static final Codec<CombustionGeneratorOxidizerRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                VERecipeCodecs.VE_FLUID_INGREDIENT_CODEC.listOf().fieldOf("fluid_ingredients").forGetter((getter) -> getter.fluidIngredientList),
                Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime)
        ).apply(instance, CombustionGeneratorOxidizerRecipe::new));

        private static final FluidSerializerHelper<CombustionGeneratorOxidizerRecipe> helper = new FluidSerializerHelper<>();

        @Nullable
        @Override
        public CombustionGeneratorOxidizerRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
            return helper.fromNetwork(new CombustionGeneratorOxidizerRecipe(), buffer);
        }

        @Override
        public @NotNull Codec<CombustionGeneratorOxidizerRecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull CombustionGeneratorOxidizerRecipe recipe) {
            helper.toNetwork(buffer, recipe);
        }
    };

    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<VEFluidRecipe> getType() {
        return RECIPE_TYPE;
    }

}