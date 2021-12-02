package com.veteam.voluminousenergy.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.RecipeUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class IndustrialBlastingRecipe extends VERecipe {

    public ArrayList<Item> ingredientList = new ArrayList<>();
    public ArrayList<Item> ingredientListIncludingSeconds = new ArrayList<>();
    public ArrayList<Item> onlySecondInput = new ArrayList<>();
    public final ResourceLocation recipeId;
    private int processTime;
    private int minimumHeat;
    private int secondInputAmount;
    private int outputAmount;
    private ItemStack secondInputStack;

    public static final RecipeType<IndustrialBlastingRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.INDUSTRIAL_BLASTING;

    public static final Serializer SERIALIZER = new Serializer();

    public IndustrialBlastingRecipe(ResourceLocation recipeId){
        this.recipeId = recipeId;
    }

    private final Map<Ingredient, Integer> ingredients = new LinkedHashMap<>();

    public Map<Ingredient, Integer> getIngredientMap() {
        return ImmutableMap.copyOf(ingredients);
    }

    @Override
    public boolean matches(Container inv, Level worldIn){
        ItemStack stack = inv.getItem(0);
        int count = stack.getCount();
        return ingredient.test(stack) && count >= ingredientCount;
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

    public int getProcessTime(){ return processTime; }

    public int getMinimumHeat(){ return minimumHeat; }

    public int getSecondInputAmount() {return secondInputAmount;}

    public int getOutputAmount() {return outputAmount;}

    public ArrayList<Item> getFirstInputAsList() { return ingredientList; }

    public ItemStack getResult(){
        return result;
    }

    @Override
    public ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.BLAST_FURNACE_BLOCK);
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<IndustrialBlastingRecipe>{

        @Override
        public IndustrialBlastingRecipe fromJson(ResourceLocation recipeId, JsonObject json){
            IndustrialBlastingRecipe recipe = new IndustrialBlastingRecipe(recipeId);

            recipe.ingredient = Ingredient.fromJson(json.get("ingredient"));
            recipe.ingredientCount = GsonHelper.getAsInt(json.get("ingredient").getAsJsonObject(), "count", 1);
            recipe.processTime = GsonHelper.getAsInt(json,"process_time",200);

            for (ItemStack stack : recipe.ingredient.getItems()){
                if(!recipe.ingredientList.contains(stack.getItem())){
                    recipe.ingredientList.add(stack.getItem());
                }
                if(!recipe.ingredientListIncludingSeconds.contains(stack.getItem())){
                    recipe.ingredientListIncludingSeconds.add(stack.getItem());
                }
            }

            JsonObject secondInput = json.get("second_input").getAsJsonObject();

            if(secondInput.has("tag") && !secondInput.has("item")){
                ResourceLocation secondInputResourceLocation = ResourceLocation.of(GsonHelper.getAsString(secondInput,"tag","minecraft:air"),':');
                int secondInputAmount = GsonHelper.getAsInt(secondInput,"count",1);
                recipe.secondInputAmount = secondInputAmount;

                Tag<Item> tag = RecipeUtil.getTagFromResourceLocationForItems(secondInputResourceLocation, "Industrial Blasting");

                if(tag != null){
                    recipe.ingredientListIncludingSeconds.addAll(tag.getValues());
                    recipe.onlySecondInput.addAll(tag.getValues());
                } else {
                    VoluminousEnergy.LOGGER.debug("Tag is null!");
                    throw new JsonSyntaxException("Bad syntax for the Industrial Blasting Recipe the tag is null");
                }
            } else if(!secondInput.has("tag") && secondInput.has("item")){
                ResourceLocation secondInputResourceLocation = ResourceLocation.of(GsonHelper.getAsString(secondInput,"item","minecraft:air"),':');
                int secondInputAmount = GsonHelper.getAsInt(secondInput,"count",1);
                recipe.secondInputStack = new ItemStack(ForgeRegistries.ITEMS.getValue(secondInputResourceLocation));
                recipe.secondInputAmount = secondInputAmount;

                if(!recipe.ingredientListIncludingSeconds.contains(recipe.secondInputStack.getItem()))
                    recipe.ingredientListIncludingSeconds.add(recipe.secondInputStack.getItem());
                if(!recipe.onlySecondInput.contains(recipe.secondInputStack.getItem()))
                    recipe.onlySecondInput.add(recipe.secondInputStack.getItem());
            } else {
                throw new JsonSyntaxException("Bad syntax for the Industrial Blasting Recipe");
            }

            // Main Output Slot
            ResourceLocation itemResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("result").getAsJsonObject(),"item","minecraft:air"),':');
            recipe.outputAmount = GsonHelper.getAsInt(json.get("result").getAsJsonObject(),"count",1);
            recipe.result = new ItemStack(ForgeRegistries.ITEMS.getValue(itemResourceLocation));

            // Minimum heat
            recipe.minimumHeat = GsonHelper.getAsInt(json,"minimum_heat_kelvin",300); // Minimum heat in Kelvin (K), default fluid temperature is 300 K

            return recipe;
        }

        @Nullable
        @Override
        public IndustrialBlastingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
            IndustrialBlastingRecipe recipe = new IndustrialBlastingRecipe(recipeId);
            recipe.ingredient = Ingredient.fromNetwork(buffer);

            int ingredientListSize = buffer.readInt();
            for (int i = 0; i < ingredientListSize; i++){
                recipe.ingredientList.add(buffer.readItem().getItem());
            }

            int secondListSize = buffer.readInt();
            for (int i = 0; i < secondListSize; i++){
                recipe.onlySecondInput.add(buffer.readItem().getItem());
            }

            recipe.secondInputAmount = buffer.readInt();

            recipe.result = buffer.readItem();
            recipe.processTime = buffer.readInt();
            recipe.outputAmount = buffer.readInt();
            recipe.minimumHeat = buffer.readInt();
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, IndustrialBlastingRecipe recipe){
            recipe.ingredient.toNetwork(buffer);

            buffer.writeInt(recipe.ingredientList.size());
            recipe.ingredientList.forEach(item -> {
                buffer.writeItem(new ItemStack(item));
            });

            buffer.writeInt(recipe.onlySecondInput.size());
            recipe.onlySecondInput.forEach(item -> {
                buffer.writeItem(new ItemStack(item));
            });

            buffer.writeInt(recipe.secondInputAmount);
            buffer.writeItem(recipe.getResult());
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.outputAmount);
            buffer.writeInt(recipe.minimumHeat);
        }
    }
}
