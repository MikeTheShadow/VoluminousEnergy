package com.veteam.voluminousenergy.recipe.CombustionGenerator;

import com.google.gson.JsonObject;
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
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class CombustionGeneratorOxidizerRecipe extends VERecipe {
    public static final IRecipeType<CombustionGeneratorOxidizerRecipe> RECIPE_TYPE = IRecipeType.register("oxidizer_combustion");
    public static final Serializer SERIALIZER = new Serializer();

    public static ArrayList<Item> ingredientList = new ArrayList<>();

    private final ResourceLocation recipeId;
    private int processTime;

    public ItemStack inputFluid;
    public ItemStack result;
    public ItemStack secondResult;

    public CombustionGeneratorOxidizerRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
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

    public int getProcessTime() { return processTime; }


    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CombustionGeneratorOxidizerRecipe> {
        @Override
        public CombustionGeneratorOxidizerRecipe read(ResourceLocation recipeId, JsonObject json) {
            CombustionGeneratorOxidizerRecipe recipe = new CombustionGeneratorOxidizerRecipe(recipeId);

            recipe.ingredient = Ingredient.deserialize(json.get("ingredient"));
            recipe.ingredientCount = JSONUtils.getInt(json.get("ingredient").getAsJsonObject(), "count", 1);
            recipe.processTime = JSONUtils.getInt(json,"processTime",1600);

            for (ItemStack stack : recipe.ingredient.getMatchingStacks()){
                if(!ingredientList.contains(stack.getItem())){
                    ingredientList.add(stack.getItem());
                }
            }

            recipe.result = new ItemStack(Items.AIR); // REQUIRED TO PREVENT JEI OR VANILLA RECIPE BOOK TO RETURN A NULL POINTER
            return recipe;
        }

        @Nullable
        @Override
        public CombustionGeneratorOxidizerRecipe read(ResourceLocation recipeId, PacketBuffer buffer){
            CombustionGeneratorOxidizerRecipe recipe = new CombustionGeneratorOxidizerRecipe((recipeId));
            recipe.ingredient = Ingredient.read(buffer);
            recipe.ingredientCount = buffer.readByte();
            recipe.processTime = buffer.readInt();
            return recipe;
        }

        @Override
        public void write(PacketBuffer buffer, CombustionGeneratorOxidizerRecipe recipe){
            recipe.ingredient.write(buffer);
            buffer.writeByte(recipe.getIngredientCount());
            buffer.writeItemStack(recipe.getResult());
            buffer.writeInt(recipe.processTime);
        }
    }
}