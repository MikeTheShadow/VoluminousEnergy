package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.items.tools.multitool.Multitool;
import com.veteam.voluminousenergy.items.tools.multitool.bits.BitItem;
import com.veteam.voluminousenergy.util.recipe.IngredientSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipeCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToolingRecipe extends VERecipe {
    public static final RecipeType<ToolingRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.TOOLING.get();

    public ToolingRecipe() {

    }

    public ToolingRecipe(List<VERecipeCodecs.RegistryIngredient> i, List<ItemStack> oi) {
        super(i, oi, 0);

        this.bits = Lazy.of(() -> {
            ArrayList<Item> foundBits = new ArrayList<>();

            for (var rawData : i) {
                Ingredient ingredient = rawData.getIngredient();
                for (Item input : Arrays.stream(ingredient.getItems()).map(ItemStack::getItem).toList()) {
                    if (input instanceof BitItem bitItem) {
                        foundBits.add(bitItem);
                    }
                }
            }

            return foundBits;
        });

        this.bases = Lazy.of(() -> {
            ArrayList<Item> foundBases = new ArrayList<>();

            for (var rawData : i) {
                Ingredient ingredient = rawData.getIngredient();
                for (Item input : Arrays.stream(ingredient.getItems()).map(ItemStack::getItem).toList()) {
                    if (input instanceof Multitool multitoolItem && multitoolItem.getBit() == null) {
                        foundBases.add(multitoolItem);
                    }
                }
            }

            return foundBases;
        });
    }

    // TODO fix me and make me right!
    public static final RecipeSerializer<ToolingRecipe> SERIALIZER = new RecipeSerializer<>() {

        public static final Codec<ToolingRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                VERecipeCodecs.VE_LAZY_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.registryIngredients),
                CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.results)
        ).apply(instance, ToolingRecipe::new));

        private static final IngredientSerializerHelper<ToolingRecipe> helper = new IngredientSerializerHelper<>();

        @Nullable
        @Override
        public ToolingRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
            return helper.fromNetwork(new ToolingRecipe(), buffer);
        }

        @Override
        public @NotNull Codec<ToolingRecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull ToolingRecipe recipe) {
            helper.toNetwork(buffer, recipe);
        }
    };

    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer(){ return SERIALIZER;}

    protected Lazy<ArrayList<Item>> bits;
    protected ArrayList<Item> basesAndBits;
    protected Lazy<ArrayList<Item>> bases;

    protected boolean usesTagKey;
    protected String tagKeyString;

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
        if (this.basesAndBits == null || this.basesAndBits.isEmpty()) {
            this.basesAndBits = new ArrayList<>();
            this.basesAndBits.addAll(bits.get());
            this.basesAndBits.addAll(bases.get());
        }
        return this.basesAndBits;
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
