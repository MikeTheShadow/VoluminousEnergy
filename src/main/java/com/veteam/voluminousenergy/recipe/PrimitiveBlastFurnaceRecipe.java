package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class PrimitiveBlastFurnaceRecipe extends VERecipe {

    public static final RecipeType<PrimitiveBlastFurnaceRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.PRIMITIVE_BLAST_FURNACING.get();

    public static final Serializer SERIALIZER = new Serializer();

    public final ResourceLocation recipeId;
    public Lazy<Ingredient> ingredient;
    public int ingredientCount;
    public ItemStack result;
    private int processTime;
    private int outputAmount;

    public PrimitiveBlastFurnaceRecipe(ResourceLocation recipeId){ this.recipeId = recipeId; }


    @Override
    public Ingredient getIngredient() {
        return ingredient.get();
    }

    public int getIngredientCount() {
        return ingredientCount;
    }

    @Override
    public ItemStack getResult() { return result; }

    public int getProcessTime() { return processTime; }

    @Override
    public boolean matches(Container inv, Level worldIn){
        ItemStack stack = inv.getItem(0);
        int count = stack.getCount();
        return ingredient.get().test(stack) && count >= ingredientCount;
    }

    @Override
    public ItemStack assemble(Container inv){
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height){
        return true;
    }

    @Override
    public ItemStack getResultItem(){
        return result;
    }

    @Override
    public ResourceLocation getId(){
        return recipeId;
    }

    @Override
    public RecipeSerializer<?> getSerializer(){
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType(){
        return RECIPE_TYPE;
    }

    public int getOutputAmount(){
        return outputAmount;
    }

    @Override
    public ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.PRIMITIVE_BLAST_FURNACE_BLOCK.get());
    }

    public static class Serializer implements RecipeSerializer<PrimitiveBlastFurnaceRecipe>{

        @Override
        public PrimitiveBlastFurnaceRecipe fromJson(ResourceLocation recipeId, JsonObject json){

            PrimitiveBlastFurnaceRecipe recipe = new PrimitiveBlastFurnaceRecipe(recipeId);

            recipe.ingredient = Lazy.of(() -> Ingredient.fromJson(json.get("ingredient")));
            recipe.ingredientCount = GsonHelper.getAsInt(json.get("ingredient").getAsJsonObject(),"count",1);
            recipe.processTime = GsonHelper.getAsInt(json, "process_time", 200);

            ResourceLocation itemResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("result").getAsJsonObject(), "item", "minecraft:air"),':');
            int itemAmount = GsonHelper.getAsInt(json.get("result").getAsJsonObject(), "count", 1);
            recipe.result = new ItemStack(ForgeRegistries.ITEMS.getValue(itemResourceLocation));
            recipe.outputAmount = itemAmount;

            return recipe;
        }

        @Nullable
        @Override
        public PrimitiveBlastFurnaceRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
            PrimitiveBlastFurnaceRecipe recipe = new PrimitiveBlastFurnaceRecipe(recipeId);
            recipe.ingredientCount = buffer.readByte();
            recipe.result = buffer.readItem();
            recipe.processTime = buffer.readInt();
            recipe.outputAmount = buffer.readInt();

            Ingredient tempIngredient = Ingredient.fromNetwork(buffer);
            recipe.ingredient = Lazy.of(() -> tempIngredient);

            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, PrimitiveBlastFurnaceRecipe recipe){
            buffer.writeByte(recipe.getIngredientCount());
            buffer.writeItem(recipe.getResult());
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.outputAmount);

            recipe.ingredient.get().toNetwork(buffer);

        }
    }

}
