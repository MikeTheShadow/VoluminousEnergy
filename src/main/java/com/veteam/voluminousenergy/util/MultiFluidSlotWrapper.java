package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.tools.Config;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class MultiFluidSlotWrapper implements IFluidHandler {
    List<VERelationalTank> tanks;
    VETileEntity tileEntity;

    public MultiFluidSlotWrapper(List<VERelationalTank> tanks, VETileEntity tileEntity) {
        this.tanks = tanks;
        this.tileEntity = tileEntity;
    }

    @Override
    public int getTanks() {
        return tanks.size();
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {

        if(tank < 0 || tank >= tanks.size()) return FluidStack.EMPTY;
        return tanks.get(tank).getTank().getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        if(tank < 0 || tank >= tanks.size()) return 0;
        return this.tanks.get(tank).getTank().getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        VERelationalTank relationalTank = tanks.get(tank);
        if (relationalTank.isAllowAny() ||
                (relationalTank.getValidator() != null && relationalTank.getValidator().validateFluid(stack,tileEntity))) return true;
        for (VERecipe recipe : tileEntity.getPotentialRecipes()) {
            if (recipe.getFluidIngredient(relationalTank.getRecipePos()).test(stack)) {
                return true;
            }
        }
        return relationalTank.getTank().isFluidValid(stack);
    }


    @Override
    public int fill(FluidStack resource, FluidAction action) {
        for (VERelationalTank tank : tanks) {
            if (tank.getTankType() == TankType.OUTPUT) continue;
            if (isFluidValid(tank.getSlotNum(), resource) && (tank.getTank().isEmpty() || resource.isFluidEqual(tank.getTank().getFluid()))) {
                if (tank.getTank().getFluid().getAmount() != tank.getTank().getCapacity()) tileEntity.markRecipeDirty();
                return tank.getTank().fill(resource.copy(), action);
            }
        }
        return 0;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) {
            return FluidStack.EMPTY;
        }
        for (VERelationalTank tank : tanks) {
            if (!tank.getSideStatus() && !tank.isIgnoreDirection()) continue;
            if (!Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.get()) {
                if (tank.getTankType() != TankType.OUTPUT && tank.getTankType() != TankType.BOTH) continue;
            }
            if (resource.isFluidEqual(tank.getTank().getFluid())) {
                if (tank.getTank().getFluid().getAmount() != tank.getTank().getCapacity()) tileEntity.markRecipeDirty();
                return tank.getTank().drain(resource.copy(), action);
            }
        }
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        for (VERelationalTank tank : tanks) {
            if (!Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.get()) {
                if (tank.getTankType() != TankType.OUTPUT && tank.getTankType() != TankType.BOTH) continue;
            }
            if (tank.getTank().getFluidAmount() > 0) {
                if (tank.getTank().getFluid().getAmount() != tank.getTank().getCapacity()) tileEntity.markRecipeDirty();
                return tank.getTank().drain(maxDrain, action);
            }
        }
        return FluidStack.EMPTY;
    }

    public void addRelationalTank(VERelationalTank tank) {
        tanks.add(tank);
    }

    public void removeRelationalTank(VERelationalTank tank) {
        tanks.remove(tank);
    }
}