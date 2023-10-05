package com.veteam.voluminousenergy.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.TagUtil;
import com.veteam.voluminousenergy.util.recipe.IngredientSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.RecipeUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ToolingRecipe extends VERecipe {
    public static final RecipeType<ToolingRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.TOOLING.get();

//    public static final Serializer SERIALIZER = new Serializer();

    protected Lazy<ArrayList<Item>> bits;
    protected Lazy<ArrayList<Item>> basesAndBits;
    protected Lazy<ArrayList<Item>> bases;

    protected boolean usesTagKey;
    protected String tagKeyString;

    private final Map<Ingredient, Integer> ingredients = new LinkedHashMap<>();

    @Override
    public @NotNull RecipeType<?> getType(){
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.TOOLING_STATION_BLOCK.get());
    }

    public ArrayList<Item> getBits(){
        return this.bits.get();
    }

    public ArrayList<Item> getBasesAndBits(){
        return this.basesAndBits.get();
    }

    public ArrayList<Item> getBases(){
        return this.bases.get();
    }

//    public static class Serializer implements RecipeSerializer<ToolingRecipe>{
//
//        @Override
//        public @NotNull ToolingRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json){
//
//            ToolingRecipe recipe = new ToolingRecipe(recipeId);
//
//            Lazy<Ingredient> ingredientLazy = Lazy.of(() -> Ingredient.fromJson(json.get("ingredient"))); // bits
//
//            recipe.bits = Lazy.of(() -> {
//                ArrayList<Item> items = new ArrayList<>();
//                for (ItemStack item : ingredientLazy.get().getItems()) {
//                    items.add(item.getItem());
//                }
//                return items;
//            });
//
//            recipe.addLazyIngredient(ingredientLazy);
//
//            JsonObject toolBase = json.get("tool_base").getAsJsonObject();
//
//            if(toolBase.has("tag") && !toolBase.has("item")){
//                recipe.usesTagKey = true;
//                ResourceLocation toolBaseResourceLocation = ResourceLocation.of(GsonHelper.getAsString(toolBase,"tag","minecraft:air"),':');
//                recipe.tagKeyString = toolBaseResourceLocation.toString();
//
//                recipe.bases = TagUtil.getLazyItems(toolBaseResourceLocation);
//            } else if(!toolBase.has("tag") && toolBase.has("item")){
//                recipe.usesTagKey = false;
//                ResourceLocation secondInputResourceLocation = ResourceLocation.of(GsonHelper.getAsString(toolBase,"item","minecraft:air"),':');
//
//                recipe.bases = Lazy.of(() -> {
//                    ArrayList<Item> items = new ArrayList<>();
//                    items.add((new ItemStack(ForgeRegistries.ITEMS.getValue(secondInputResourceLocation))).getItem());
//                    return items;
//                });
//            } else {
//                throw new JsonSyntaxException("Bad syntax for the Tooling Recipe");
//            }
//
//            // Create Anthology
//            recipe.basesAndBits = RecipeUtil.createLazyAnthology(recipe.bases, recipe.bits);
//
//            ResourceLocation itemResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("result").getAsJsonObject(), "item", "minecraft:air"),':');
//            recipe.addResult(new ItemStack(ForgeRegistries.ITEMS.getValue(itemResourceLocation)));
//
//            return recipe;
//        }
//
//        IngredientSerializerHelper<ToolingRecipe> helper= new IngredientSerializerHelper<>();
//
//        @Nullable
//        @Override
//        public ToolingRecipe fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buffer){
//            ToolingRecipe recipe = new ToolingRecipe(recipeId);
//
//            // Start with usesTagKey check
//            recipe.usesTagKey = buffer.readBoolean();
//
//            if (recipe.usesTagKey){
//                ResourceLocation itemTagLocation = buffer.readResourceLocation();
//                recipe.bases = TagUtil.getLazyItems(itemTagLocation);
//            } else {
//                int basesSize = buffer.readInt();
//                ArrayList<Item> tempBases = new ArrayList<>();
//                for (int i = 0; i < basesSize; i++){
//                    tempBases.add(buffer.readItem().getItem());
//                }
//                recipe.bases = Lazy.of(() -> tempBases);
//            }
//
//            int bitsSize = buffer.readInt();
//            ArrayList<Item> bitList = new ArrayList<>();
//            for (int i = 0; i < bitsSize; i++){
//                bitList.add(buffer.readItem().getItem());
//            }
//            recipe.bits = Lazy.of(() -> bitList);
//            recipe.basesAndBits = RecipeUtil.createLazyAnthology(recipe.bases, recipe.bits);
//            helper.fromNetwork(recipe,buffer);
//            return recipe;
//        }
//
//        @Override
//        public void toNetwork(FriendlyByteBuf buffer, ToolingRecipe recipe){
//
//            buffer.writeBoolean(recipe.usesTagKey);
//            if (recipe.usesTagKey){
//                buffer.writeResourceLocation(new ResourceLocation(recipe.tagKeyString));
//            } else { // does not use tags for item input
//                buffer.writeInt(recipe.bases.get().size());
//                recipe.bases.get().forEach(item -> buffer.writeItem(new ItemStack(item)));
//            }
//            buffer.writeInt(recipe.bits.get().size());
//            recipe.bits.get().forEach(item -> buffer.writeItem(new ItemStack(item)));
//            helper.toNetwork(buffer,recipe);
//        }
//    }
}
