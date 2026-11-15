package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.recipe.parser.BasicParser;
import com.veteam.voluminousenergy.recipe.parser.IndustrialBlastFurnaceParser;
import com.veteam.voluminousenergy.recipe.serializer.IngredientSerializerHelper;
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
            Codec.INT.fieldOf("minimum_heat_kelvin").forGetter(IndustrialBlastingRecipe::getMinimumHeat),
            Codec.intRange(0, 4000).optionalFieldOf("heat_source_consumption", 0).forGetter(IndustrialBlastingRecipe::getHeatSourceConsumption)
    ).apply(instance, IndustrialBlastingRecipe::new));

    private static final IngredientSerializerHelper<IndustrialBlastingRecipe> helper = new IngredientSerializerHelper<>();

    public static final StreamCodec<RegistryFriendlyByteBuf, IndustrialBlastingRecipe> VE_RECIPE_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull IndustrialBlastingRecipe recipe) {
            buf.writeInt(recipe.getMinimumHeat());
            buf.writeInt(recipe.getHeatSourceConsumption());
            helper.toNetwork(buf, recipe);
        }

        @Override
        @NotNull
        public IndustrialBlastingRecipe decode(@NotNull RegistryFriendlyByteBuf buffer) {
            IndustrialBlastingRecipe recipe = new IndustrialBlastingRecipe();
            recipe.setMinimumHeat(buffer.readInt());
            recipe.setHeatSourceConsumption(buffer.readInt());
            return helper.fromNetwork(recipe, buffer);
        }
    };

    private static final RecipeSerializer<IndustrialBlastingRecipe> SERIALIZER = new RecipeSerializer<>(VE_RECIPE_CODEC, VE_RECIPE_STREAM_CODEC);


    private int minimumHeat;
    private int heatSourceConsumption;
    private List<Fluid> hotEnoughFluids;

    public IndustrialBlastingRecipe() {

    }

    public IndustrialBlastingRecipe(List<VERecipeCodecs.RegistryIngredient> i, List<ItemStackTemplate> oi, int processTime, int minimumHeat, int heatSourceConsumption) {
        super(i, List.of(), List.of(), oi, processTime);
        this.minimumHeat = minimumHeat;
        this.heatSourceConsumption = heatSourceConsumption;
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

    public void setHeatSourceConsumption(int heatSourceConsumption) {
        this.heatSourceConsumption = heatSourceConsumption;
    }

    /**
     * Returns the mB of heat-source fluid one operation drains. At 0 the recipe needs a hot-enough
     * fluid in the tank but consumes none of it.
     */
    public int getHeatSourceConsumption() {
        return heatSourceConsumption;
    }

    public List<Fluid> getHotEnoughFluids() {
        if (hotEnoughFluids == null) {
            hotEnoughFluids = BuiltInRegistries.FLUID.stream().filter(this::isFluidHotEnough).toList();
        }
        return hotEnoughFluids;
    }

    /**
     * Returns the hot-enough fluids as stacks of this recipe's consumption, or of 1 mB when it consumes
     * none, since a 0 mB stack is empty and would match no fluid.
     */
    public List<FluidStack> getHeatSourceStacks() {
        int stackAmount = Math.max(heatSourceConsumption, 1);
        return getHotEnoughFluids().stream().map(fluid -> new FluidStack(fluid, stackAmount)).toList();
    }

    @Override
    public List<FluidIngredient> getFluidIngredients() {
        return List.of(FluidIngredient.of(getHeatSourceStacks().stream()));
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
