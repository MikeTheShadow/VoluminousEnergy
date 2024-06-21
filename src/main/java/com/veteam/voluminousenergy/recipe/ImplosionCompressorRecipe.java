package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.parser.BasicParser;
import com.veteam.voluminousenergy.recipe.parser.ImplosionCompressorParser;
import com.veteam.voluminousenergy.recipe.serializer.IngredientSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ImplosionCompressorRecipe extends VERecipe {
    public static final RecipeType<VERecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.IMPLOSION_COMPRESSING.get();

    private final BasicParser parser = new ImplosionCompressorParser(this)
            .addIngredient(0, 0)
            .addItemResult(2, 0);

    public ImplosionCompressorRecipe() {
    }

    public ImplosionCompressorRecipe(List<VERecipeCodecs.RegistryIngredient> ingredients, List<ItemStack> results, int processTime) {
        super(ingredients, new ArrayList<>(), new ArrayList<>(), results, processTime);
    }

    public static final RecipeSerializer<ImplosionCompressorRecipe> SERIALIZER = new RecipeSerializer<>() {

        public static final MapCodec<ImplosionCompressorRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                VERecipeCodecs.VE_LAZY_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.registryIngredients),
                VERecipeCodecs.VE_OUTPUT_ITEM_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.results),
                Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime)
        ).apply(instance, ImplosionCompressorRecipe::new));

        private static final IngredientSerializerHelper<ImplosionCompressorRecipe> helper = new IngredientSerializerHelper<>();

        @Override
        public @NotNull MapCodec<ImplosionCompressorRecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        @NotNull
        public StreamCodec<RegistryFriendlyByteBuf, ImplosionCompressorRecipe> streamCodec() {
            return new StreamCodec<>() {
                @Override
                public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull ImplosionCompressorRecipe recipe) {
                    helper.toNetwork(buf, recipe);
                }

                @Override
                @NotNull
                public ImplosionCompressorRecipe decode(@NotNull RegistryFriendlyByteBuf buffer) {
                    return helper.fromNetwork(new ImplosionCompressorRecipe(), buffer);
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

    @Override
    public @NotNull RecipeType<?> getType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.COMPRESSOR.block().get());
    }
}
