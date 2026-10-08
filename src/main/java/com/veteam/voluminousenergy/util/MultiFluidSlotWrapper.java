package com.veteam.voluminousenergy.util;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.recipe.processor.BasicProcessor;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public class MultiFluidSlotWrapper implements IFluidHandler {
    List<VERelationalTank> tanks;
    VETileEntity tileEntity;
    private final Direction face;

    public MultiFluidSlotWrapper(List<VERelationalTank> tanks, VETileEntity tileEntity, Direction face) {
        this.tanks = tanks;
        this.tileEntity = tileEntity;
        this.face = face;
    }

    @Override
    public int getTanks() {
        return tanks.size();
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {

        if (tank < 0 || tank >= tanks.size() || !isAccessible(tanks.get(tank))) {
            return FluidStack.EMPTY;
        }
        return tanks.get(tank).getTank().getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        if (tank < 0 || tank >= tanks.size() || !isAccessible(tanks.get(tank))) {
            return 0;
        }
        return this.tanks.get(tank).getTank().getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return tank >= 0 && tank < tanks.size() && isAccessible(tanks.get(tank)) && tanks.get(tank).canPull()
                && tileEntity.isFluidValidForTank(tanks.get(tank), stack);
    }

    @Override
    public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
        for (int tankIndex = 0; tankIndex < tanks.size(); tankIndex++) {
            VERelationalTank tank = tanks.get(tankIndex);
            if (!isAccessible(tank) || tank.getTankType() == TankType.OUTPUT) {
                continue;
            }

            if (isFluidValid(tankIndex, resource) && (tank.getTank().isEmpty() || resource.is(tank.getTank().getFluid().getFluid()))) {
                if (action.execute() && tileEntity.getRecipeProcessor() instanceof BasicProcessor basicProcessor) {
                    basicProcessor.markRecipeDirty();
                }
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
            if (!isAccessible(tank)) {
                continue;
            }
            if (!Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.get()) {
                if (tank.getTankType() != TankType.OUTPUT && tank.getTankType() != TankType.BOTH) {
                    continue;
                }
            }
            if (FluidStack.isSameFluidSameComponents(resource, tank.getTank().getFluid())) {
                if (action.execute() && tileEntity.getRecipeProcessor() instanceof BasicProcessor basicProcessor) {
                    basicProcessor.markRecipeDirty();
                }
                return tank.getTank().drain(resource.copy(), action);
            }
        }
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        for (VERelationalTank tank : tanks) {
            if (!isAccessible(tank)) {
                continue;
            }
            if (!Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.get()) {
                if (tank.getTankType() != TankType.OUTPUT && tank.getTankType() != TankType.BOTH) {
                    continue;
                }
            }
            if (tank.getTank().getFluidAmount() > 0) {
                if (action.execute() && tileEntity.getRecipeProcessor() instanceof BasicProcessor basicProcessor) {
                    basicProcessor.markRecipeDirty();
                }
                return tank.getTank().drain(maxDrain, action);
            }
        }
        return FluidStack.EMPTY;
    }

    private boolean isAccessible(VERelationalTank tank) {
        return tank.getDirection() == face;
    }

    public void addRelationalTank(VERelationalTank tank) {
        tanks.add(tank);
    }

    public void removeRelationalTank(VERelationalTank tank) {
        tanks.remove(tank);
    }
}
