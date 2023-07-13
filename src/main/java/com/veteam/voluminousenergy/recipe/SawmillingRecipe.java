package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.recipe.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import com.veteam.voluminousenergy.util.recipe.VEFluidIngredientSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class SawmillingRecipe extends VEFluidRecipe {

    public static final RecipeType<SawmillingRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.SAWMILLING.get();

    public static final Serializer SERIALIZER = new Serializer();
    private boolean isLogRecipe;
    private final ResourceLocation recipeId;

    public SawmillingRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    @Override
    public @NotNull ResourceLocation getId(){return recipeId;}

    @Override
    public @NotNull RecipeSerializer<?> getSerializer(){ return SERIALIZER;}

    @Override
    public @NotNull RecipeType getType(){return RECIPE_TYPE;}

    @Override
    public @NotNull ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.SAWMILL_BLOCK.get());
    }

    public boolean isLogRecipe() {
        return isLogRecipe;
    }

    public static class Serializer implements RecipeSerializer<SawmillingRecipe>{

        @Override
        public @NotNull SawmillingRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json){
            SawmillingRecipe recipe = new SawmillingRecipe(recipeId);

            recipe.isLogRecipe = GsonHelper.getAsBoolean(json, "auto_log_to_plank", false);

            if (recipe.isLogRecipe) return recipe;

            JsonObject ingredientJson = json.get("ingredient").getAsJsonObject();
            int ingredientCount = GsonHelper.getAsInt(ingredientJson, "count", 1);
            Lazy<Ingredient> ingredientLazy = Lazy.of(() -> RecipeUtil.modifyIngredientAmounts(Ingredient.fromJson(ingredientJson), ingredientCount));
            recipe.processTime = GsonHelper.getAsInt(json,"process_time",200);
            recipe.lazyIngredientList.add(ingredientLazy);

            JsonObject resultData = json.get("result").getAsJsonObject();
            ResourceLocation itemResourceLocation = ResourceLocation.of(GsonHelper.getAsString(resultData,"item","minecraft:air"),':');
            int itemAmount = GsonHelper.getAsInt(resultData,"count",1);
            ItemStack result = new ItemStack(ForgeRegistries.ITEMS.getValue(itemResourceLocation),itemAmount);
            recipe.addItemOutput(result);

            JsonObject secondResultData = json.get("second_result").getAsJsonObject();
            ResourceLocation secondResultResourceLocation = ResourceLocation.of(GsonHelper.getAsString(secondResultData,"item","minecraft:air"),':');
            int secondResultAmount = GsonHelper.getAsInt(secondResultData,"count",0);

            ItemStack secondResult = new ItemStack(ForgeRegistries.ITEMS.getValue(secondResultResourceLocation),secondResultAmount);
            recipe.addItemOutput(secondResult);

            JsonObject outputFluid = json.get("output_fluid").getAsJsonObject();
            ResourceLocation outputFluidRL = ResourceLocation.of(GsonHelper.getAsString(outputFluid, "fluid", "minecraft:air"),':');
            int fluidAmount = GsonHelper.getAsInt(outputFluid, "amount", 0);

            FluidStack outputFluidStack = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(outputFluidRL)), fluidAmount);
            recipe.addFluidOutput(outputFluidStack);

            return recipe;
        }

        FluidSerializerHelper<SawmillingRecipe> helper = new FluidSerializerHelper<>();

        @Nullable
        @Override
        public SawmillingRecipe fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buffer){
            SawmillingRecipe recipe = new SawmillingRecipe((recipeId));

            recipe.isLogRecipe = buffer.readBoolean();
            if (!recipe.isLogRecipe){
                helper.fromNetwork(recipe,buffer);
            }

            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SawmillingRecipe recipe){
            buffer.writeBoolean(recipe.isLogRecipe);
            if (!recipe.isLogRecipe){
                helper.toNetwork(buffer,recipe);
            }
        }
    }
}
