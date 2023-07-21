package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.recipe.IngredientSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public class CentrifugalSeparatorRecipe extends VERecipe implements IRNGRecipe {

    public static final RecipeType<CentrifugalSeparatorRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.CENTRIFUGAL_SEPARATION.get();

    public static final CentrifugalSeparatorRecipe.Serializer SERIALIZER = new CentrifugalSeparatorRecipe.Serializer();

    private float[] rngOutputs;

    private final Map<Ingredient, Integer> ingredients = new LinkedHashMap<>();

    public CentrifugalSeparatorRecipe(ResourceLocation recipeId){
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
        return new ItemStack(VEBlocks.CENTRIFUGAL_SEPARATOR_BLOCK.get());
    }

    @Override
    public float[] getRNGOutputs() {
        return rngOutputs;
    }

    @Override
    public void setRNGOutputs(float[] rngOutputs) {
        this.rngOutputs = rngOutputs;
    }

    public static class Serializer implements RecipeSerializer<CentrifugalSeparatorRecipe>{

        @Override
        public @NotNull CentrifugalSeparatorRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json){
            CentrifugalSeparatorRecipe recipe = new CentrifugalSeparatorRecipe(recipeId);

            float[] rngOutputs = new float[4];

            JsonObject ingredientJson = json.get("ingredient").getAsJsonObject();

            int ingredientCount = GsonHelper.getAsInt(ingredientJson, "count", 1);
            Lazy<Ingredient> ingredientLazy = Lazy.of(() -> RecipeUtil.modifyIngredientAmounts(Ingredient.fromJson(ingredientJson), ingredientCount));
            recipe.addLazyIngredient(ingredientLazy);

            recipe.processTime = GsonHelper.getAsInt(json,"process_time",200);

            // Main Output Slot
            ResourceLocation itemResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("result").getAsJsonObject(),"item","minecraft:air"),':');
            int itemAmount = GsonHelper.getAsInt(json.get("result").getAsJsonObject(),"count",1);
            int bucketNeeded = GsonHelper.getAsInt(json.get("result").getAsJsonObject(),"buckets",0);
            ItemStack result = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(itemResourceLocation)),itemAmount);
            recipe.addResult(result);
            rngOutputs[0] = 1;



            Ingredient bucketIngredient;

            if(bucketNeeded != 0) {
                List<ItemStack> buckets = new ArrayList<>();
                buckets.add(new ItemStack(Items.BUCKET,bucketNeeded));
                bucketIngredient = Ingredient.of(buckets.stream());
            } else {
                bucketIngredient = Ingredient.EMPTY;
            }
            recipe.addLazyIngredient(Lazy.of(() -> bucketIngredient)); // Note you must use lazy otherwise it'll get overwritten


            // First RNG Slot, RNG 0
            ResourceLocation rngResourceLocation0 = ResourceLocation.of(GsonHelper.getAsString(json.get("rng_slot_0").getAsJsonObject(),"item","minecraft:air"),':');
            int rngAmount0 = GsonHelper.getAsInt(json.get("rng_slot_0").getAsJsonObject(),"count",0);
            float rngChance0 = GsonHelper.getAsFloat(json.get("rng_slot_0").getAsJsonObject(),"chance",0); //Enter % as DECIMAL. Ie 50% = 0.5

            ItemStack rngItem0 = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(rngResourceLocation0)),rngAmount0);
            recipe.addResult(rngItem0);
            rngOutputs[1] = rngChance0;

            //Second RNG Slot, RNG 1
            ResourceLocation rngResourceLocation1 = ResourceLocation.of(GsonHelper.getAsString(json.get("rng_slot_1").getAsJsonObject(),"item","minecraft:air"),':');
            int rngAmount1 = GsonHelper.getAsInt(json.get("rng_slot_1").getAsJsonObject(),"count",0);
            float rngChance1 = GsonHelper.getAsFloat(json.get("rng_slot_1").getAsJsonObject(),"chance",0); //Enter % as DECIMAL. Ie 50% = 0.5

            ItemStack rngItem1 = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(rngResourceLocation1)),rngAmount1);
            recipe.addResult(rngItem1);
            rngOutputs[2]  = rngChance1;

            //Third RNG Slot, RNG 2
            ResourceLocation rngResourceLocation2 = ResourceLocation.of(GsonHelper.getAsString(json.get("rng_slot_2").getAsJsonObject(),"item","minecraft:air"),':');
            int rngAmount2 = GsonHelper.getAsInt(json.get("rng_slot_2").getAsJsonObject(),"count",0);
            float rngChance2 = GsonHelper.getAsFloat(json.get("rng_slot_2").getAsJsonObject(),"chance",0); //Enter % as DECIMAL. Ie 50% = 0.5

            ItemStack rngItem2 = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(rngResourceLocation2)));
            recipe.addResult(rngItem2);
            rngOutputs[3]  = rngChance2;

            recipe.setRNGOutputs(rngOutputs);

            return recipe;
        }

        IngredientSerializerHelper<CentrifugalSeparatorRecipe> helper = new IngredientSerializerHelper<>();

        @Nullable
        @Override
        public CentrifugalSeparatorRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer){
            CentrifugalSeparatorRecipe recipe = new CentrifugalSeparatorRecipe(recipeId);
            helper.fromNetwork(recipe,buffer);
            return recipe;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull CentrifugalSeparatorRecipe recipe){
            helper.toNetwork(buffer,recipe);
        }
    }

}
