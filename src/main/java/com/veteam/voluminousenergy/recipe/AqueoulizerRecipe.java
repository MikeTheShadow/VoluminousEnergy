package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.parser.RecipeParser;
import com.veteam.voluminousenergy.recipe.serializer.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class AqueoulizerRecipe extends VERecipe {

    public static final RecipeType<VERecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.AQUEOULIZING.get();

    private final RecipeParser parser;

    public AqueoulizerRecipe() {
        parser = RecipeParser.forRecipe(this)
                .addIngredient(new RecipeParser.SlotAndRecipePos(4, 0))
                .addFluidIngredient(new RecipeParser.SlotAndRecipePos(0, 0))
                .addFluidResult(new RecipeParser.SlotAndRecipePos(1, 0));
    }

    public AqueoulizerRecipe(List<VERecipeCodecs.RegistryIngredient> i, List<VERecipeCodecs.RegistryFluidIngredient> fi, List<FluidStack> of, int processTime) {
        super(i, fi, of, List.of(), processTime);
        parser = RecipeParser.forRecipe(this)
                .addIngredient(new RecipeParser.SlotAndRecipePos(4, 0))
                .addFluidIngredient(new RecipeParser.SlotAndRecipePos(0, 0))
                .addFluidResult(new RecipeParser.SlotAndRecipePos(1, 0));
    }

    public static final RecipeSerializer<AqueoulizerRecipe> SERIALIZER = new RecipeSerializer<>() {

        public static final Codec<AqueoulizerRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                VERecipeCodecs.VE_LAZY_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.registryIngredients),
                VERecipeCodecs.VE_FLUID_INGREDIENT_CODEC.listOf().fieldOf("fluid_ingredients").forGetter((getter) -> getter.registryFluidIngredients),
                VERecipeCodecs.VE_OUTPUT_FLUID_CODEC.listOf().fieldOf("fluid_results").forGetter((getter) -> getter.fluidOutputList),
                Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime)
        ).apply(instance, AqueoulizerRecipe::new));

        private static final FluidSerializerHelper helper = new FluidSerializerHelper();

        @Nullable
        @Override
        public AqueoulizerRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
            return (AqueoulizerRecipe) helper.fromNetwork(new AqueoulizerRecipe(), buffer);
        }

        @Override
        public @NotNull Codec<AqueoulizerRecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull AqueoulizerRecipe recipe) {
            helper.toNetwork(buffer, recipe);
        }
    };


    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<VERecipe> getType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.AQUEOULIZER_BLOCK.get());
    }

    @Override
    public RecipeParser getParser() {
        return parser;
    }
}


