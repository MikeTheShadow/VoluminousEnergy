package com.veteam.voluminousenergy.recipe.CombustionGenerator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.VERecipes;
import com.veteam.voluminousenergy.util.recipe.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CombustionGeneratorFuelRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.FUEL_COMBUSTION.get();
    private int volumetricEnergy;

    public CombustionGeneratorFuelRecipe() {
    }

    public CombustionGeneratorFuelRecipe(List<VERecipeCodecs.RegistryFluidIngredient> fi,int processTime,int volumetricEnergy) {
        super(new ArrayList<>(), fi, new ArrayList<>(), new ArrayList<>(), processTime);
        this.volumetricEnergy = volumetricEnergy;
    }

    public static final RecipeSerializer<CombustionGeneratorFuelRecipe> SERIALIZER = new RecipeSerializer<>() {

        public static final Codec<CombustionGeneratorFuelRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                VERecipeCodecs.VE_FLUID_INGREDIENT_CODEC.listOf().fieldOf("fluid_ingredients").forGetter((getter) -> getter.registryFluidIngredients),
                Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime),
                Codec.INT.fieldOf("volumetric_energy").forGetter((getter) -> getter.volumetricEnergy)
        ).apply(instance, CombustionGeneratorFuelRecipe::new));

        private static final FluidSerializerHelper<CombustionGeneratorFuelRecipe> helper = new FluidSerializerHelper<>();

        @Nullable
        @Override
        public CombustionGeneratorFuelRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
            return helper.fromNetwork(new CombustionGeneratorFuelRecipe(), buffer);
        }

        @Override
        public @NotNull Codec<CombustionGeneratorFuelRecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull CombustionGeneratorFuelRecipe recipe) {
            helper.toNetwork(buffer, recipe);
        }
    };

    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer(){ return SERIALIZER;}

    @Override
    public @NotNull RecipeType<VEFluidRecipe> getType(){return RECIPE_TYPE;}

    public int getVolumetricEnergy() {return volumetricEnergy;}

}
