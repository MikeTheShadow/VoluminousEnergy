package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.parser.BasicParser;
import com.veteam.voluminousenergy.recipe.parser.HydroponicParser;
import com.veteam.voluminousenergy.recipe.serializer.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HydroponicIncubatorRecipe extends VERNGRecipe {
    public static final RecipeType<VERecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.HYDROPONIC_INCUBATING.get();

    private final BasicParser parser = new HydroponicParser(this)
        .addChancedItemResult(4, 1)
        .addChancedItemResult(5, 2)
        .addChancedItemResult(6, 3)
        .addItemResult(3, 0)
        .addFluidIngredient(0, 0)
        .addIngredient(2, 0);

    public HydroponicIncubatorRecipe() {
    }

    public HydroponicIncubatorRecipe(List<VERecipeCodecs.RegistryIngredient> i, List<VERecipeCodecs.RegistryFluidIngredient> fi, List<VERecipeCodecs.VEChancedItemWithCount> oi, int processTime) {
        super(i, fi, List.of(), oi, processTime);
    }

    public static final MapCodec<HydroponicIncubatorRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
        VERecipeCodecs.VE_LAZY_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.registryIngredients),
        VERecipeCodecs.VE_FLUID_INGREDIENT_CODEC.listOf().fieldOf("fluid_ingredients").forGetter((getter) -> getter.registryFluidIngredients),
        VERecipeCodecs.VE_CHANCED_OUTPUT_ITEM_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.itemResultsWithChance),
        Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime)
    ).apply(instance, HydroponicIncubatorRecipe::new));

    private static final FluidSerializerHelper<HydroponicIncubatorRecipe> helper = new FluidSerializerHelper<>();

    public static final StreamCodec<RegistryFriendlyByteBuf, HydroponicIncubatorRecipe> VE_RECIPE_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull HydroponicIncubatorRecipe recipe) {
            helper.toNetwork(buf, recipe);
        }

        @Override
        @NotNull
        public HydroponicIncubatorRecipe decode(@NotNull RegistryFriendlyByteBuf buffer) {
            return helper.fromNetwork(new HydroponicIncubatorRecipe(), buffer);
        }
    };

    public static final RecipeSerializer<HydroponicIncubatorRecipe> SERIALIZER = new RecipeSerializer<>(VE_RECIPE_CODEC, VE_RECIPE_STREAM_CODEC);


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
        return new ItemStack(VEBlocks.HYDROPONIC_INCUBATOR.block().get());
    }
}
