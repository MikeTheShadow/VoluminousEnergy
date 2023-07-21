package com.veteam.voluminousenergy.recipe.CombustionGenerator;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.recipe.VERecipes;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.veteam.voluminousenergy.blocks.tiles.CombustionGeneratorTile.COMBUSTION_GENERATOR_CONSUMPTION_AMOUNT;

public class CombustionGeneratorFuelRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.FUEL_COMBUSTION.get();

    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation recipeId;
    private int volumetricEnergy;

    public static List<Lazy<Pair<List<Fluid>,Integer>>> lazyFluidsWithVolumetricEnergy = new ArrayList<>();

    public CombustionGeneratorFuelRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    public CombustionGeneratorFuelRecipe() {
        recipeId = null;
    }

    private final Map<Ingredient, Integer> ingredients = new LinkedHashMap<>();

    public Map<Ingredient, Integer> getIngredientMap() {
        return ImmutableMap.copyOf(ingredients);
    }

    @Override
    public @NotNull ResourceLocation getId(){return recipeId;}

    @Override
    public @NotNull RecipeSerializer<?> getSerializer(){ return SERIALIZER;}

    @Override
    public @NotNull RecipeType<VEFluidRecipe> getType(){return RECIPE_TYPE;}

    @Override
    public int getProcessTime() { // Just get the volumetric energy for now
        return getVolumetricEnergy();
    }

    public int getVolumetricEnergy() {return volumetricEnergy;}


    public static class Serializer implements RecipeSerializer<CombustionGeneratorFuelRecipe> {
        @Override
        public @NotNull CombustionGeneratorFuelRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json) {
            CombustionGeneratorFuelRecipe recipe = new CombustionGeneratorFuelRecipe(recipeId);

            int ingredientCount = GsonHelper.getAsInt(json.get("ingredient").getAsJsonObject(), "count", 1);
            recipe.getLazyIngredientList()
                    .add(Lazy.of(() -> RecipeUtil.modifyIngredientAmounts(Ingredient.fromJson(json.get("ingredient")), ingredientCount)));
            recipe.volumetricEnergy = GsonHelper.getAsInt(json.get("ingredient").getAsJsonObject(), "volumetric_energy", 102400);

            recipe.getLazyFluidIngredientList().add(Lazy.of(()
                    -> FluidIngredient.fromJsonNoAmount(json.get("input_fluid").getAsJsonObject(),COMBUSTION_GENERATOR_CONSUMPTION_AMOUNT)));

            lazyFluidsWithVolumetricEnergy.add(Lazy.of(()
                    -> new Pair<>(Arrays.stream(
                            recipe.getFluidIngredientList().get(0).getFluids())
                    .map(FluidStack::getFluid).collect(Collectors.toList()), recipe.volumetricEnergy)));
            return recipe;
        }

        FluidSerializerHelper<CombustionGeneratorFuelRecipe> helper = new FluidSerializerHelper<>();

        @Nullable
        @Override
        public CombustionGeneratorFuelRecipe fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buffer){
            CombustionGeneratorFuelRecipe recipe = new CombustionGeneratorFuelRecipe((recipeId));
            recipe.volumetricEnergy = buffer.readInt();

            lazyFluidsWithVolumetricEnergy.add(Lazy.of(()
                    -> new Pair<>(Arrays.stream(
                            recipe.getFluidIngredientList().get(0).getFluids())
                    .map(FluidStack::getFluid).collect(Collectors.toList()), recipe.volumetricEnergy)));
           helper.fromNetwork(recipe,buffer);

            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CombustionGeneratorFuelRecipe recipe){
            buffer.writeInt(recipe.volumetricEnergy);
            helper.toNetwork(buffer,recipe);
        }
    }
}
