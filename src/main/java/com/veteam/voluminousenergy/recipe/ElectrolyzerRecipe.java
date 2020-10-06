package com.veteam.voluminousenergy.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ElectrolyzerRecipe extends VERecipe {

    public static final IRecipeType<ElectrolyzerRecipe> RECIPE_TYPE = IRecipeType.register("electrolyzing");
    public static final Serializer serializer = new Serializer();

    public final ResourceLocation recipeId;
    public Ingredient ingredient;
    public int ingredientCount;
    public ItemStack result;
    private ItemStack rngResult0;
    private ItemStack rngResult1;
    private ItemStack rngResult2;
    private int processTime;
    private int outputAmount;
    private int outputRngAmount0;
    private int outputRngAmount1;
    private int outputRngAmount2;
    private float chance0;
    private float chance1;
    private float chance2;
    private boolean usesBucket;

    private final Map<Ingredient, Integer> ingredients = new LinkedHashMap<>();

    public ElectrolyzerRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    public Ingredient getIngredient(){ return ingredient;}

    public int getIngredientCount(){ return ingredientCount;}

    public ItemStack getResult() {return result;}

    public ItemStack getRngItemSlot0(){return rngResult0;}

    public ItemStack getRngItemSlot1(){return rngResult1;}

    public ItemStack getRngItemSlot2(){return rngResult2;}

    public float getChance0(){return chance0;}

    public float getChance1(){return chance1;}

    public float getChance2(){return chance2;}

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
    public IRecipeSerializer<?> getSerializer(){ return serializer;}

    @Override
    public IRecipeType<?> getType(){return RECIPE_TYPE;}

    public int getOutputAmount() {return outputAmount;}

    public int getOutputRngAmount0(){return outputRngAmount0;}

    public int getOutputRngAmount1(){return outputRngAmount1;}

    public int getOutputRngAmount2(){return outputRngAmount2;}

    public int getProcessTime() { return processTime; }

    public boolean isUsesBucket() {return usesBucket;}

    public Map<Ingredient, Integer> getIngredientMap() {
        return ImmutableMap.copyOf(ingredients);
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ElectrolyzerRecipe>{

        public static ArrayList<Item> ingredientList = new ArrayList<>();

        @Override
        public ElectrolyzerRecipe read(ResourceLocation recipeId, JsonObject json){
            ElectrolyzerRecipe recipe = new ElectrolyzerRecipe(recipeId);

            recipe.ingredient = Ingredient.deserialize(json.get("ingredient"));
            recipe.ingredientCount = JSONUtils.getInt(json.get("ingredient").getAsJsonObject(), "count", 1);
            recipe.processTime = JSONUtils.getInt(json,"processTime",200);

            for (ItemStack stack : recipe.ingredient.getMatchingStacks()){
                if(!ingredientList.contains(stack.getItem())){
                    ingredientList.add(stack.getItem());
                }
            }

            // Main Output Slot
            ResourceLocation itemResourceLocation = ResourceLocation.create(JSONUtils.getString(json.get("result").getAsJsonObject(),"item","minecraft:air"),':');
            int itemAmount = JSONUtils.getInt(json.get("result").getAsJsonObject(),"count",1);
            boolean bucketNeeded = JSONUtils.getBoolean(json.get("result").getAsJsonObject(),"consumes_bucket",false);
            recipe.result = new ItemStack(ForgeRegistries.ITEMS.getValue(itemResourceLocation));
            recipe.outputAmount = itemAmount;
            recipe.usesBucket = bucketNeeded;

            // First RNG Slot, RNG 0
            ResourceLocation rngResourceLocation0 = ResourceLocation.create(JSONUtils.getString(json.get("rng_slot_0").getAsJsonObject(),"item","minecraft:air"),':');
            int rngAmount0 = JSONUtils.getInt(json.get("rng_slot_0").getAsJsonObject(),"count",0);
            float rngChance0 = JSONUtils.getFloat(json.get("rng_slot_0").getAsJsonObject(),"chance",0); //Enter % as DECIMAL. Ie 50% = 0.5

            recipe.rngResult0 = new ItemStack(ForgeRegistries.ITEMS.getValue(rngResourceLocation0));
            recipe.outputRngAmount0 = rngAmount0;
            recipe.chance0 = rngChance0;

            //Second RNG Slot, RNG 1
            ResourceLocation rngResourceLocation1 = ResourceLocation.create(JSONUtils.getString(json.get("rng_slot_1").getAsJsonObject(),"item","minecraft:air"),':');
            int rngAmount1 = JSONUtils.getInt(json.get("rng_slot_1").getAsJsonObject(),"count",0);
            float rngChance1 = JSONUtils.getFloat(json.get("rng_slot_1").getAsJsonObject(),"chance",0); //Enter % as DECIMAL. Ie 50% = 0.5

            recipe.rngResult1 = new ItemStack(ForgeRegistries.ITEMS.getValue(rngResourceLocation1));
            recipe.outputRngAmount1 = rngAmount1;
            recipe.chance1 = rngChance1;

            //Third RNG Slot, RNG 2
            ResourceLocation rngResourceLocation2 = ResourceLocation.create(JSONUtils.getString(json.get("rng_slot_2").getAsJsonObject(),"item","minecraft:air"),':');
            int rngAmount2 = JSONUtils.getInt(json.get("rng_slot_2").getAsJsonObject(),"count",0);
            float rngChance2 = JSONUtils.getFloat(json.get("rng_slot_2").getAsJsonObject(),"chance",0); //Enter % as DECIMAL. Ie 50% = 0.5

            recipe.rngResult2 = new ItemStack(ForgeRegistries.ITEMS.getValue(rngResourceLocation2));
            recipe.outputRngAmount2 = rngAmount2;
            recipe.chance2 = rngChance2;

            return recipe;
        }

        @Nullable
        @Override
        public ElectrolyzerRecipe read(ResourceLocation recipeId, PacketBuffer buffer){
            ElectrolyzerRecipe recipe = new ElectrolyzerRecipe((recipeId));
            recipe.ingredient = Ingredient.read(buffer);
            recipe.ingredientCount = buffer.readByte();
            recipe.result = buffer.readItemStack();
            recipe.processTime = buffer.readInt();
            recipe.outputAmount = buffer.readInt();
            recipe.usesBucket = buffer.readBoolean();
            //RNG 0
            recipe.rngResult0 = buffer.readItemStack();
            recipe.outputRngAmount0 = buffer.readInt();
            recipe.chance0 = buffer.readFloat();
            //RNG 1
            recipe.rngResult1 = buffer.readItemStack();
            recipe.outputRngAmount1 = buffer.readInt();
            recipe.chance1 = buffer.readFloat();
            //RNG 2
            recipe.rngResult2 = buffer.readItemStack();
            recipe.outputRngAmount2 = buffer.readInt();
            recipe.chance2 = buffer.readFloat();
            return recipe;
        }

        @Override
        public void write(PacketBuffer buffer, ElectrolyzerRecipe recipe){
            recipe.ingredient.write(buffer);
            buffer.writeByte(recipe.getIngredientCount());
            buffer.writeItemStack(recipe.getResult());
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.outputAmount);
            buffer.writeBoolean(recipe.usesBucket);
            //RNG 0
            buffer.writeItemStack(recipe.rngResult0);
            buffer.writeInt(recipe.outputRngAmount0);
            buffer.writeFloat(recipe.chance0);
            //RNG 1
            buffer.writeItemStack(recipe.rngResult1);
            buffer.writeInt(recipe.outputRngAmount1);
            buffer.writeFloat(recipe.chance1);
            //RNG 2
            buffer.writeItemStack(recipe.rngResult2);
            buffer.writeInt(recipe.outputRngAmount2);
            buffer.writeFloat(recipe.chance2);
        }
    }

}
