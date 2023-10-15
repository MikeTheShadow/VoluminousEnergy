package com.veteam.voluminousenergy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.veteam.voluminousenergy.blocks.blocks.VEBlocks;
import com.veteam.voluminousenergy.util.recipe.IngredientSerializerHelper;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipeCodecs;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class IndustrialBlastingRecipe extends VEFluidRecipe {

    public static final RecipeType<IndustrialBlastingRecipe> RECIPE_TYPE = VERecipes.VERecipeTypes.INDUSTRIAL_BLASTING.get();
    private static final RecipeSerializer<IndustrialBlastingRecipe> SERIALIZER = new RecipeSerializer<>() {

        public static final Codec<IndustrialBlastingRecipe> VE_RECIPE_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                VERecipeCodecs.VE_LAZY_INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter((getter) -> getter.registryIngredients),
                CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.listOf().fieldOf("item_results").forGetter((getter) -> getter.results),
                Codec.INT.fieldOf("process_time").forGetter((getter) -> getter.processTime),
                Codec.INT.fieldOf("minimum_heat_kelvin").forGetter(IndustrialBlastingRecipe::getMinimumHeat)
        ).apply(instance, IndustrialBlastingRecipe::new));

        private static final IngredientSerializerHelper<IndustrialBlastingRecipe> helper = new IngredientSerializerHelper<>();

        @Nullable
        @Override
        public IndustrialBlastingRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
            IndustrialBlastingRecipe recipe = new IndustrialBlastingRecipe();
            recipe.setMinimumHeat(buffer.readInt());
            return helper.fromNetwork(recipe, buffer);
        }

        @Override
        public @NotNull Codec<IndustrialBlastingRecipe> codec() {
            return VE_RECIPE_CODEC;
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull IndustrialBlastingRecipe recipe) {
            buffer.writeInt(recipe.getMinimumHeat());
            helper.toNetwork(buffer, recipe);
        }
    };

    private int minimumHeat;
    private static List<Fluid> hotEnoughFluids;

    public IndustrialBlastingRecipe() {

    }

    public IndustrialBlastingRecipe(List<VERecipeCodecs.RegistryIngredient> i, List<ItemStack> oi, int processTime, int minimumHeat) {
        super(i,List.of(),List.of(),oi,processTime);
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
        return new ItemStack(VEBlocks.BLAST_FURNACE_BLOCK.get());
    }

    public void setMinimumHeat(int minimumHeat) {
        this.minimumHeat = minimumHeat;
    }

    // Note: Temperature units are in Kelvin (K)
    public int getMinimumHeat() {
        return minimumHeat;
    }

    public List<Fluid> getHotEnoughFluids() {
        HashSet<Fluid> fluidHashSet = new HashSet<>();
        if(hotEnoughFluids == null) {
            for(Fluid fluid : ForgeRegistries.FLUIDS.getValues()) {
                if(fluid.getFluidType().getTemperature() > minimumHeat)
                    fluidHashSet.add(fluid);
            }
            hotEnoughFluids = fluidHashSet.stream().toList();
        }
        return hotEnoughFluids;
    }

    public boolean isFluidHotEnough(Fluid fluid) {
        return isFluidHotEnough(fluid.getFluidType());
    }

    public boolean isFluidHotEnough(FluidType fluidType) {
        return fluidType.getTemperature() > this.minimumHeat;
    }

}
