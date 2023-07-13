package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.recipe.IExperienceRecipe;
import com.veteam.voluminousenergy.util.recipe.IngredientSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class CrusherRecipe extends VERecipe implements IRNGRecipe, IExperienceRecipe {

    public static final RecipeType<CrusherRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.CRUSHING.get();

    public static final Serializer SERIALIZER = new Serializer();

    public final ResourceLocation recipeId;

    private int minExperience;
    private int maxExperience;

    public CrusherRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    @Override
    public @NotNull ResourceLocation getId(){return recipeId;}

    @Override
    public @NotNull RecipeSerializer<?> getSerializer(){ return SERIALIZER;}

    @Override
    public @NotNull RecipeType<?> getType(){return RECIPE_TYPE;}
    @Override
    public @NotNull ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.CRUSHER_BLOCK.get());
    }

    @Override
    public float[] getRNGOutputs() {
        return new float[0];
    }

    @Override
    public void setRNGOutputs(float[] rngOutputs) {

    }

    @Override
    public int getMinExperience() {
        return this.minExperience;
    }

    @Override
    public int getMaxExperience() {
        return this.maxExperience;
    }

    @Override
    public void setBoth(int min, int max) {
        this.minExperience = min;
        this.maxExperience = min;
    }

    public static class Serializer implements RecipeSerializer<CrusherRecipe>{

        @Override
        public @NotNull CrusherRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json){
            CrusherRecipe recipe = new CrusherRecipe(recipeId);

            float[] rng = new float[2];

            JsonObject ingredientJson = json.get("ingredient").getAsJsonObject();

            int ingredientCount = GsonHelper.getAsInt(ingredientJson, "count", 1);
            Lazy<Ingredient> ingredientLazy = Lazy.of(() -> RecipeUtil.modifyIngredientAmounts(Ingredient.fromJson(ingredientJson), ingredientCount));
            recipe.lazyIngredients.add(ingredientLazy);
            recipe.processTime = GsonHelper.getAsInt(json,"process_time",200);

            ResourceLocation itemResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("result").getAsJsonObject(),"item","minecraft:air"),':');
            int itemAmount = GsonHelper.getAsInt(json.get("result").getAsJsonObject(),"count",1);


            ItemStack result = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(itemResourceLocation)),itemAmount);
            recipe.addResult(result);
            rng[0] = 1;

            ResourceLocation rngResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("rng").getAsJsonObject(),"item","minecraft:air"),':');
            int rngAmount = GsonHelper.getAsInt(json.get("rng").getAsJsonObject(),"count",0);
            float rngChance = GsonHelper.getAsFloat(json.get("rng").getAsJsonObject(),"chance",0); //Enter % as DECIMAL. Ie 50% = 0.5

            ItemStack rngResult = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(rngResourceLocation)),rngAmount);
            recipe.addResult(rngResult);
            rng[1] = rngChance;


            recipe.minExperience = GsonHelper.getAsInt(json.get("experience").getAsJsonObject(),"minimum",0);
            recipe.maxExperience = GsonHelper.getAsInt(json.get("experience").getAsJsonObject(),"maximum",0);

            recipe.setRNGOutputs(rng);

            return recipe;
        }

        IngredientSerializerHelper<CrusherRecipe> helper = new IngredientSerializerHelper<>();

        @Nullable
        @Override
        public CrusherRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer){
            CrusherRecipe recipe = new CrusherRecipe((recipeId));
            helper.fromNetwork(recipe,buffer);
            return recipe;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull CrusherRecipe recipe){
            helper.toNetwork(buffer,recipe);
        }
    }

}
