package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.items.tools.multitool.Multitool;
import com.veteam.voluminousenergy.items.tools.multitool.bits.BitItem;
import com.veteam.voluminousenergy.recipe.parser.BasicParser;
import com.veteam.voluminousenergy.recipe.serializer.IngredientSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToolingRecipe extends VERecipe {
    public static final RecipeType<VERecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.TOOLING.get();

    private final BasicParser parser = new BasicParser(this);

    public ToolingRecipe() {

    }

    public ToolingRecipe(List<VERecipeCodecs.RegistryIngredient> i, List<ItemStack> oi) {
        super(i, new ArrayList<>(), new ArrayList<>(), oi, 0);

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

    public static final RecipeSerializer<ToolingRecipe> SERIALIZER = new RecipeSerializer<>() {

        public static final MapCodec<ToolingRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                VERecipeCodecs.VE_LAZY_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.registryIngredients),
                VERecipeCodecs.VE_OUTPUT_ITEM_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.results)
        ).apply(instance, ToolingRecipe::new));

        private static final IngredientSerializerHelper<ToolingRecipe> helper = new IngredientSerializerHelper<>();

        @Override
        public @NotNull MapCodec<ToolingRecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        @NotNull
        public StreamCodec<RegistryFriendlyByteBuf, ToolingRecipe> streamCodec() {
            return new StreamCodec<>() {
                @Override
                public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull ToolingRecipe recipe) {
                    ArrayList<Item> bitItems = recipe.bits.get();
                    ArrayList<Item> baseItems = recipe.bases.get();

                    // Write bits to network
                    buf.writeInt(bitItems.size());
                    for (Item bitItem : bitItems) {
                        ItemStack.STREAM_CODEC.encode(buf,new ItemStack(bitItem,1));
                    }

                    // Write bases to network
                    buf.writeInt(baseItems.size());
                    for (Item baseItem : baseItems) {
                        ItemStack.STREAM_CODEC.encode(buf,new ItemStack(baseItem,1));
                    }

                    helper.toNetwork(buf, recipe);
                }

                @Override
                @NotNull
                public ToolingRecipe decode(@NotNull RegistryFriendlyByteBuf buffer) {
                    ArrayList<Item> bitItems = new ArrayList<>();
                    ArrayList<Item> baseItems = new ArrayList<>();

                    int bitsToRead = buffer.readInt();
                    for (int i = 0; i < bitsToRead; i++) {
                        bitItems.add(ItemStack.STREAM_CODEC.decode(buffer).getItem());
                    }

                    int basesToRead = buffer.readInt();
                    for (int i = 0; i < basesToRead; i++) {
                        baseItems.add(ItemStack.STREAM_CODEC.decode(buffer).getItem());
                    }

                    ToolingRecipe toolingRecipe = new ToolingRecipe();
                    toolingRecipe.bits = Lazy.of(() -> bitItems);
                    toolingRecipe.bases = Lazy.of(() -> baseItems);

                    return helper.fromNetwork(toolingRecipe, buffer);
                }
            };
        }

    };

    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public BasicParser getParser() {
        return parser;
    }

    protected Lazy<ArrayList<Item>> bits;
    protected ArrayList<Item> basesAndBits;
    protected Lazy<ArrayList<Item>> bases;

    protected boolean usesTagKey;
    protected String tagKeyString;

    @Override
    public @NotNull RecipeType<?> getType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.TOOLING_STATION.block().get());
    }

    public ArrayList<Item> getBits() {
        return this.bits.get();
    }

    public ArrayList<Item> getBasesAndBits() {
        if (this.basesAndBits == null || this.basesAndBits.isEmpty()) {
            this.basesAndBits = new ArrayList<>();
            this.basesAndBits.addAll(bits.get());
            this.basesAndBits.addAll(bases.get());
        }
        return this.basesAndBits;
    }

    public ArrayList<Item> getBases() {
        return this.bases.get();
    }

}
