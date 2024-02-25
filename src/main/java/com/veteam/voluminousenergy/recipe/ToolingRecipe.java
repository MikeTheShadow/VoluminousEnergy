package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.items.tools.multitool.Multitool;
import com.veteam.voluminousenergy.items.tools.multitool.bits.BitItem;
import com.veteam.voluminousenergy.recipe.serializer.IngredientSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
        super(i,new ArrayList<>(),new ArrayList<>(), oi, 0);

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
                ItemStack.ITEM_WITH_COUNT_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.results)
        ).apply(instance, ToolingRecipe::new));

        private static final IngredientSerializerHelper<ToolingRecipe> helper = new IngredientSerializerHelper<>();

        @Nullable
        @Override
        public ToolingRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {

            ArrayList<Item> bitItems = new ArrayList<>();
            ArrayList<Item> baseItems = new ArrayList<>();

            int bitsToRead = buffer.readInt();
            for (int i = 0; i < bitsToRead; i++) {
                bitItems.add(buffer.readItem().getItem());
            }

            int basesToRead = buffer.readInt();
            for (int i = 0; i < basesToRead; i++) {
                baseItems.add(buffer.readItem().getItem());
            }

            ToolingRecipe toolingRecipe = new ToolingRecipe();

            toolingRecipe.bits = Lazy.of(() -> bitItems);
            toolingRecipe.bases = Lazy.of(() -> baseItems);

            return helper.fromNetwork(toolingRecipe, buffer);
        }

        @Override
        public @NotNull Codec<ToolingRecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull ToolingRecipe recipe) {

            ArrayList<Item> bitItems = recipe.bits.get();
            ArrayList<Item> baseItems = recipe.bases.get();

            int bitLength = bitItems.size();
            int baseLength = baseItems.size();

            // Write bits to network
            buffer.writeInt(bitLength);

            for (Item bitItem : bitItems) {
                buffer.writeItem(new ItemStack(bitItem));
            }

            // Write bases to network
            buffer.writeInt(baseLength);

            for (Item baseItem : baseItems) {
                buffer.writeItem(new ItemStack(baseItem));
            }

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

}
