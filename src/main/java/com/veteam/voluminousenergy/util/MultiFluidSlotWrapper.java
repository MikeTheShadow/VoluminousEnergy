package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.VERecipe;
import com.veteam.voluminousenergy.recipe.processor.BasicProcessor;
import com.veteam.voluminousenergy.tools.Config;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
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

        if (tank < 0 || tank >= tanks.size())
            return FluidStack.EMPTY;
        return tanks.get(tank).getTank().getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        if (tank < 0 || tank >= tanks.size())
            return 0;
        return this.tanks.get(tank).getTank().getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        VERelationalTank relationalTank = tanks.get(tank);
        if (relationalTank.isAllowAny())
            return true;
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
        return relationalTank.getTank().isFluidValid(stack);
    }

    @Override
    public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
        for (int tank = 0; tank < tanks.size(); tank++) {
            if (canFill(tank, resource)) {
                return fill(tank, resource, action);
            }
        }
        return 0;
    }

    /**
     * Fills only the given tank, following the same rules as {@link #fill(FluidStack, FluidAction)}.
     */
    public int fill(int tank, FluidStack resource, FluidAction action) {
        if (!canFill(tank, resource)) {
            return 0;
        }
        markRecipeDirty();
        return tanks.get(tank).getTank().fill(resource.copy(), action);
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        for (int tank = 0; tank < tanks.size(); tank++) {
            if (canDrain(tank, resource)) {
                return drain(tank, resource, action);
            }
        }
        return FluidStack.EMPTY;
    }

    /**
     * Drains only the given tank, following the same rules as {@link #drain(FluidStack, FluidAction)}.
     */
    public FluidStack drain(int tank, FluidStack resource, FluidAction action) {
        if (!canDrain(tank, resource)) {
            return FluidStack.EMPTY;
        }
        markRecipeDirty();
        return tanks.get(tank).getTank().drain(resource.copy(), action);
    }

    /**
     * Replaces the contents of the given tank without any checks, for restoring a snapshot.
     */
    public void setFluidInTank(int tank, FluidStack stack) {
        tanks.get(tank).getTank().setFluid(stack);
    }

    private boolean canFill(int tank, FluidStack resource) {
        if (tank < 0 || tank >= tanks.size()) {
            return false;
        }
        VERelationalTank relationalTank = tanks.get(tank);
        if (relationalTank.getTankType() == TankType.OUTPUT || !isFluidValid(tank, resource)) {
            return false;
        }
        return relationalTank.getTank().isEmpty() || resource.is(relationalTank.getTank().getFluid().getFluid());
    }

    private boolean canDrain(int tank, FluidStack resource) {
        if (resource.isEmpty() || tank < 0 || tank >= tanks.size()) {
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
        return resource.is(relationalTank.getTank().getFluid().getFluid());
    }

    private void markRecipeDirty() {
        if (tileEntity.getRecipeProcessor() instanceof BasicProcessor basicProcessor) {
            basicProcessor.markRecipeDirty();
        }
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        for (VERelationalTank tank : tanks) {
            if (!Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.get()) {
                if (tank.getTankType() != TankType.OUTPUT && tank.getTankType() != TankType.BOTH)
                    continue;
            }
            if (tank.getTank().getFluidAmount() > 0) {
                if (tank.getTank().getFluid().getAmount() != tank.getTank().getCapacity())
                    if (tileEntity.getRecipeProcessor() instanceof BasicProcessor basicProcessor) {
                        basicProcessor.markRecipeDirty();
                    }
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
