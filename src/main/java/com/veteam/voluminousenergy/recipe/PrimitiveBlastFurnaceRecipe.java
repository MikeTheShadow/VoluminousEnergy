package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
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
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PrimitiveBlastFurnaceRecipe extends VERecipe {

    public static final RecipeType<VERecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.PRIMITIVE_BLAST_FURNACING.get();

    private final BasicParser parser = new BasicParser(this)
            .addIngredient(0, 0)
            .addItemResult(1, 0);

    public PrimitiveBlastFurnaceRecipe() {
    }

    public PrimitiveBlastFurnaceRecipe(List<VERecipeCodecs.RegistryIngredient> ingredients, List<net.minecraft.world.item.ItemStackTemplate> results, int processTime) {
        super(ingredients, new ArrayList<>(), new ArrayList<>(), results, processTime);
    }

    public static final MapCodec<PrimitiveBlastFurnaceRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            VERecipeCodecs.VE_LAZY_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.registryIngredients),
            VERecipeCodecs.VE_OUTPUT_ITEM_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.resultTemplates),
            Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime)
    ).apply(instance, PrimitiveBlastFurnaceRecipe::new));

    private static final IngredientSerializerHelper<PrimitiveBlastFurnaceRecipe> helper = new IngredientSerializerHelper<>();

    public static final StreamCodec<RegistryFriendlyByteBuf, PrimitiveBlastFurnaceRecipe> VE_RECIPE_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull PrimitiveBlastFurnaceRecipe recipe) {
            helper.toNetwork(buf, recipe);
        }

        @Override
        @NotNull
        public PrimitiveBlastFurnaceRecipe decode(@NotNull RegistryFriendlyByteBuf buffer) {
            return helper.fromNetwork(new PrimitiveBlastFurnaceRecipe(), buffer);
        }
    };

    public static final RecipeSerializer<PrimitiveBlastFurnaceRecipe> SERIALIZER = new RecipeSerializer<>(VE_RECIPE_CODEC, VE_RECIPE_STREAM_CODEC);

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
        return new ItemStack(VEBlocks.PRIMITIVE_BLAST_FURNACE.block().get());
    }

}
