package com.veteam.voluminousenergy.recipe;

import com.google.common.collect.ImmutableMap;
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
import java.util.LinkedHashMap;
import java.util.Map;

public class CrusherRecipe extends VERecipe {

    public static final RecipeType<CrusherRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.CRUSHING;

    public static final Serializer SERIALIZER = new Serializer();

    public final ResourceLocation recipeId;
    public Lazy<Ingredient> ingredient;
    public int ingredientCount;
    public ItemStack result;
    public ItemStack rngResult;
    private int processTime;
    private int outputAmount;
    private int outputRngAmount;
    private float chance;

    private final Map<Ingredient, Integer> ingredients = new LinkedHashMap<>();


    public CrusherRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    public Ingredient getIngredient(){ return ingredient.get();}

    public int getIngredientCount(){ return ingredientCount;}

    public ItemStack getResult() {return result;}

    public ItemStack getRngItem(){return rngResult;}

    public float getChance(){return chance;}

    @Override
    public boolean matches(Container inv, Level worldIn){
        ItemStack stack = inv.getItem(0);
        int count = stack.getCount();
        return ingredient.get().test(stack) && count >= ingredientCount;
    }

    @Override
    public ItemStack assemble(Container inv){return ItemStack.EMPTY;}

    @Override
    public boolean canCraftInDimensions(int width, int height){return true;}

    @Override
    public ItemStack getResultItem(){return result;}

    @Override
    public ResourceLocation getId(){return recipeId;}

    @Override
    public RecipeSerializer<?> getSerializer(){ return SERIALIZER;}

    @Override
    public RecipeType<?> getType(){return RECIPE_TYPE;}

    public int getOutputAmount() {return outputAmount;}

    public int getOutputRngAmount(){return outputRngAmount;}

    public int getProcessTime() { return processTime; }

    public Map<Ingredient, Integer> getIngredientMap() {
        return ImmutableMap.copyOf(ingredients);
    }

    @Override
    public ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.CRUSHER_BLOCK);
    }

    public static class Serializer implements RecipeSerializer<CrusherRecipe>{

        @Override
        public CrusherRecipe fromJson(ResourceLocation recipeId, JsonObject json){
            CrusherRecipe recipe = new CrusherRecipe(recipeId);

            JsonObject ingredientJson = json.get("ingredient").getAsJsonObject();

            recipe.ingredient = Lazy.of(() -> Ingredient.fromJson(ingredientJson));
            recipe.ingredientCount = GsonHelper.getAsInt(ingredientJson, "count", 1);
            recipe.processTime = GsonHelper.getAsInt(json,"process_time",200);

            ResourceLocation itemResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("result").getAsJsonObject(),"item","minecraft:air"),':');
            int itemAmount = GsonHelper.getAsInt(json.get("result").getAsJsonObject(),"count",1);
            recipe.result = new ItemStack(ForgeRegistries.ITEMS.getValue(itemResourceLocation));
            recipe.outputAmount = itemAmount;

            ResourceLocation rngResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("rng").getAsJsonObject(),"item","minecraft:air"),':');
            int rngAmount = GsonHelper.getAsInt(json.get("rng").getAsJsonObject(),"count",0);
            float rngChance = GsonHelper.getAsFloat(json.get("rng").getAsJsonObject(),"chance",0); //Enter % as DECIMAL. Ie 50% = 0.5

            recipe.rngResult = new ItemStack(ForgeRegistries.ITEMS.getValue(rngResourceLocation));
            recipe.outputRngAmount = rngAmount;
            recipe.chance = rngChance;

            return recipe;
        }

        @Nullable
        @Override
        public CrusherRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
            CrusherRecipe recipe = new CrusherRecipe((recipeId));
            recipe.ingredientCount = buffer.readByte();
            recipe.result = buffer.readItem();
            recipe.processTime = buffer.readInt();
            recipe.outputAmount = buffer.readInt();
            recipe.rngResult = buffer.readItem();
            recipe.outputRngAmount = buffer.readInt();
            recipe.chance = buffer.readFloat();

            Ingredient tempIngredient = Ingredient.fromNetwork(buffer);
            recipe.ingredient = Lazy.of(() -> tempIngredient);

            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CrusherRecipe recipe){
            buffer.writeByte(recipe.getIngredientCount());
            buffer.writeItem(recipe.getResult());
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.outputAmount);
            buffer.writeItem(recipe.rngResult);
            buffer.writeInt(recipe.outputRngAmount);
            buffer.writeFloat(recipe.chance);

            recipe.ingredient.get().toNetwork(buffer);

        }
    }

}
