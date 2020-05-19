package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.tools.api.FluidIngredient;
import com.veteam.voluminousenergy.tools.api.IFluidInventory;
import com.veteam.voluminousenergy.tools.api.IFluidRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CentrifugalAgitatorRecipe implements IFluidRecipe<IFluidInventory> {
    public static final IRecipeType<CentrifugalAgitatorRecipe> RECIPE_TYPE = IRecipeType.register("centrifugal_agitating");
    public static final Serializer SERIALIZER = new Serializer();

    private final ResourceLocation recipeId;
    private int processTime;
    private final List<FluidIngredient> ingredients = NonNullList.create();

    public FluidStack inputFluid;
    public FluidStack result;
    public FluidStack secondResult;
    public int inputAmount;
    public int outputAmount;
    public int secondAmount;

    public CentrifugalAgitatorRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    public int getProcessTime(){
        return processTime;
    }

    @Override
    public List<FluidStack> getFluidResults(IFluidInventory inv) {
        return getFluidOutputs();
    }

    @Override
    public List<FluidStack> getFluidOutputs() {
        return Collections.singletonList(result.copy());
    }

    @Override
    public List<FluidIngredient> getFluidIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    @Override
    public boolean matches(IFluidInventory inv, World worldIn) {
        return false;
    }

    @Override
    public ResourceLocation getId() {
        return recipeId;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return RECIPE_TYPE;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CentrifugalAgitatorRecipe> {
        @Override
        public CentrifugalAgitatorRecipe read(ResourceLocation recipeId, JsonObject json) {
            CentrifugalAgitatorRecipe recipe = new CentrifugalAgitatorRecipe(recipeId);

            recipe.processTime = JSONUtils.getInt(json, "processTime");


            JSONUtils.getJsonArray(json, "ingredients").forEach(e ->
                    recipe.ingredients.add(FluidIngredient.deserialize(e.getAsJsonObject())));

            ResourceLocation fluidOutputResourceLocation = ResourceLocation.create(JSONUtils.getString(json.get("first_result").getAsJsonObject(), "fluid", "minecraft:empty"),':');
            int outputFluidAmount = JSONUtils.getInt(json.get("first_result").getAsJsonObject(), "amount", 0);
            recipe.result = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluidOutputResourceLocation),outputFluidAmount);
            recipe.outputAmount = outputFluidAmount;

            ResourceLocation secondFluidOutputResourceLocation = ResourceLocation.create(JSONUtils.getString(json.get("second_result").getAsJsonObject(), "fluid", "minecraft:empty"),':');
            int secondOutputFluidAmount = JSONUtils.getInt(json.get("second_result").getAsJsonObject(), "amount", 0);
            recipe.secondResult = new FluidStack(ForgeRegistries.FLUIDS.getValue(secondFluidOutputResourceLocation),secondOutputFluidAmount);
            recipe.secondAmount = secondOutputFluidAmount;

            //recipe.result = IFluidRecipe.deserializeFluid(JSONUtils.getJsonObject(json, "result"));

            return recipe;
        }

        @Nullable
        @Override
        public CentrifugalAgitatorRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            CentrifugalAgitatorRecipe recipe = new CentrifugalAgitatorRecipe(recipeId);
            recipe.processTime = buffer.readVarInt();
            int ingredientCount = buffer.readByte();
            for (int i = 0; i < ingredientCount; ++i) {
                recipe.ingredients.add(FluidIngredient.read(buffer));
            }
            recipe.result = IFluidRecipe.readFluid(buffer);
            return recipe;
        }

        @Override
        public void write(PacketBuffer buffer, CentrifugalAgitatorRecipe recipe) {
            buffer.writeVarInt(recipe.processTime);
            buffer.writeByte(recipe.ingredients.size());
            recipe.ingredients.forEach(ingredient -> ingredient.write(buffer));
            IFluidRecipe.writeFluid(buffer, recipe.result);
        }
    }
}
