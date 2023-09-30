package com.veteam.voluminousenergy.recipe.CombustionGenerator;

import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.recipe.VERecipes;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.FluidSerializerHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class CombustionGeneratorOxidizerRecipe extends VEFluidRecipe {
    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.OXIDIZING.get();

    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation recipeId;

    public CombustionGeneratorOxidizerRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }
    @Override
    public @NotNull ResourceLocation getId(){return recipeId;}

    @Override
    public @NotNull RecipeSerializer<?> getSerializer(){ return SERIALIZER;}

    @Override
    public @NotNull RecipeType<VEFluidRecipe> getType() {
        return RECIPE_TYPE;
    }

    public static class Serializer implements RecipeSerializer<CombustionGeneratorOxidizerRecipe> {
        @Override
        public @NotNull CombustionGeneratorOxidizerRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            CombustionGeneratorOxidizerRecipe recipe = new CombustionGeneratorOxidizerRecipe(recipeId);

            int processTime = GsonHelper.getAsInt(json,"process_time",1600);
            recipe.setProcessTime(processTime);

            JsonObject inputFluid = json.get("input_fluid").getAsJsonObject();
            recipe.getLazyFluidIngredientList().add(Lazy.of(() -> FluidIngredient.fromJson(inputFluid)));
            return recipe;
        }

        FluidSerializerHelper<CombustionGeneratorOxidizerRecipe> helper = new FluidSerializerHelper<>();

        @Nullable
        @Override
        public CombustionGeneratorOxidizerRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer){
            CombustionGeneratorOxidizerRecipe recipe = new CombustionGeneratorOxidizerRecipe((recipeId));
            helper.fromNetwork(recipe,buffer);
            return recipe;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull CombustionGeneratorOxidizerRecipe recipe){
            helper.toNetwork(buffer,recipe);
        }

    }

}