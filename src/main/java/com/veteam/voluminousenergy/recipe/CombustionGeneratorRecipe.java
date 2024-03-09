package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.recipe.parser.RecipeParser;
import com.veteam.voluminousenergy.recipe.serializer.FluidSerializerHelper;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CombustionGeneratorRecipe extends VERecipe {
    public static final RecipeType<VERecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.FUEL_COMBUSTION.get();

    private final RecipeParser parser = RecipeParser.forRecipe(this)
            .addFluidIngredient(0,0)
            .addFluidIngredient(1,1);

    public CombustionGeneratorRecipe() {
    }

    public CombustionGeneratorRecipe(List<VERecipeCodecs.RegistryFluidIngredient> fi) {
        super(new ArrayList<>(), fi, new ArrayList<>(), new ArrayList<>(), Config.COMBUSTION_GENERATOR_FIXED_TICK_TIME.get());
    }

    public static final RecipeSerializer<CombustionGeneratorRecipe> SERIALIZER = new RecipeSerializer<>() {

        public static final Codec<CombustionGeneratorRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                VERecipeCodecs.VE_FLUID_INGREDIENT_CODEC.listOf().fieldOf("fluid_ingredients").forGetter((getter) -> getter.registryFluidIngredients)
        ).apply(instance, CombustionGeneratorRecipe::new));

        private static final FluidSerializerHelper<CombustionGeneratorRecipe> helper = new FluidSerializerHelper<>();

        @Nullable
        @Override
        public CombustionGeneratorRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
            return helper.fromNetwork(new CombustionGeneratorRecipe(), buffer);
        }

        @Override
        public @NotNull Codec<CombustionGeneratorRecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull CombustionGeneratorRecipe recipe) {
            helper.toNetwork(buffer, recipe);
        }
    };

    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer(){ return SERIALIZER;}

    @Override
    public RecipeParser getParser() {
        return parser;
    }

    @Override
    public @NotNull RecipeType<VERecipe> getType(){return RECIPE_TYPE;}

}
