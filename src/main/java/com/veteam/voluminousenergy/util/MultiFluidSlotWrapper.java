package com.veteam.voluminousenergy.util;

import com.google.common.base.Preconditions;
import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import com.veteam.voluminousenergy.tools.Config;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;

public class MultiFluidSlotWrapper implements IFluidHandler {

    HashMap<Integer, RelationalTank> tankHashMap = new HashMap<>();
    List<RelationalTank> tanks;
    VETileEntity tileEntity;

    public MultiFluidSlotWrapper(List<RelationalTank> tanks) {
        Preconditions.checkArgument(!tanks.isEmpty(), "You need to have at least one slot defined!");
        this.tanks = tanks;
        tanks.forEach(m -> tankHashMap.put(m.getSlotNum(), m));
    }

    @Override
    public int getTanks() {
        return tanks.size();
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tankHashMap.containsKey(tank)) {
            RelationalTank fluidTank = tankHashMap.get(tank);
            return fluidTank.getTank() == null ? FluidStack.EMPTY : fluidTank.getTank().getFluid();
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        if (tankHashMap.containsKey(tank)) {
            RelationalTank fluidTank = tankHashMap.get(tank);
            return fluidTank.getTank() == null ? 0 : fluidTank.getTank().getCapacity();
        }
        return 0;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        RelationalTank relationalTank = tankHashMap.get(tank);
        if (relationalTank == null) return false;
        return relationalTank.getTank() != null && relationalTank.getTank().isFluidValid(stack);
    }


    @Override
    public int fill(FluidStack resource, FluidAction action) {
        for(RelationalTank tank : tanks) {
            if(tank.getTankType() == TankType.OUTPUT) continue;
            if (isFluidValid(tank.getSlotNum(), resource) && (tank.getTank().isEmpty() || resource.isFluidEqual(tank.getTank().getFluid()))) {
                tileEntity.markRecipeDirty();
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
        for(RelationalTank tank : tanks) {
            if(tank.getTankType() == TankType.INPUT && !Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.get() && !tank.isAllowAny()) continue;
            if (resource.isFluidEqual(tank.getTank().getFluid())) {
                tileEntity.markRecipeDirty();
                return tank.getTank().drain(resource.copy(), action);
            }
        }
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        for(RelationalTank tank : tanks) {
            if(tank.getTankType() == TankType.INPUT && !Config.ALLOW_EXTRACTION_FROM_INPUT_TANKS.get() && !tank.isAllowAny()) continue;
            if (tank.getTank().getFluidAmount() > 0) {
                tileEntity.markRecipeDirty();
                return tank.getTank().drain(maxDrain, action);
            }
        }
        return FluidStack.EMPTY;
    }
}