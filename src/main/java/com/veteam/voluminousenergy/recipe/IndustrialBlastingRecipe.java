package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class IndustrialBlastingRecipe extends VEFluidRecipe {

    public final ResourceLocation recipeId;
    private int minimumHeat;

    public static final RecipeType<VEFluidRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.INDUSTRIAL_BLASTING.get();

    public static final Serializer SERIALIZER = new Serializer();

    public IndustrialBlastingRecipe(ResourceLocation recipeId) {
        this.recipeId = recipeId;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return recipeId;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<VEFluidRecipe> getType() {
        return RECIPE_TYPE;
    }

    @Override
    public List<FluidIngredient> getFluidIngredients() {
        if (this.fluidIngredientList == null) {
            fluidIngredientList = new ArrayList<>();
            Collection<Fluid> fluids = ForgeRegistries.FLUIDS.getValues();

            List<FluidStack> fluidStacks = new ArrayList<>();
            for(Fluid fluid : fluids) {
                if(fluid.getFluidType().getTemperature() < this.minimumHeat) continue;
                fluidStacks.add(new FluidStack(fluid,Config.BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION.get()));
            }

            FluidIngredient ingredient = FluidIngredient.of(fluidStacks.stream());
            fluidIngredientList.add(ingredient);
        }
        return this.fluidIngredientList;
    }

    public int getMinimumHeat() {
        return minimumHeat;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.BLAST_FURNACE_BLOCK.get());
    }

    public static class Serializer implements RecipeSerializer<IndustrialBlastingRecipe> {

        @Override
        public @NotNull IndustrialBlastingRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json) {
            IndustrialBlastingRecipe recipe = new IndustrialBlastingRecipe(recipeId);

            final JsonObject ingredientJson = json.get("ingredient").getAsJsonObject();

            int amounts = GsonHelper.getAsInt(ingredientJson, "count", 1);
            Lazy<Ingredient> ingredientLazy = Lazy.of(() -> RecipeUtil.modifyIngredientAmounts(Ingredient.fromJson(ingredientJson), amounts));
            recipe.lazyIngredientList.add(ingredientLazy);

            recipe.processTime = GsonHelper.getAsInt(json, "process_time", 200);

            JsonObject secondInput = json.get("second_input").getAsJsonObject();


            int secondInputAmount = GsonHelper.getAsInt(secondInput, "count", 1);
            Lazy<Ingredient> secondIngredient = Lazy.of(() -> RecipeUtil.pullUnknownItemFromJSON(secondInput, secondInputAmount));
            recipe.lazyIngredientList.add(secondIngredient);

            // Main Output Slot
            ResourceLocation itemResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("result").getAsJsonObject(), "item", "minecraft:air"), ':');
            int outputAmount = GsonHelper.getAsInt(json.get("result").getAsJsonObject(), "count", 1);
            ItemStack result = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(itemResourceLocation)), outputAmount);
            recipe.addItemOutput(result);
            // Minimum heat
            recipe.minimumHeat = GsonHelper.getAsInt(json, "minimum_heat_kelvin", 300); // Minimum heat in Kelvin (K), default fluid temperature is 300 K

            return recipe;
        }

        FluidSerializerHelper<IndustrialBlastingRecipe> helper = new FluidSerializerHelper<>();

        @Nullable
        @Override
        public IndustrialBlastingRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            IndustrialBlastingRecipe recipe = new IndustrialBlastingRecipe(recipeId);

            helper.fromNetwork(recipe, buffer);
            recipe.minimumHeat = buffer.readInt();

            return recipe;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull IndustrialBlastingRecipe recipe) {
            helper.toNetwork(buffer, recipe);
            buffer.writeInt(recipe.minimumHeat);
        }
    }
}
