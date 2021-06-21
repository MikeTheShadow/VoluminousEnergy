package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
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

public class IndustrialBlastingRecipe extends VERecipe {

    public ArrayList<Item> ingredientList = new ArrayList<>();
    public final ResourceLocation recipeId;
    private int processTime;
    private int minimumHeat;
    private int secondInputAmount;
    private int outputAmount;
    private ItemStack secondInputStack;

    public static final IRecipeType<IndustrialBlastingRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.INDUSTRIAL_BLASTING;

    public static final Serializer SERIALIZER = new Serializer();

    public IndustrialBlastingRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    //public Ingredient getIngredient(){ return ingredient;}

    //public int getIngredientCount(){ return ingredientCount;}

    @Override
    public boolean matches(IInventory inv, World worldIn){
        ItemStack stack = inv.getItem(0);
        int count = stack.getCount();
        return ingredient.test(stack) && count >= ingredientCount;
    }

    @Override
    public ItemStack assemble(IInventory inv){return ItemStack.EMPTY;}

    @Override
    public boolean canCraftInDimensions(int width, int height){return true;}

    @Override
    public ItemStack getResultItem(){return result;}

    @Override
    public ResourceLocation getId(){return recipeId;}

    @Override
    public IRecipeSerializer<?> getSerializer(){ return SERIALIZER;}

    @Override
    public IRecipeType<?> getType(){return RECIPE_TYPE;}

    public int getProcessTime(){ return processTime; }

    public int getMinimumHeat(){ return minimumHeat; }

    public int getSecondInputAmount() {return secondInputAmount;}

    public int getOutputAmount() {return outputAmount;}

    public ItemStack getSecondInputStack() { return secondInputStack.copy(); }

    public ArrayList<Item> getFirstInputAsList() { return ingredientList; }

    public ItemStack getResult(){
        return result;
    }

    @Override
    public ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.BLAST_FURNACE_BLOCK);
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<IndustrialBlastingRecipe>{

        @Override
        public IndustrialBlastingRecipe fromJson(ResourceLocation recipeId, JsonObject json){
            IndustrialBlastingRecipe recipe = new IndustrialBlastingRecipe(recipeId);

            recipe.ingredient = Ingredient.fromJson(json.get("ingredient"));
            recipe.ingredientCount = JSONUtils.getAsInt(json.get("ingredient").getAsJsonObject(), "count", 1);
            recipe.processTime = JSONUtils.getAsInt(json,"process_time",200);

            for (ItemStack stack : recipe.ingredient.getItems()){
                if(!recipe.ingredientList.contains(stack.getItem())){
                    recipe.ingredientList.add(stack.getItem());
                }
            }

            // Second Input
            ResourceLocation secondInputResourceLocation = ResourceLocation.of(JSONUtils.getAsString(json.get("second_input").getAsJsonObject(),"item","minecraft:air"),':');
            int secondInputAmount = JSONUtils.getAsInt(json.get("second_input").getAsJsonObject(),"count",1);
            recipe.secondInputStack = new ItemStack(ForgeRegistries.ITEMS.getValue(secondInputResourceLocation));
            recipe.secondInputAmount = secondInputAmount;

            // Main Output Slot
            ResourceLocation itemResourceLocation = ResourceLocation.of(JSONUtils.getAsString(json.get("result").getAsJsonObject(),"item","minecraft:air"),':');
            recipe.outputAmount = JSONUtils.getAsInt(json.get("result").getAsJsonObject(),"count",1);
            recipe.result = new ItemStack(ForgeRegistries.ITEMS.getValue(itemResourceLocation));

            // Minimum heat
            recipe.minimumHeat = JSONUtils.getAsInt(json,"minimum_heat_kelvin",300); // Minimum heat in Kelvin (K), default fluid temperature is 300 K

            return recipe;
        }

        @Nullable
        @Override
        public IndustrialBlastingRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer){
            IndustrialBlastingRecipe recipe = new IndustrialBlastingRecipe(recipeId);
            recipe.ingredient = Ingredient.fromNetwork(buffer);

            int ingredientListSize = buffer.readInt();
            for (int i = 0; i < ingredientListSize; i++){
                recipe.ingredientList.add(buffer.readItem().getItem());
            }

            recipe.ingredientCount = buffer.readInt();
            recipe.secondInputStack = buffer.readItem();
            recipe.secondInputAmount = buffer.readInt();

            recipe.result = buffer.readItem();
            recipe.processTime = buffer.readInt();
            recipe.outputAmount = buffer.readInt();
            recipe.minimumHeat = buffer.readInt();
            return recipe;
        }

        @Override
        public void toNetwork(PacketBuffer buffer, IndustrialBlastingRecipe recipe){
            recipe.ingredient.toNetwork(buffer);

            buffer.writeInt(recipe.ingredientList.size());
            recipe.ingredientList.forEach(item -> {
                buffer.writeItem(new ItemStack(item));
            });

            buffer.writeInt(recipe.getIngredientCount());

            buffer.writeItem(recipe.secondInputStack);
            buffer.writeInt(recipe.secondInputAmount);
            buffer.writeItem(recipe.getResult());
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.outputAmount);
            buffer.writeInt(recipe.minimumHeat);
        }
    }
}
