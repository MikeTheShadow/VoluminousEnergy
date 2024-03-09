package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.parser.RecipeParser;
import com.veteam.voluminousenergy.recipe.serializer.IngredientSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ImplosionCompressorRecipe extends VERecipe {
    public static final RecipeType<VERecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.IMPLOSION_COMPRESSING.get();

    private final RecipeParser parser = RecipeParser.forRecipe(this)
            .addIngredient(0,0)
            .addIngredient(1,1)
            .addItemResult(2,0);

    public ImplosionCompressorRecipe() {
    }

    public ImplosionCompressorRecipe(List<VERecipeCodecs.RegistryIngredient> ingredients, List<ItemStack> results, int processTime) {
        super(ingredients,new ArrayList<>(),new ArrayList<>(), results, processTime);
        ingredients.add(new VERecipeCodecs.RegistryIngredient("","minecraft:gunpowder",1));
    }

    public static final RecipeSerializer<ImplosionCompressorRecipe> SERIALIZER = new RecipeSerializer<>() {

        public static final Codec<ImplosionCompressorRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                VERecipeCodecs.VE_LAZY_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.registryIngredients),
                VERecipeCodecs.VE_OUTPUT_ITEM_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.results),
                Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime)
        ).apply(instance, ImplosionCompressorRecipe::new));

        private static final IngredientSerializerHelper<ImplosionCompressorRecipe> helper = new IngredientSerializerHelper<>();

        @Nullable
        @Override
        public ImplosionCompressorRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
            return helper.fromNetwork(new ImplosionCompressorRecipe(), buffer);
        }

        @Override
        public @NotNull Codec<ImplosionCompressorRecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull ImplosionCompressorRecipe recipe) {
            helper.toNetwork(buffer, recipe);
        }
    };

    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer(){ return SERIALIZER;}

    @Override
    public RecipeParser getParser() {
        return parser;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.COMPRESSOR_BLOCK.get());
    }
}
