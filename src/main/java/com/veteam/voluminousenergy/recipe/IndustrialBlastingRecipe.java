package com.veteam.voluminousenergy.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.RecipeUtil;
import com.veteam.voluminousenergy.util.TagUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class IndustrialBlastingRecipe extends VERecipe {

    public Lazy<ArrayList<Item>> ingredientList;
    public Lazy<ArrayList<Item>> ingredientListIncludingSeconds;
    public Lazy<ArrayList<Item>> onlySecondInput;
    public final ResourceLocation recipeId;
    private int processTime;
    private int minimumHeat;
    private int secondInputAmount;
    private int outputAmount;

    protected boolean usesTagKey;
    protected String tagKeyString;

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

    public int getProcessTime(){ return processTime; }

    public int getMinimumHeat(){ return minimumHeat; }

    public int getSecondInputAmount() {return secondInputAmount;}

    public int getOutputAmount() {return outputAmount;}

    public ArrayList<Item> getFirstInputAsList() { return ingredientList.get(); }

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

            recipe.ingredient = Lazy.of(() -> Ingredient.fromJson(json.get("ingredient")));
            recipe.ingredientCount = GsonHelper.getAsInt(json.get("ingredient").getAsJsonObject(), "count", 1);
            recipe.processTime = GsonHelper.getAsInt(json,"process_time",200);

            recipe.ingredientList = Lazy.of(() -> {
                ArrayList<Item> temp = new ArrayList<>();
                for (ItemStack item : recipe.ingredient.get().getItems()) {
                    temp.add(item.getItem());
                }
                return temp;
            });

            JsonObject secondInput = json.get("second_input").getAsJsonObject();

            if(secondInput.has("tag") && !secondInput.has("item")){
                ResourceLocation secondInputResourceLocation = ResourceLocation.of(GsonHelper.getAsString(secondInput,"tag","minecraft:air"),':');
                int secondInputAmount = GsonHelper.getAsInt(secondInput,"count",1);
                recipe.secondInputAmount = secondInputAmount;

                recipe.usesTagKey = true;
                recipe.tagKeyString = secondInputResourceLocation.toString();

                recipe.onlySecondInput = TagUtil.getLazyItems(secondInputResourceLocation);
            } else if(!secondInput.has("tag") && secondInput.has("item")){
                recipe.usesTagKey = false;
                ResourceLocation secondInputResourceLocation = ResourceLocation.of(GsonHelper.getAsString(secondInput,"item","minecraft:air"),':');
                int secondInputAmount = GsonHelper.getAsInt(secondInput,"count",1);
                recipe.secondInputAmount = secondInputAmount;
                recipe.onlySecondInput = Lazy.of(() -> {
                    ArrayList<Item> items = new ArrayList<>();
                    items.add((new ItemStack(ForgeRegistries.ITEMS.getValue(secondInputResourceLocation))).getItem());
                    return items;
                });
            } else {
                throw new JsonSyntaxException("Bad syntax for the Industrial Blasting Recipe");
            }
            // Create Anthology of both inputs
            recipe.ingredientListIncludingSeconds = RecipeUtil.createLazyAnthology(recipe.ingredientList, recipe.onlySecondInput);

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

            // Start with usesTagKey check
            recipe.usesTagKey = buffer.readBoolean();

            if (recipe.usesTagKey){
                int sequenceLength = buffer.readInt();
                recipe.tagKeyString = buffer.readCharSequence(sequenceLength, StandardCharsets.UTF_8).toString();
                ResourceLocation itemTagLocation = new ResourceLocation(recipe.tagKeyString);
                recipe.onlySecondInput = TagUtil.getLazyItems(itemTagLocation);

                recipe.ingredientList = Lazy.of(() -> {
                    ArrayList<Item> temp = new ArrayList<>();
                    for (ItemStack item : recipe.ingredient.get().getItems()) {
                        temp.add(item.getItem());
                    }
                    return temp;
                });
            } else {
                int ingredientListSize = buffer.readInt();
                for (int i = 0; i < ingredientListSize; i++){
                    recipe.ingredientList.get().add(buffer.readItem().getItem());
                }

                int secondListSize = buffer.readInt();
                for (int i = 0; i < secondListSize; i++){
                    recipe.onlySecondInput.get().add(buffer.readItem().getItem());
                }
            }

            recipe.secondInputAmount = buffer.readInt();

            recipe.result = buffer.readItem();
            recipe.processTime = buffer.readInt();
            recipe.outputAmount = buffer.readInt();
            recipe.minimumHeat = buffer.readInt();

            Ingredient tempIngredient = Ingredient.fromNetwork(buffer);
            recipe.ingredient = Lazy.of(() -> tempIngredient);

            // Build Anthology
            recipe.ingredientListIncludingSeconds = RecipeUtil.createLazyAnthology(recipe.ingredientList, recipe.onlySecondInput);

            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, IndustrialBlastingRecipe recipe){

            if (recipe.usesTagKey){
                buffer.writeInt(recipe.tagKeyString.length());
                buffer.writeCharSequence(recipe.tagKeyString, StandardCharsets.UTF_8);
            } else { // does not use tags for item input
                buffer.writeInt(recipe.ingredientList.get().size());
                recipe.ingredientList.get().forEach(item -> {
                    buffer.writeItem(new ItemStack(item));
                });

                buffer.writeInt(recipe.onlySecondInput.get().size());
                recipe.onlySecondInput.get().forEach(item -> {
                    buffer.writeItem(new ItemStack(item));
                });
            }

            buffer.writeInt(recipe.secondInputAmount);
            buffer.writeItem(recipe.getResult());
            buffer.writeInt(recipe.processTime);
            buffer.writeInt(recipe.outputAmount);
            buffer.writeInt(recipe.minimumHeat);

            recipe.ingredient.get().toNetwork(buffer);

        }
    }
}
