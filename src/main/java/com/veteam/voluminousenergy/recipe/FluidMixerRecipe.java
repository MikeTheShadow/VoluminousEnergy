package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.parser.BasicParser;
import com.veteam.voluminousenergy.recipe.serializer.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FluidMixerRecipe extends VERecipe {
    public static final RecipeType<VERecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.FLUID_MIXING.get();

    private final BasicParser parser = new BasicParser(this)
            .addFluidIngredient(0, 0)
            .addFluidResult(1, 0)
            .addFluidResult(2, 1);

    public FluidMixerRecipe() {
    }

    public FluidMixerRecipe(List<VERecipeCodecs.RegistryFluidIngredient> fi, List<FluidStack> of, int processTime) {
        super(List.of(), fi, of, List.of(), processTime);
    }

    public static final RecipeSerializer<FluidMixerRecipe> SERIALIZER = new RecipeSerializer<>() {

        public static final Codec<FluidMixerRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                VERecipeCodecs.VE_FLUID_INGREDIENT_CODEC.listOf().fieldOf("fluid_ingredients").forGetter((getter) -> getter.registryFluidIngredients),
                VERecipeCodecs.VE_OUTPUT_FLUID_CODEC.listOf().fieldOf("fluid_results").forGetter((getter) -> getter.fluidOutputList),
                Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime)
        ).apply(instance, FluidMixerRecipe::new));

        private static final FluidSerializerHelper<FluidMixerRecipe> helper = new FluidSerializerHelper<>();

        @Override
        public @NotNull MapCodec<FluidMixerRecipe> codec() {
            return MapCodec.assumeMapUnsafe(VE_RECIPE_CODEC);
        }

        @Override
        @NotNull
        public StreamCodec<RegistryFriendlyByteBuf, FluidMixerRecipe> streamCodec() {
            return new StreamCodec<>() {
                @Override
                public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull FluidMixerRecipe recipe) {
                    helper.toNetwork(buf, recipe);
                }

                @Override
                @NotNull
                public FluidMixerRecipe decode(@NotNull RegistryFriendlyByteBuf buffer) {
                    return helper.fromNetwork(new FluidMixerRecipe(), buffer);
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
    public @NotNull RecipeType<VERecipe> getType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.FLUID_MIXER.block().get());
    }
}