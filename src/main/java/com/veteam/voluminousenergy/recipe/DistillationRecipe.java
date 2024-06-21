package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.parser.RNGBasicParser;
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

import javax.annotation.Nullable;
import java.util.List;

public class DistillationRecipe extends VERNGRecipe {
    public static final RecipeType<VERecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.DISTILLING.get();

    private final BasicParser parser = new RNGBasicParser(this)
            .addChancedItemResult(6, 0)
            .addFluidIngredient(0, 0)
            .addFluidResult(1, 0)
            .addFluidResult(2, 1);

    public DistillationRecipe() {
    }

    public DistillationRecipe(List<VERecipeCodecs.RegistryFluidIngredient> fi, List<FluidStack> of, List<VERecipeCodecs.VEChancedItemWithCount> oi, int processTime) {
        super(List.of(), fi, of, oi, processTime);
    }

    public static final RecipeSerializer<DistillationRecipe> SERIALIZER = new RecipeSerializer<>() {

        public static final MapCodec<DistillationRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                VERecipeCodecs.VE_FLUID_INGREDIENT_CODEC.listOf().fieldOf("fluid_ingredients").forGetter((getter) -> getter.registryFluidIngredients),
                VERecipeCodecs.VE_OUTPUT_FLUID_CODEC.listOf().fieldOf("fluid_results").forGetter((getter) -> getter.fluidOutputList),
                VERecipeCodecs.VE_CHANCED_OUTPUT_ITEM_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.itemResultsWithChance),
                Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime)
        ).apply(instance, DistillationRecipe::new));

        private static final FluidSerializerHelper<DistillationRecipe> helper = new FluidSerializerHelper<>();

        @Override
        public @NotNull MapCodec<DistillationRecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        @NotNull
        public StreamCodec<RegistryFriendlyByteBuf, DistillationRecipe> streamCodec() {
            return new StreamCodec<>() {
                @Override
                public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull DistillationRecipe recipe) {
                    helper.toNetwork(buf, recipe);
                }

                @Override
                @NotNull
                public DistillationRecipe decode(@NotNull RegistryFriendlyByteBuf buffer) {
                    return helper.fromNetwork(new DistillationRecipe(), buffer);
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
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.DISTILLATION_UNIT.block().get());
    }

    @Override
    public @NotNull RecipeType<VERecipe> getType() {
        return RECIPE_TYPE;
    }
}
