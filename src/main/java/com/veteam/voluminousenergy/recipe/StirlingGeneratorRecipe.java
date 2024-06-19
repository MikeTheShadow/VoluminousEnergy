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
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class StirlingGeneratorRecipe extends VEEnergyRecipe {

    public static final RecipeType<VERecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.STIRLING.get();

    private final BasicParser parser = new BasicParser(this)
            .addIngredient(0, 0);

    public StirlingGeneratorRecipe() {
    }

    public StirlingGeneratorRecipe(List<VERecipeCodecs.RegistryIngredient> ingredients, int processTime, int energy_per_tick) {
        super(ingredients, processTime, energy_per_tick);
    }

    public static final RecipeSerializer<StirlingGeneratorRecipe> SERIALIZER = new RecipeSerializer<>() {

        public static final Codec<StirlingGeneratorRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                VERecipeCodecs.VE_LAZY_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.registryIngredients),
                Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime),
                Codec.INT.fieldOf("energy_per_tick").forGetter((getter) -> getter.processTime)
        ).apply(instance, StirlingGeneratorRecipe::new));

        private static final IngredientSerializerHelper<StirlingGeneratorRecipe> helper = new IngredientSerializerHelper<>();

        @Override
        public @NotNull MapCodec<StirlingGeneratorRecipe> codec() {
            return MapCodec.assumeMapUnsafe(VE_RECIPE_CODEC);
        }

        @Override
        @NotNull
        public StreamCodec<RegistryFriendlyByteBuf, StirlingGeneratorRecipe> streamCodec() {
            return new StreamCodec<>() {
                @Override
                public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull StirlingGeneratorRecipe recipe) {
                    buf.writeInt(recipe.getEnergyPerTick());
                    helper.toNetwork(buf, recipe);
                }

                @Override
                @NotNull
                public StirlingGeneratorRecipe decode(@NotNull RegistryFriendlyByteBuf buffer) {
                    StirlingGeneratorRecipe recipe = new StirlingGeneratorRecipe();
                    recipe.setEnergyPerTick(buffer.readInt());
                    return helper.fromNetwork(recipe, buffer);
                }
            };
        }
    };

    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.STIRLING_GENERATOR.block().get());
    }

    @Override
    public BasicParser getParser() {
        return parser;
    }
}