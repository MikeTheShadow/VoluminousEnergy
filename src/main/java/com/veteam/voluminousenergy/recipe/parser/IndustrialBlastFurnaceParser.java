package com.veteam.voluminousenergy.recipe.parser;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.IndustrialBlastingRecipe;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class IndustrialBlastFurnaceParser extends BasicParser {


    private IndustrialBlastingRecipe blastingRecipe;

    public IndustrialBlastFurnaceParser(IndustrialBlastingRecipe recipe) {
        super(recipe);
        this.blastingRecipe = recipe;
    }

    @Override
    public boolean isCompleteRecipe(VETileEntity tile) {
        FluidStack tankFluid = tile.getFluidStackFromTank(0);
        if(tankFluid.getFluid().getFluidType().getTemperature() < blastingRecipe.getMinimumHeat()) {
            return false;
        }
        return super.isCompleteRecipe(tile);
    }

    @Override
    public void completeRecipe(VETileEntity tile) {
        tile.getRelationalTank(0).getTank().drain(250, IFluidHandler.FluidAction.EXECUTE);
        super.completeRecipe(tile);
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack) {
        if (slot == 0 || slot == 1) {
            return stack.getItem() instanceof BucketItem;
        }
        return super.canInsertItem(slot, stack);
    }
}
