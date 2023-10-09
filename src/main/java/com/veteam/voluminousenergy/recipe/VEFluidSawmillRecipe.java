package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.tools.Config;
import com.veteam.voluminousenergy.util.recipe.FluidSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipeCodecs;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class VEFluidSawmillRecipe extends VEFluidRecipe {

    public static final RecipeType<VEFluidSawmillRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.SAWMILLING.get();
    private boolean isLogRecipe;

    public VEFluidSawmillRecipe() {

    }

    public VEFluidSawmillRecipe(List<VERecipeCodecs.RegistryIngredient> i, List<FluidStack> of, List<ItemStack> oi, int processTime, boolean isLogRecipe) {
        super(i,List.of(),of,oi,processTime);
        this.isLogRecipe = isLogRecipe;
    }

    private static final RecipeSerializer<VEFluidSawmillRecipe> SERIALIZER = new RecipeSerializer<>() {

        public static final Codec<VEFluidSawmillRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                VERecipeCodecs.VE_LAZY_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.registryIngredients),
                VERecipeCodecs.VE_OUTPUT_FLUID_CODEC.listOf().fieldOf("fluid_results").forGetter((getter) -> getter.fluidOutputList),
                CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.results),
                Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime),
                Codec.BOOL.fieldOf("is_log_recipe").forGetter(VEFluidSawmillRecipe::isLogRecipe)
        ).apply(instance, VEFluidSawmillRecipe::new));

        private static final FluidSerializerHelper<VEFluidSawmillRecipe> helper = new FluidSerializerHelper<>();

        @Nullable
        @Override
        public VEFluidSawmillRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
            return helper.fromNetwork(new VEFluidSawmillRecipe(), buffer);
        }

        @Override
        public @NotNull Codec<VEFluidSawmillRecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull VEFluidSawmillRecipe recipe) {
            helper.toNetwork(buffer, recipe);
        }
    };

    @Override
    public @NotNull RecipeSerializer<? extends VERecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<?>> getType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull ItemStack getToastSymbol(){
        return new ItemStack(VEBlocks.SAWMILL_BLOCK.get());
    }

    public boolean isLogRecipe() {
        return isLogRecipe;
    }

    @Override
    public List<FluidStack> getOutputFluids() {
        if (this.isLogRecipe && Config.SAWMILL_ALLOW_NON_SAWMILL_RECIPE_LOGS_TO_BE_SAWED.get()) {
            ResourceLocation fluidLocation = new ResourceLocation(Config.SAWMILL_FLUID_LOCATION.get());
            Fluid outputFluid = ForgeRegistries.FLUIDS.getValue(fluidLocation);
            return List.of(new FluidStack(outputFluid, Config.SAWMILL_FLUID_AMOUNT.get()));
        }
        return super.getOutputFluids();
    }

    @Override
    public FluidStack getOutputFluid(int slot) {
        if (this.isLogRecipe && Config.SAWMILL_ALLOW_NON_SAWMILL_RECIPE_LOGS_TO_BE_SAWED.get()) {
            return this.getOutputFluid(0);
        }
        return super.getOutputFluid(slot);
    }

}
