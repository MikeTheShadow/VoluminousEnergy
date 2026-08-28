package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.parser.BasicParser;
import com.veteam.voluminousenergy.recipe.parser.SawmillParser;
import com.veteam.voluminousenergy.recipe.serializer.FluidSerializerHelper;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SawmillRecipe extends VERecipe {

    public static final RecipeType<VERecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.SAWMILLING.get();
    private boolean isLogRecipe;

    private final BasicParser parser =
            new SawmillParser(this)
                    .addIngredient(0,0)
                    .addFluidResult(0,0)
                    .addItemResult(1,0)
                    .addItemResult(2,1);

    public SawmillRecipe() {

    }

    public SawmillRecipe(List<VERecipeCodecs.RegistryIngredient> i, List<net.neoforged.neoforge.fluids.FluidStackTemplate> of, List<net.minecraft.world.item.ItemStackTemplate> oi, int processTime, boolean isLogRecipe) {
        super(i, List.of(), of, oi, processTime);
        this.isLogRecipe = isLogRecipe;
    }

    public static final MapCodec<SawmillRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            VERecipeCodecs.VE_LAZY_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.registryIngredients),
            VERecipeCodecs.VE_OUTPUT_FLUID_CODEC.listOf().fieldOf("fluid_results").forGetter((getter) -> getter.fluidOutputTemplates),
            VERecipeCodecs.VE_OUTPUT_ITEM_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.resultTemplates),
            Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime),
            Codec.BOOL.fieldOf("is_log_recipe").forGetter(SawmillRecipe::isLogRecipe)
    ).apply(instance, SawmillRecipe::new));

    private static final FluidSerializerHelper<SawmillRecipe> helper = new FluidSerializerHelper<>();

    public static final StreamCodec<RegistryFriendlyByteBuf, SawmillRecipe> VE_RECIPE_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull SawmillRecipe recipe) {
            buf.writeBoolean(recipe.isLogRecipe());
            helper.toNetwork(buf, recipe);
        }

        @Override
        @NotNull
        public SawmillRecipe decode(@NotNull RegistryFriendlyByteBuf buffer) {
            SawmillRecipe recipe = new SawmillRecipe();
            recipe.setLogRecipe(buffer.readBoolean());
            return helper.fromNetwork(recipe, buffer);
        }
    };

    private static final RecipeSerializer<SawmillRecipe> SERIALIZER = new RecipeSerializer<>(VE_RECIPE_CODEC, VE_RECIPE_STREAM_CODEC);

    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<RecipeInput>> getType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.SAWMILL.block().get());
    }

    public boolean isLogRecipe() {
        return isLogRecipe;
    }

    public void setLogRecipe(boolean logRecipe) {
        isLogRecipe = logRecipe;
    }

    @Override
    public List<FluidStack> getOutputFluids() {
        if (this.isLogRecipe && Config.SAWMILL_ALLOW_NON_SAWMILL_RECIPE_LOGS_TO_BE_SAWED.get()) {
            Identifier fluidLocation = Identifier.parse(Config.SAWMILL_FLUID_LOCATION.get());
            Fluid outputFluid = BuiltInRegistries.FLUID.getValue(fluidLocation);
            return List.of(new FluidStack(outputFluid, Config.SAWMILL_FLUID_AMOUNT.get()));
        }
        return super.getOutputFluids();
    }

    @Override
    public BasicParser getParser() {
        return parser;
    }

}
