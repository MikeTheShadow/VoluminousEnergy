package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.parser.BasicParser;
import com.veteam.voluminousenergy.recipe.parser.IndustrialBlastFurnaceParser;
import com.veteam.voluminousenergy.recipe.serializer.IngredientSerializerHelper;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.recipe.FluidIngredient;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IndustrialBlastingRecipe extends VERecipe {

    public static final RecipeType<VERecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.INDUSTRIAL_BLASTING.get();

    private final BasicParser parser = new IndustrialBlastFurnaceParser(this)
            .addIngredient(2, 0)
            .addIngredient(3,1)
            .addItemResult(4, 0);

    public static final MapCodec<IndustrialBlastingRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            VERecipeCodecs.VE_LAZY_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.registryIngredients),
            VERecipeCodecs.VE_OUTPUT_ITEM_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.resultTemplates),
            Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime),
            Codec.INT.fieldOf("minimum_heat_kelvin").forGetter(IndustrialBlastingRecipe::getMinimumHeat)
    ).apply(instance, IndustrialBlastingRecipe::new));

    private static final IngredientSerializerHelper<IndustrialBlastingRecipe> helper = new IngredientSerializerHelper<>();

    public static final StreamCodec<RegistryFriendlyByteBuf, IndustrialBlastingRecipe> VE_RECIPE_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull IndustrialBlastingRecipe recipe) {
            buf.writeInt(recipe.getMinimumHeat());
            helper.toNetwork(buf, recipe);
        }

        @Override
        @NotNull
        public IndustrialBlastingRecipe decode(@NotNull RegistryFriendlyByteBuf buffer) {
            IndustrialBlastingRecipe recipe = new IndustrialBlastingRecipe();
            recipe.setMinimumHeat(buffer.readInt());
            return helper.fromNetwork(recipe, buffer);
        }
    };

    private static final RecipeSerializer<IndustrialBlastingRecipe> SERIALIZER = new RecipeSerializer<>(VE_RECIPE_CODEC, VE_RECIPE_STREAM_CODEC);


    private int minimumHeat;
    private List<Fluid> hotEnoughFluids;

    public IndustrialBlastingRecipe() {

    }

    public IndustrialBlastingRecipe(List<VERecipeCodecs.RegistryIngredient> i, List<ItemStackTemplate> oi, int processTime, int minimumHeat) {
        super(i, List.of(), List.of(), oi, processTime);
        this.minimumHeat = minimumHeat;
    }

    @Override
    public @NotNull RecipeType<? extends VERecipe> getType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(VEBlocks.BLAST_FURNACE.block().get());
    }

    public void setMinimumHeat(int minimumHeat) {
        this.minimumHeat = minimumHeat;
    }

    // Note: Temperature units are in Kelvin (K)
    public int getMinimumHeat() {
        return minimumHeat;
    }

    public List<Fluid> getHotEnoughFluids() {
        if (hotEnoughFluids == null) {
            hotEnoughFluids = BuiltInRegistries.FLUID.stream().filter(this::isFluidHotEnough).toList();
        }
        return hotEnoughFluids;
    }

    @Override
    public List<FluidIngredient> getFluidIngredients() {
        int consumption = Config.BLAST_FURNACE_HEAT_SOURCE_CONSUMPTION.get();
        return List.of(FluidIngredient.of(getHotEnoughFluids().stream().map(fluid -> new FluidStack(fluid, consumption))));
    }

    @Override
    public BasicParser getParser() {
        return parser;
    }

    public boolean isFluidHotEnough(Fluid fluid) {
        return isFluidHotEnough(fluid.getFluidType());
    }

    public boolean isFluidHotEnough(FluidType fluidType) {
        return fluidType.getTemperature() >= this.minimumHeat;
    }

}
