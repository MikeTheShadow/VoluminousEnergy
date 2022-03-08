package com.veteam.voluminousenergy.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
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
import java.util.Optional;

public class ToolingRecipe extends VERecipe {
    public static final RecipeType<ToolingRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.TOOLING;

    public static final Serializer SERIALIZER = new Serializer();

    protected ArrayList<Item> bits = new ArrayList<>();
    protected ArrayList<Item> basesAndBits = new ArrayList<>();
    protected ArrayList<Item> bases = new ArrayList<>();

    public final ResourceLocation recipeId;
    public Ingredient ingredient;
    public ItemStack result;
    private ItemStack base;

    private final Map<Ingredient, Integer> ingredients = new LinkedHashMap<>();

    public Map<Ingredient, Integer> getIngredientMap() {
        return ImmutableMap.copyOf(ingredients);
    }

    public ToolingRecipe(ResourceLocation recipeId){ this.recipeId = recipeId; }

    @Override
    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getIngredientCount() {
        return ingredientCount;
    }

    @Override
    public ItemStack getResult() { return result; }

    @Override
    public boolean matches(Container inv, Level worldIn){
        ItemStack stack = inv.getItem(0);
        int count = stack.getCount();
        return ingredient.test(stack) && count >= ingredientCount;
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

    @Override
    public ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.TOOLING_STATION_BLOCK);
    }

    public ArrayList<Item> getBits(){
        return this.bits;
    }

    public ArrayList<Item> getBasesAndBits(){
        return this.basesAndBits;
    }

    public ArrayList<Item> getBases(){
        return this.bases;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<ToolingRecipe>{

        @Override
        public ToolingRecipe fromJson(ResourceLocation recipeId, JsonObject json){

            ToolingRecipe recipe = new ToolingRecipe(recipeId);

            recipe.ingredient = Ingredient.fromJson(json.get("ingredient"));

            for (ItemStack stack : recipe.ingredient.getItems()){
                if(!recipe.bits.contains(stack.getItem())){
                    recipe.bits.add(stack.getItem());
                }
                if(!recipe.basesAndBits.contains(stack.getItem())){
                    recipe.basesAndBits.add(stack.getItem());
                }
            }

            JsonObject toolBase = json.get("tool_base").getAsJsonObject();

            if(toolBase.has("tag") && !toolBase.has("item")){
                ResourceLocation toolBaseResourceLocation = ResourceLocation.of(GsonHelper.getAsString(toolBase,"tag","minecraft:air"),':');

                Optional<TagKey<Item>> tag = Optional.of(TagKey.create(Registry.ITEM_REGISTRY, toolBaseResourceLocation));

                if(tag != null){
                    for (Holder<Item> itemHolder : Registry.ITEM.getTagOrEmpty(tag.get())){// TODO: Forge use their own registry but this was not the case for tags in 18.1
                        recipe.basesAndBits.add(itemHolder.value());
                        recipe.bases.add(itemHolder.value());
                    }
                } else {
                    VoluminousEnergy.LOGGER.debug("Tag is null!");
                    throw new JsonSyntaxException("Bad syntax for the Tooling Recipe, the tag is null");
                }
            } else if(!toolBase.has("tag") && toolBase.has("item")){
                ResourceLocation secondInputResourceLocation = ResourceLocation.of(GsonHelper.getAsString(toolBase,"item","minecraft:air"),':');

                recipe.base = new ItemStack(ForgeRegistries.ITEMS.getValue(secondInputResourceLocation));

                if(!recipe.basesAndBits.contains(recipe.base.getItem()))
                    recipe.basesAndBits.add(recipe.base.getItem());
                if(!recipe.bases.contains(recipe.base.getItem()))
                    recipe.bases.add(recipe.base.getItem());
            } else {
                throw new JsonSyntaxException("Bad syntax for the Tooling Recipe");
            }

            ResourceLocation itemResourceLocation = ResourceLocation.of(GsonHelper.getAsString(json.get("result").getAsJsonObject(), "item", "minecraft:air"),':');
            recipe.result = new ItemStack(ForgeRegistries.ITEMS.getValue(itemResourceLocation));

            return recipe;
        }

        @Nullable
        @Override
        public ToolingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
            ToolingRecipe recipe = new ToolingRecipe(recipeId);
            recipe.ingredient = Ingredient.fromNetwork(buffer);

            int basesAndBitsSize = buffer.readInt();
            for (int i = 0; i < basesAndBitsSize; i++){
                recipe.basesAndBits.add(buffer.readItem().getItem());
            }

            int basesSize = buffer.readInt();
            for (int i = 0; i < basesSize; i++){
                recipe.bases.add(buffer.readItem().getItem());
            }

            int bitsSize = buffer.readInt();
            for (int i = 0; i < bitsSize; i++){
                recipe.bits.add(buffer.readItem().getItem());
            }

            recipe.result = buffer.readItem();
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ToolingRecipe recipe){
            recipe.ingredient.toNetwork(buffer);

            buffer.writeInt(recipe.basesAndBits.size());
            recipe.basesAndBits.forEach(item -> {
                buffer.writeItem(new ItemStack(item));
            });

            buffer.writeInt(recipe.bases.size());
            recipe.bases.forEach(item -> {
                buffer.writeItem(new ItemStack(item));
            });

            buffer.writeInt(recipe.bits.size());
            recipe.bits.forEach(item -> {
                buffer.writeItem(new ItemStack(item));
            });

            buffer.writeItem(recipe.getResult());
        }
    }
}
