package com.veteam.voluminousenergy.blocks.blocks.util;

import com.veteam.voluminousenergy.recipe.RecipeCache;
import com.veteam.voluminousenergy.recipe.VEFluidRecipe;
import com.veteam.voluminousenergy.tools.sidemanager.VESlotManager;
import com.veteam.voluminousenergy.util.RelationalTank;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VEItemStackWithFluidHandler extends ItemStackHandler implements IFluidHandler {

    private final VESlotManager[] managers;
    private final RelationalTank[] relationalTanks;
    private final Class<?> recipeType;
    private VEFluidRecipe recipe;

    private int[] recipeIndex;

    public VEItemStackWithFluidHandler(int size, Class<?> recipeType, List<RelationalTank> tankList, VESlotManager... managers) {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        this.managers = managers;
        this.recipeType = recipeType;
        this.relationalTanks = tankList.toArray(RelationalTank[]::new);
        recipeIndex = new int[relationalTanks.length + managers.length];
    }

    public VESlotManager[] getManagers() {
        return this.managers;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public int getTanks() {
        return 0;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return relationalTanks[tank].getTank().getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return relationalTanks[tank].getTank().getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {

//        RecipeCache.recipeHasItem()

        return false;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return 0;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        return null;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        return null;
    }
}
