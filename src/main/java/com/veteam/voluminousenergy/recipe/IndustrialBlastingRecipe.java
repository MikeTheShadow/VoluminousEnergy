package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import com.veteam.voluminousenergy.util.recipe.IngredientSerializerHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class IndustrialBlastingRecipe extends VERecipe {

    public final ResourceLocation recipeId;
    private int minimumHeat;

    public static final RecipeType<IndustrialBlastingRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.INDUSTRIAL_BLASTING.get();

    public static final Serializer SERIALIZER = new Serializer();

    public IndustrialBlastingRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    @Override
    public @NotNull ResourceLocation getId(){return recipeId;}

    @Override
    public @NotNull RecipeSerializer<?> getSerializer(){ return SERIALIZER;}

    @Override
    public @NotNull RecipeType<? extends Recipe> getType(){return RECIPE_TYPE;}

    public int getMinimumHeat(){ return minimumHeat; }

    @Override
    public @NotNull ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.BLAST_FURNACE_BLOCK.get());
    }

    public static class Serializer implements RecipeSerializer<IndustrialBlastingRecipe>{

        @Override
        public @NotNull IndustrialBlastingRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json){
            IndustrialBlastingRecipe recipe = new IndustrialBlastingRecipe(recipeId);

            final JsonObject ingredientJson = json.get("ingredient").getAsJsonObject();

            int amounts = GsonHelper.getAsInt(ingredientJson, "count", 1);
            Lazy<Ingredient> ingredientLazy = Lazy.of(() -> RecipeUtil.modifyIngredientAmounts(Ingredient.fromJson(ingredientJson),amounts));
            recipe.addLazyIngredient(ingredientLazy);

            recipe.processTime = GsonHelper.getAsInt(json,"process_time",200);

            JsonObject secondInput = json.get("second_input").getAsJsonObject();


            int secondInputAmount = GsonHelper.getAsInt(secondInput,"count",1);
            Lazy<Ingredient> secondIngredient = Lazy.of(() -> RecipeUtil.pullUnknownItemFromJSON(secondInput,secondInputAmount));
            recipe.addLazyIngredient(secondIngredient);

            // Main Output Slot
            ResourceLocation itemResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("result").getAsJsonObject(),"item","minecraft:air"),':');
            int outputAmount = GsonHelper.getAsInt(json.get("result").getAsJsonObject(),"count",1);
            ItemStack result = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(itemResourceLocation)),outputAmount);
            recipe.addResult(result);
            // Minimum heat
            recipe.minimumHeat = GsonHelper.getAsInt(json,"minimum_heat_kelvin",300); // Minimum heat in Kelvin (K), default fluid temperature is 300 K

            return recipe;
        }

        IngredientSerializerHelper<IndustrialBlastingRecipe> helper = new IngredientSerializerHelper<>();

        @Nullable
        @Override
        public IndustrialBlastingRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer){
            IndustrialBlastingRecipe recipe = new IndustrialBlastingRecipe(recipeId);

            helper.fromNetwork(recipe,buffer);
            recipe.minimumHeat = buffer.readInt();

            return recipe;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull IndustrialBlastingRecipe recipe){
            helper.toNetwork(buffer,recipe);
            buffer.writeInt(recipe.minimumHeat);
        }
    }
}
