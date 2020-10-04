package com.veteam.voluminousenergy.recipe.CombustionGenerator;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.recipe.CentrifugalAgitatorRecipe;
import com.veteam.voluminousenergy.recipe.VERecipe;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CombustionGeneratorFuelRecipe extends VERecipe {
    public static final IRecipeType<CombustionGeneratorFuelRecipe> RECIPE_TYPE = IRecipeType.register("fuel_combustion");
    public static final Serializer SERIALIZER = new Serializer();

    public static ArrayList<Item> ingredientList = new ArrayList<>();

    private final ResourceLocation recipeId;
    private int volumetricEnergy;

    public ItemStack inputFluid;
    public ItemStack result;

    public CombustionGeneratorFuelRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    private final Map<Ingredient, Integer> ingredients = new LinkedHashMap<>();

    public Map<Ingredient, Integer> getIngredientMap() {
        return ImmutableMap.copyOf(ingredients);
    }

    public Ingredient getIngredient(){ return ingredient;}

    public int getIngredientCount(){ return ingredientCount;}

    public ItemStack getResult() {return result;}

    public FluidStack getInputFluid(){
        if (inputFluid.getItem() instanceof BucketItem){
            return new FluidStack(((BucketItem) inputFluid.getItem()).getFluid(), 1000);
        }
        return FluidStack.EMPTY;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn){
        ItemStack stack = inv.getStackInSlot(0);
        int count = stack.getCount();
        return ingredient.test(stack) && count >= ingredientCount;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv){return ItemStack.EMPTY;}

    @Override
    public boolean canFit(int width, int height){return true;}

    @Override
    public ItemStack getRecipeOutput(){return result;}

    @Override
    public ResourceLocation getId(){return recipeId;}

    @Override
    public IRecipeSerializer<?> getSerializer(){ return SERIALIZER;}

    @Override
    public IRecipeType<?> getType(){return RECIPE_TYPE;}

    public int getVolumetricEnergy() {return volumetricEnergy;}


    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CombustionGeneratorFuelRecipe> {
        @Override
        public CombustionGeneratorFuelRecipe read(ResourceLocation recipeId, JsonObject json) {
            CombustionGeneratorFuelRecipe recipe = new CombustionGeneratorFuelRecipe(recipeId);

            recipe.ingredient = Ingredient.deserialize(json.get("ingredient"));
            recipe.ingredientCount = JSONUtils.getInt(json.get("ingredient").getAsJsonObject(), "count", 1);
            recipe.volumetricEnergy = JSONUtils.getInt(json.get("ingredient").getAsJsonObject(), "volumetricEnergy", 102400);

            for (ItemStack stack : recipe.ingredient.getMatchingStacks()){
                if(!ingredientList.contains(stack.getItem())){
                    ingredientList.add(stack.getItem());
                }
            }

            recipe.result = new ItemStack(Items.BUCKET); // REQUIRED TO PREVENT JEI OR VANILLA RECIPE BOOK TO RETURN A NULL POINTER
            return recipe;
        }

        @Nullable
        @Override
        public CombustionGeneratorFuelRecipe read(ResourceLocation recipeId, PacketBuffer buffer){
            CombustionGeneratorFuelRecipe recipe = new CombustionGeneratorFuelRecipe((recipeId));
            recipe.ingredient = Ingredient.read(buffer);
            recipe.ingredientCount = buffer.readByte();
            recipe.result = buffer.readItemStack();
            recipe.volumetricEnergy = buffer.readInt();
            return recipe;
        }

        @Override
        public void write(PacketBuffer buffer, CombustionGeneratorFuelRecipe recipe){
            recipe.ingredient.write(buffer);
            buffer.writeByte(recipe.getIngredientCount());
            buffer.writeItemStack(recipe.getResult());
            buffer.writeInt(recipe.volumetricEnergy);
        }
    }
}
