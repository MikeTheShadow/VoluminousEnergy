package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.parser.RNGBasicParser;
import com.veteam.voluminousenergy.recipe.parser.BasicParser;
import com.veteam.voluminousenergy.recipe.serializer.IngredientSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CentrifugalSeparatorRecipe extends VERNGRecipe {

    public static final RecipeType<VERecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.CENTRIFUGAL_SEPARATION.get();

    private final BasicParser parser = new RNGBasicParser(this)
            .addChancedItemResult(2, 0)
            .addChancedItemResult(3, 1)
            .addChancedItemResult(4, 2)
            .addChancedItemResult(5, 3)
            .addIngredient(0, 0)
            .addIngredient(1, 1);


    public CentrifugalSeparatorRecipe() {
    }

    public CentrifugalSeparatorRecipe(List<VERecipeCodecs.RegistryIngredient> ingredients, List<VERecipeCodecs.VEChancedItemWithCount> results, int processTime) {
        super(ingredients, new ArrayList<>(), new ArrayList<>(), results, processTime);
    }

    public static final MapCodec<CentrifugalSeparatorRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            VERecipeCodecs.VE_LAZY_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.registryIngredients),
            VERecipeCodecs.VE_CHANCED_OUTPUT_ITEM_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.itemResultsWithChance),
            Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime)
    ).apply(instance, CentrifugalSeparatorRecipe::new));

    private static final IngredientSerializerHelper<CentrifugalSeparatorRecipe> helper = new IngredientSerializerHelper<>();

    public static final StreamCodec<RegistryFriendlyByteBuf, CentrifugalSeparatorRecipe> VE_RECIPE_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull CentrifugalSeparatorRecipe recipe) {
            helper.toNetwork(buf, recipe);
        }

        @Override
        @NotNull
        public CentrifugalSeparatorRecipe decode(@NotNull RegistryFriendlyByteBuf buffer) {
            return helper.fromNetwork(new CentrifugalSeparatorRecipe(), buffer);
        }
    };

    public static final RecipeSerializer<CentrifugalSeparatorRecipe> SERIALIZER = new RecipeSerializer<>(VE_RECIPE_CODEC, VE_RECIPE_STREAM_CODEC);



    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public BasicParser getParser() {
        return parser;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<RecipeInput>> getType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.CENTRIFUGAL_SEPARATOR.block().get());
    }
}
