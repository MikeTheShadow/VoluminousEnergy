package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.processor.BasicProcessor;
import com.veteam.voluminousenergy.tools.Config;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.ArrayList;
import java.util.List;

/**
 * The view of a machine's tanks from one side. Output tanks refuse filling, and only enabled tanks
 * can be drained, input tanks only when {@code Allow Extraction from Input Tanks} is on. A transfer
 * without a tank index spreads over every tank that accepts it.
 */
public class MultiFluidSlotWrapper implements ResourceHandler<FluidResource> {
    List<VERelationalTank> tanks;
    VETileEntity tileEntity;

    public MultiFluidSlotWrapper(List<VERelationalTank> tanks, VETileEntity tileEntity) {
        this.tanks = tanks;
        this.tileEntity = tileEntity;
    }

    @Override
    public int size() {
        return tanks.size();
    }

    @Override
    public FluidResource getResource(int tank) {
        if (tank < 0 || tank >= tanks.size())
            return FluidResource.EMPTY;
        return tanks.get(tank).getTank().getResource(0);
    }

    @Override
    public long getAmountAsLong(int tank) {
        if (tank < 0 || tank >= tanks.size())
            return 0;
        return tanks.get(tank).getTank().getFluidAmount();
    }

    @Override
    public long getCapacityAsLong(int tank, FluidResource resource) {
        if (tank < 0 || tank >= tanks.size())
            return 0;
        return this.tanks.get(tank).getTank().getCapacity();
    }

    @Override
    public boolean isValid(int tank, FluidResource resource) {
        if (tank < 0 || tank >= tanks.size())
            return false;
        VERelationalTank relationalTank = tanks.get(tank);
        if (relationalTank.isAllowAny())
            return true;
        FluidStack stack = resource.toStack(1);
        if (relationalTank.getValidator() != null)
            return relationalTank.getValidator().validateFluid(stack, tileEntity);

        List<VERecipe> recipes = new ArrayList<>();
        if (tileEntity.getRecipeProcessor() instanceof BasicProcessor basicProcessor) {
            recipes = basicProcessor.getPotentialRecipes();
        }

        for (VERecipe recipe : recipes) {
            if (recipe.getFluidIngredient(relationalTank.getRecipePos()).test(stack)) {
                return true;
            }
        }
        return relationalTank.getTank().isValid(0, resource);
    }

    @Override
    public int insert(int tank, FluidResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if (!canFill(tank, resource)) {
            return 0;
        }
        markRecipeDirty();
        return tanks.get(tank).getTank().insert(0, resource, amount, transaction);
    }

    @Override
    public int extract(int tank, FluidResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if (!canDrain(tank, resource)) {
            return 0;
        }
        markRecipeDirty();
        return tanks.get(tank).getTank().extract(0, resource, amount, transaction);
    }

    private boolean canFill(int tank, FluidResource resource) {
        if (tank < 0 || tank >= tanks.size()) {
            return false;
        }
        VERelationalTank relationalTank = tanks.get(tank);
        if (relationalTank.getTankType() == TankType.OUTPUT || !isValid(tank, resource)) {
            return false;
        }
        VEFluidTank fluidTank = relationalTank.getTank();
        return fluidTank.isEmpty() || resource.getFluid() == fluidTank.getResource(0).getFluid();
    }

    private boolean canDrain(int tank, FluidResource resource) {
        if (tank < 0 || tank >= tanks.size()) {
            return false;
        }
        VERelationalTank relationalTank = tanks.get(tank);
        if (!relationalTank.getSideStatus() && !relationalTank.isIgnoreDirection()) {
            return false;
        }
        if (!Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.get()
                && relationalTank.getTankType() != TankType.OUTPUT && relationalTank.getTankType() != TankType.BOTH) {
            return false;
        }
        return resource.getFluid() == relationalTank.getTank().getResource(0).getFluid();
    }

    private void markRecipeDirty() {
        if (tileEntity.getRecipeProcessor() instanceof BasicProcessor basicProcessor) {
            basicProcessor.markRecipeDirty();
        }
    }

    public void addRelationalTank(VERelationalTank tank) {
        tanks.add(tank);
    }

    public void removeRelationalTank(VERelationalTank tank) {
        tanks.remove(tank);
    }
}
